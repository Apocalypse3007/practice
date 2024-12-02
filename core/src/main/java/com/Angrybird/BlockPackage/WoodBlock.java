package com.Angrybird.BlockPackage;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class WoodBlock extends Block {
    public WoodBlock(World world, Vector2 position) {
        super(world, position, "images/Woodblock.png", 1.f);
    }
}
