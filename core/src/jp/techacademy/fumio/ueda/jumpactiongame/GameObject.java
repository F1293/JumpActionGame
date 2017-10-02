package jp.techacademy.fumio.ueda.jumpactiongame;

/**
 * Created by Fumio on 2017/10/02.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class GameObject extends Sprite {
    public final Vector2 velocity;  //Vector2クラスはメンバ変数にxとyを持つクラス x方向、y方向の速度を保持する

    public GameObject(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);

        velocity = new Vector2();
    }
}