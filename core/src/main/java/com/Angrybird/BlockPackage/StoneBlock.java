package com.Angrybird.BlockPackage;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class StoneBlock extends Block {
    public StoneBlock(World world, Vector2 position) {
        super(world, position, "images/Stoneblock.png", 1.5f);
    }
}
