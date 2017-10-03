package jp.techacademy.fumio.ueda.jumpactiongame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class DarkStar extends Sprite {
    // 横幅、高さ
    public static final float DARKSTAR_WIDTH = 0.8f;
    public static final float DARKSTAR_HEIGHT = 0.8f;

    // 状態、存在する場合と獲得されて無くなった場合
  //  public static final int DARKSTAR_EXIST = 0;
   // public static final int DARKSTAR_NONE = 1;

    //状態を保持するメンバ変数
   // int mDSState;

    public DarkStar(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(DARKSTAR_WIDTH, DARKSTAR_HEIGHT);
     //   mDSState = DARKSTAR_EXIST;
    }

    //プレイヤーが触れた時に呼ばれるgetメソッド,状態をSTAR_NONEにし、setAlphaメソッドで透明に
   // public void get() {
  //      mDSState = DARKSTAR_NONE;
   //     setAlpha(0);
   // }
}