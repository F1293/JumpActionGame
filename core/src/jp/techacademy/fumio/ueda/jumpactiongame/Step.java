package jp.techacademy.fumio.ueda.jumpactiongame;

/**
 * Created by Fumio on 2017/10/02.
 */

import com.badlogic.gdx.graphics.Texture;

public class Step extends GameObject {
    // 横幅、高さ
    public static final float STEP_WIDTH = 2.0f;
    public static final float STEP_HEIGHT = 0.5f;

    // タイプ（通常と動くタイプ）
    public static final int STEP_TYPE_STATIC = 0;
    public static final int STEP_TYPE_MOVING = 1;


    // 速度
    //public static final float STEP_VELOCITY = 2.0f;

    int mType;
    int mState;

    public Step(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(STEP_WIDTH, STEP_HEIGHT);
       // mType = type;
        //if (mType == STEP_TYPE_MOVING) {
        //    velocity.x = STEP_VELOCITY;
        //}
    }

    // 座標を更新する
    public void update(float deltaTime) {
        //タイプがSTEP_TYPE_MOVINGの場合に速度から座標を計算。画面端に達したときは反対側の端から表示??
        //画面はしに行くと速度が逆？
        if (mType == STEP_TYPE_MOVING) {
            setX(getX() + velocity.x * deltaTime);

            if (getX() < - STEP_WIDTH / 2) {
                velocity.x = -velocity.x;
                setX(- STEP_WIDTH / 2);
            }
            if (getX() > GameScreen.WORLD_WIDTH - STEP_WIDTH / 2) {
                velocity.x = -velocity.x;
                setX(GameScreen.WORLD_WIDTH - STEP_WIDTH / 2);
            }
        }
    }


    //踏むたびに早く動く
    public void move(float i) {
        mType = STEP_TYPE_MOVING;
        if (getX() < GameScreen.WORLD_WIDTH / 2 - STEP_WIDTH / 2) {
            velocity.x = i;
        }
        if (getX() > GameScreen.WORLD_WIDTH / 2 - STEP_WIDTH / 2) {
            velocity.x = - i ;
        }
       // velocity.x ++;

    }
}