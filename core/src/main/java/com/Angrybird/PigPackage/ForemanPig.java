package com.Angrybird.PigPackage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class ForemanPig  extends Pig {
    private static final float DIAMETER = 0.35f; // Diameter in pixels
    private static final float HEALTH = 1.5f;   // Health points
    private static final float WEIGHT = 0.6f;    // Weight for physics body

    public ForemanPig(World world, Vector2 position) {
        super(world, position, WEIGHT, HEALTH, DIAMETER,"images/ForemanPig.png");
    }
    public ForemanPig(Vector2 slingStartPos) {
        super(slingStartPos, WEIGHT, HEALTH, DIAMETER);
    }
}
