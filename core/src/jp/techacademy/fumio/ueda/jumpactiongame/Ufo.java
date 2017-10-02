package jp.techacademy.fumio.ueda.jumpactiongame;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Fumio on 2017/10/02.
 */

//Ufoはゴールなので最初の一から移動しない
public class Ufo extends GameObject {
    // 横幅、高さ
    public static final float UFO_WIDTH = 2.0f;
    public static final float UFO_HEIGHT = 1.3f;

    public Ufo(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(UFO_WIDTH, UFO_HEIGHT);
    }
}