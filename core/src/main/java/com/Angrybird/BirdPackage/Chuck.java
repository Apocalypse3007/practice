package com.Angrybird.BirdPackage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Chuck extends Bird {
    public Chuck(World world, Vector2 slingStartPos) {
        super(world, 10, slingStartPos, "images/Chuck.png", 0.9f, 0.2f);
    }
    public Chuck(Vector2 slingStartPos) {
        super(10, slingStartPos, 0.9f, 0.2f);
    }


    @Override
    public void specialAbility() {

    }
}
