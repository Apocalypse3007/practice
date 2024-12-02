package com.Angrybird.StaticElements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.Angrybird.Main.*;

public class Slingshot {
    private Texture texture;
    private Vector2 slingStartPos;

    public Slingshot(Viewport viewport, World world) {
        texture = new Texture("images/slingshot.png");
        slingStartPos = new Vector2(WORLD_WIDTH* 0.15f, WORLD_HEIGHT * 0.15f );
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(texture, slingStartPos.x, slingStartPos.y , 1.2f, 2f);
        batch.end();
    }

    public Vector2 getSlingStartPos() {
        return slingStartPos;
    }

    public void dispose() {
        texture.dispose();
    }

}
