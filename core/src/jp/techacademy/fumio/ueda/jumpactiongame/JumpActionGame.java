package jp.techacademy.fumio.ueda.jumpactiongame;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class JumpActionGame extends Game {
	//publicにすることで外からアクセス可能にする
	public SpriteBatch batch;
	public ActivityRequestHandler mRequestHandler;
//ActivityRequestHandlerインタフェースを実装したAndroidLauncherを受け取る
	//広告表示させるかどうか
	public JumpActionGame(ActivityRequestHandler requestHandler) {
		super();
		mRequestHandler = requestHandler;
		//受け取ったActivityRequestHandlerはメンバ変数に保持
	}


	@Override
	public void create(){
		batch = new SpriteBatch();

		//GameScreenを表示する
		setScreen(new GameScreen(this));
	}
}