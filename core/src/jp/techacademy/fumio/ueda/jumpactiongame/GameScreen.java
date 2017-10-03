package jp.techacademy.fumio.ueda.jumpactiongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Fumio on 2017/10/01.
 */

public class GameScreen extends ScreenAdapter {
    //カメラのサイズを表す定数を定義する
    static final float CAMERA_WIDTH = 10;
    static final float CAMERA_HEIGHT = 15;
    //ゲーム世界の広さを定義
    static final float WORLD_WIDTH = 10;
    static final float WORLD_HEIGHT = 15 * 20; // 20画面分登れば終了
    static final float GUI_WIDTH = 320;//GUI用カメラのサイズ
    static final float GUI_HEIGHT = 480;//GUI用カメラのサイズ

//ゲーム開始前、中、ゲーム終了を表す定数の定義
    static final int GAME_STATE_READY = 0;
    static final int GAME_STATE_PLAYING = 1;
    static final int GAME_STATE_GAMEOVER = 2;

    // 重力
    static final float GRAVITY = -12;

    private JumpActionGame mGame;

    Sprite mBg;

//カメラクラスとビューポートクラスをメンバ変数として定義
    OrthographicCamera mCamera;
    OrthographicCamera mGuiCamera;

    FitViewport mViewPort;
    FitViewport mGuiViewPort;

    Random mRandom;//乱数を取得するためのクラス
    List<Step> mSteps;//生成して配置した踏み台を保持するリスト
    List<Star> mStars;//生成して配置した星を保持するためのリスト
    List<DarkStar> mDarkStars;//生成して配置した偽星を保持するためのリスト
    Ufo mUfo;//生成して配置したゴールUFOを保持する
    Player mPlayer;//生成して配置したプレーヤーを保持する

    float mHeightSoFar; //プレーヤーから地面までの距離を保持
    int mGameState;//ゲームの状態を保持
    Vector3 mTouchPoint; // タッチされた座標を保持するメンバ変数
    BitmapFont mFont; // Bitmapフォントの使用
    int mScore; // スコアを保持するメンバ変数
    int mHighScore; // ハイスコアを保持するメンバ変数
    Preferences mPrefs; // データを永続化させるためのPreferenceをメンバ変数に定義
    float h =1; //足場を踏むごとに早く動かすための変数

