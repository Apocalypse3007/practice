package com.Angrybird.BirdPackage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Bomb extends Bird {
    public Bomb(World world, Vector2 slingStartPos) {

        super(world, 10, slingStartPos, "images/bomb.png", 0.7f, 0.25f);
    }

    @Override
    public void specialAbility() {

    }
}
