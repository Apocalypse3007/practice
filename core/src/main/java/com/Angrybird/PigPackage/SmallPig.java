package com.Angrybird.PigPackage;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;

public class SmallPig extends Pig {
    private static final float DIAMETER = 0.2f; // Diameter in pixels
    private static final float HEALTH = 1f;   // Health points
    private static final float WEIGHT = 0.4f;    // Weight for physics body

    public SmallPig(World world, Vector2 position) {
        // Subtract 1f from the y coordinate before passing to the superclass constructor
        super(world, new Vector2(position.x, position.y - 0.25f), WEIGHT, HEALTH, DIAMETER, "images/pig.png");
    }
}