    public GameScreen(JumpActionGame game) {
        mGame = game;

        //背景の処理
        Texture bgTexture = new Texture("back.png");
        //TextureReionで切り出すときの原点は左上
        mBg = new Sprite(new TextureRegion(bgTexture, 0, 0, 540, 810));
        //画像の切り出し
        mBg.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        //カメラサイズに設定
        mBg.setPosition(0, 0);
        //左下基準０．０に描画

        // カメラ、ViewPortを生成、設定するメンバ変数に初期化して代入
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT);
        mViewPort = new FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT, mCamera);

        // GUI用のカメラを設定する
        mGuiCamera = new OrthographicCamera();
        mGuiCamera.setToOrtho(false, GUI_WIDTH, GUI_HEIGHT);
        mGuiViewPort = new FitViewport(GUI_WIDTH, GUI_HEIGHT, mGuiCamera);


        // メンバ変数の初期化
        mRandom = new Random();
        mSteps = new ArrayList<Step>();
        mStars = new ArrayList<Star>();
        mDarkStars = new ArrayList<DarkStar>();
        mGameState = GAME_STATE_READY;
        mTouchPoint = new Vector3();
        mFont = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false); // フォントファイルの読み込み
        mFont.getData().setScale(0.8f);// フォントサイズも指定
        mScore = 0;
        mHighScore = 0;

        // ハイスコアをPreferencesから取得する
        mPrefs = Gdx.app.getPreferences("jp.techacademy.fumio.ueda.jumpactiongame");//Preferencesの取得
        mHighScore = mPrefs.getInteger("HIGHSCORE", 0);//第2引数はキーに対応する値がなかった場合に返ってくる値（初期値）

        createStage();
        //オブジェクト配置するcreateStageメソッドを呼び出す
    }

    //描画を行うレンダーメソッド
    @Override
    public void render (float delta){
        // それぞれの状態をアップデートする
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        //赤、緑、青、透過の指定
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //指定した色で塗りつぶし

        // カメラの中心を超えたらカメラを上に移動させる つまりキャラが画面の上半分には絶対に行かない
        if (mPlayer.getY() > mCamera.position.y) {
            mCamera.position.y = mPlayer.getY();
        }

        //スプライトなどの描画はbeginとendの間で行う
        // カメラの座標をアップデート（計算）し、スプライトの表示に反映させる
        mCamera.update();
        mGame.batch.setProjectionMatrix(mCamera.combined);
        //↑はカメラの座標を計算しスプライト表示に反映させるのに必要

        mGame.batch.begin();
        // 原点は左下
        mBg.setPosition(mCamera.position.x - CAMERA_WIDTH / 2, mCamera.position.y - CAMERA_HEIGHT / 2);
        mBg.draw(mGame.batch);

        // Step,リストで保持しているので順番に取り出し
        for (int i = 0; i < mSteps.size(); i++) {
            mSteps.get(i).draw(mGame.batch);
        }

        // Star,リストで保持しているので順番に取り出し
        for (int i = 0; i < mStars.size(); i++) {
            mStars.get(i).draw(mGame.batch);
        }

        // DarkStar,リストで保持しているので順番に取り出し
        for (int i = 0; i < mDarkStars.size(); i++) {
            mDarkStars.get(i).draw(mGame.batch);
        }

        // UFO
        mUfo.draw(mGame.batch);

        //Player
        mPlayer.draw(mGame.batch);

        mGame.batch.end();

        // スコア表示
        mGuiCamera.update();
        mGame.batch.setProjectionMatrix(mGuiCamera.combined);
        //↑はカメラの座標を計算しスプライト表示に反映させるのに必要
        mGame.batch.begin();
        //drawメソッドで描画第1引数にSprteBatch、第2引数に表示されたい文字列、第3引数にx座標、第4引数にy座標
        mFont.draw(mGame.batch, "HighScore: " + mHighScore, 16, GUI_HEIGHT - 15);
        mFont.draw(mGame.batch, "Score: " + mScore, 16, GUI_HEIGHT - 35);
        mGame.batch.end();
    }

    //resizeメソッドをオーバーライドしてFitViewportクラスのupdateメソッドを呼び出す
    //このメソッドは物理的な画面のサイズが変更されたときに呼ばれる
    @Override
    public void resize(int width, int height) {
        mViewPort.update(width, height);
        mGuiViewPort.update(width, height);
    }

    // ステージを作成する、オブジェクトを配置するメソッド
    private void createStage() {

        // テクスチャの準備
        Texture stepTexture = new Texture("step.png");
        Texture starTexture = new Texture("star.png");
        Texture darkstarTexture = new Texture("darkstar.png");
        Texture playerTexture = new Texture("uma.png");
        Texture ufoTexture = new Texture("ufo.png");

        // StepとStarをゴールの高さまで配置していく
        float y = 0;

        float maxJumpHeight = Player.PLAYER_JUMP_VELOCITY * Player.PLAYER_JUMP_VELOCITY / (2 * -GRAVITY);
        //ゴール直前まで繰り返して生成
        while ( y < WORLD_HEIGHT - 5) {
            //ランダムで動く床
            //int type = mRandom.nextFloat() > 0.8f ? Step.STEP_TYPE_MOVING : Step.STEP_TYPE_STATIC;
            int type = Step.STEP_TYPE_STATIC;
            //x方向はランダムな場所
            float x = mRandom.nextFloat() * (WORLD_WIDTH - Step.STEP_WIDTH);

            Step step = new Step(type, stepTexture, 0, 0, 144, 36);
            step.setPosition(x, y);
            mSteps.add(step);

            //星はランダムで生成(2/5の確率
            if (mRandom.nextFloat() > 0.6f) {
                Star star = new Star(starTexture, 0, 0, 72, 72);
                //床を基準に乱数で場所を決める
                star.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Star.STAR_HEIGHT + mRandom.nextFloat() * 3);
                mStars.add(star);
            }

            //偽星はランダムで生成(1/5の確率、中央に配置させないため二つに分ける
            if (mRandom.nextFloat() > 0.9f) {
                DarkStar darkstar = new DarkStar(darkstarTexture, 0, 0, 72, 72);
                //床を基準に乱数で場所を決める
                darkstar.setPosition(mRandom.nextFloat() * 3, step.getY() + DarkStar.DARKSTAR_HEIGHT + 1);
                mDarkStars.add(darkstar);
            }
            if (mRandom.nextFloat() > 0.9f) {
                DarkStar darkstar = new DarkStar(darkstarTexture, 0, 0, 72, 72);
                //床を基準に乱数で場所を決める
                darkstar.setPosition(mRandom.nextFloat() * 4 + 6, step.getY() + DarkStar.DARKSTAR_HEIGHT + 1);
                mDarkStars.add(darkstar);
            }

            //床はジャンプで届く位置に生成するように調整
            y += (maxJumpHeight - 0.5f);
            y -= mRandom.nextFloat() * (maxJumpHeight / 3);
        }

        // Playerを配置
        mPlayer = new Player(playerTexture, 0, 0, 72, 72);
        mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.getWidth() / 2, Step.STEP_HEIGHT);

        // ゴールのUFOを配置
        mUfo = new Ufo(ufoTexture, 0, 0, 120, 74);
        mUfo.setPosition(WORLD_WIDTH / 2 - Ufo.UFO_WIDTH / 2, y);
    }

    // それぞれのオブジェクトの状態をアップデートする
    private void update(float delta) {
        switch (mGameState) {
            case GAME_STATE_READY:
                updateReady();
                break;
            case GAME_STATE_PLAYING:
                updatePlaying(delta);
                break;
            case GAME_STATE_GAMEOVER:
                updateGameOver();
                break;
        }
    }
    //タッチされたら状態をゲーム中であるGAME_STATE_PLAYINGに変更
    private void updateReady() {
        if (Gdx.input.justTouched()) {
            mGameState = GAME_STATE_PLAYING;
        }
    }

    private void updatePlaying(float delta) {
        float accel = 0;
        //タッチされている間動作
        if (Gdx.input.isTouched()) {
            //Gdx.input.getX()とGdx.input.getY()でタッチされた座標を取得
            // Vector3クラスはx,yだけでなくZ軸を保持するメンバ変数zも持っているためsetメソッドの第3引数には0を指定
            //mTouchPointをOrthographicCameraクラスのunprojectメソッドに与えて呼び出すことでカメラを使った座標に変換
            mGuiViewPort.unproject(mTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle left = new Rectangle(0, 0, GUI_WIDTH / 2, GUI_HEIGHT);//画面左半分
            Rectangle right = new Rectangle(GUI_WIDTH / 2, 0, GUI_WIDTH / 2, GUI_HEIGHT);//画面右半分
            if (left.contains(mTouchPoint.x, mTouchPoint.y)) {
                //左タッチ時の動作
                accel = 5.0f;
            }
            if (right.contains(mTouchPoint.x, mTouchPoint.y)) {
                //右タッチ時の動作
                accel = -5.0f;
            }
        }

        // Step
        for (int i = 0; i < mSteps.size(); i++) {
            mSteps.get(i).update(delta);
        }

        // Player
        //プレイヤーの座標が0.5以下になった場合(開始時)は踏み台に乗ったと同じ処理(hitStepメソッド)を行い、ジャンプさせる
        if (mPlayer.getY() <= 0.5f) {
            mPlayer.hitStep();
        }
        mPlayer.update(delta, accel);
        mHeightSoFar = Math.max(mPlayer.getY(), mHeightSoFar);
        //保持している距離か、今のプレイヤーの高さか大きい方を保持

        checkCollision(); // 当たり判定を行う

        // ゲームオーバーか判断する
        checkGameOver();
    }

    //あたり判定の処理
    private void checkCollision() {
        // UFO(ゴールとの当たり判定)
        //getBoundingRectangleメソッドでスプライトの矩形を表すRectangleを取得
        //矩形同士が重なっているかであたり判定当たっていればoverlapsの戻り値はtrue
        if (mPlayer.getBoundingRectangle().overlaps(mUfo.getBoundingRectangle())) {
            mGameState = GAME_STATE_GAMEOVER;//ゲームクリア
            return;
        }

        // Starとの当たり判定
        for (int i = 0; i < mStars.size(); i++) {
            Star star = mStars.get(i);

            if (star.mState == Star.STAR_NONE) {//獲得済み
                continue;
            }

            if (mPlayer.getBoundingRectangle().overlaps(star.getBoundingRectangle())) {
                star.get();
                mScore++; //スコアに加算
                if (mScore > mHighScore) { //ハイスコアを超えた場合
                    mHighScore = mScore; //今の点数をハイスコアに
                    //ハイスコアをPreferenceに保存する
                    mPrefs.putInteger("HIGHSCORE", mHighScore); // 第1引数にキー、第2引数に値を指定
                    mPrefs.flush(); // 値を永続化するのに必要
                }
                break;
            }
        }

        // DarkStarとの当たり判定
        for (int i = 0; i < mDarkStars.size(); i++) {
            DarkStar darkstar = mDarkStars.get(i);

            if (darkstar.mDSState == DarkStar.DARKSTAR_NONE) {//獲得済み
                continue;
            }

            if (mPlayer.getBoundingRectangle().overlaps(darkstar.getBoundingRectangle())) {
                darkstar.get();
                mScore++; //スコアに加算
                //当たるとゲームオーバー
                Gdx.app.log("JampActionGame", "GAMEOVER");
                mGameState = GAME_STATE_GAMEOVER;
                break;
            }
        }

        // Stepとの当たり判定
        // 上昇中はStepとの当たり判定を確認しない
        if (mPlayer.velocity.y > 0) {
            return;//上昇中はここで処理終了
        }

        for (int i = 0; i < mSteps.size(); i++) {
            Step step = mSteps.get(i);


            //消えた足場は判定しない
            if (step.mState == Step.STEP_STATE_VANISH) {
                continue;
            }

            if (mPlayer.getY() > step.getY()) {
                if (mPlayer.getBoundingRectangle().overlaps(step.getBoundingRectangle())) {
                    mPlayer.hitStep();

                    //１回踏むと足場動く
                    if (h < 13) {
                        h = h * 1.1f;
                       // h = 0;
                   }
                    step.move(h);
                    break;
                }
            }
        }
    }

    private void checkGameOver() {
        //地面との距離からカメラの高さの半分を引き、その値より低くなったらゲームオーバー
        //(画面の下まで落ちた場合）
        if (mHeightSoFar - CAMERA_HEIGHT / 2 > mPlayer.getY()) {
            Gdx.app.log("JampActionGame", "GAMEOVER");
            mGameState = GAME_STATE_GAMEOVER;
        }
    }

    //ゲームオーバー時タッチするとResultScreenに遷移
    private void updateGameOver() {
        if (Gdx.input.justTouched()) {
            mGame.setScreen(new ResultScreen(mGame, mScore));
        }
    }
}