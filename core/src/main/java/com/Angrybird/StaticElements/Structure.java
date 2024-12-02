package com.Angrybird.StaticElements;

import com.Angrybird.BirdPackage.Bird;
import com.Angrybird.BirdPackage.Bomb;
import com.Angrybird.BirdPackage.Chuck;
import com.Angrybird.BirdPackage.Red;
import com.Angrybird.BlockPackage.*;
import com.Angrybird.PigPackage.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.Angrybird.Main.*;

public class Structure implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<BlockData> blockDataList; // Metadata for recreating blocks
    private List<PigData> pigDataList;     // Metadata for recreating pigs
    private List<BirdData> birdDataList;   // Metadata for recreating birds
    private transient List<Block> blocks; // Actual blocks, recreated during deserialization
    private transient List<Pig> pigs;     // Actual pigs, recreated during deserialization
    private transient List<Bird> birds;   // Actual birds, recreated during deserialization
    private transient World world;        // Non-serializable, recreated during deserialization

    public Structure(World world, Vector2 startPosition, int numFloors1, int numFloors2, String type1, String type2, int birdnum) {
        this.blocks = new ArrayList<>();
        this.pigs = new ArrayList<>();
        this.birds = new ArrayList<>();
        this.blockDataList = new ArrayList<>();
        this.pigDataList = new ArrayList<>();
        this.birdDataList = new ArrayList<>();
        this.world = world;

        createStructure(world, startPosition, numFloors1, numFloors2, type1, type2);
        createBirds(world, birdnum);
    }

    public Structure (List<Bird> birds, List<Pig> pigs) {
        this.pigs=pigs;
        this.birds=birds;
    }

    private void createBirds(World world, int count) {
        for (int i = 0; i < count; i++) {
            Vector2 position = new Vector2(2 + i, 3); // Adjust positions for birds
            Bird bird;
            if (i % 3 == 0) {
                bird = new Red(world, position);
            } else if (i % 3 == 1) {
                bird = new Chuck(world, position);
            } else {
                bird = new Bomb(world, position);
            }
            birds.add(bird);
            birdDataList.add(new BirdData(bird.getClass().getSimpleName(), position));
        }
    }

    private void createStructure(World world, Vector2 startPosition, int numFloors1, int numFloors2, String type1, String type2) {
        if (numFloors1 != -1) {
            createBuilding(world, startPosition, numFloors1, type1);
        }
        if (numFloors2 != -1) {
            Vector2 secondBuildingStartPos = new Vector2(startPosition.x + BLOCK_WIDTH * 2, startPosition.y);
            createBuilding(world, secondBuildingStartPos, numFloors2, type2);
        }
    }

    private void createBuilding(World world, Vector2 startPosition, int numFloors, String type) {
        Vector2 position = new Vector2(startPosition.x, startPosition.y);

        for (int floor = 0; floor < numFloors; floor++) {
            float yOffset = (BLOCK_WIDTH + BLOCK_HEIGHT) * floor;

            Vector2 leftVerticalBlockPos = new Vector2(position.x + BLOCK_HEIGHT / 2, position.y + yOffset + BLOCK_HEIGHT / 2);
            Block leftVerticalBlock = createBlock(world, type, leftVerticalBlockPos, 90);
            blocks.add(leftVerticalBlock);
            blockDataList.add(new BlockData(type, leftVerticalBlockPos, 90));

            Vector2 rightVerticalBlockPos = new Vector2(position.x + BLOCK_WIDTH - BLOCK_HEIGHT / 2, position.y + yOffset + BLOCK_HEIGHT / 2);
            Block rightVerticalBlock = createBlock(world, type, rightVerticalBlockPos, 90);
            blocks.add(rightVerticalBlock);
            blockDataList.add(new BlockData(type, rightVerticalBlockPos, 90));

            Vector2 horizontalBlockPos = new Vector2(position.x + BLOCK_WIDTH / 2, position.y + yOffset + BLOCK_WIDTH / 2 + BLOCK_HEIGHT);
            Block horizontalBlock = createBlock(world, type, horizontalBlockPos, 0);
            blocks.add(horizontalBlock);
            blockDataList.add(new BlockData(type, horizontalBlockPos, 0));

            Vector2 pigPosition = new Vector2(position.x + BLOCK_WIDTH / 2, position.y + yOffset - BLOCK_WIDTH / 2 + 1f);
            Pig pig = (floor == 0 && numFloors > 1) ? new BigPig(world, pigPosition) : new SmallPig(world, pigPosition);
            pigs.add(pig);
            pigDataList.add(new PigData(pig instanceof BigPig ? "BigPig" : "SmallPig", pigPosition));
        }
    }

    private Block createBlock(World world, String type, Vector2 position, float rotation) {
        Block block;
        switch (type.toLowerCase()) {
            case "stone":
                block = new StoneBlock(world, position);
                break;
            case "glass":
                block = new GlassBlock(world, position);
                break;
            case "wood":
            default:
                block = new WoodBlock(world, position);
                break;
        }
        block.getBody().setTransform(block.getBody().getPosition(), (float) Math.toRadians(rotation));
        return block;
    }

    public void render(SpriteBatch batch) {
        for (Block block : blocks) {
            block.render(batch);
        }
        for (Pig pig : pigs) {
            pig.render(batch);
        }
        for (Bird bird : birds) {
            bird.render(batch);
        }
    }

    public void dispose() {
        for (Block block : blocks) {
            block.dispose();
        }
        for (Pig pig : pigs) {
            pig.dispose();
        }
        for (Bird bird : birds) {
            bird.dispose();
        }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
    }

    public void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.blocks = new ArrayList<>();
        this.pigs = new ArrayList<>();
        this.birds = new ArrayList<>();
        this.world = new World(new Vector2(0, -9.8f), true);

        for (BlockData data : blockDataList) {
            blocks.add(createBlock(world, data.type, data.position, data.rotation));
        }
        for (PigData data : pigDataList) {
            pigs.add(data.createPig(world));
        }
        for (BirdData data : birdDataList) {
            birds.add(data.createBird(world));
        }
    }

    public List<Bird> getBirds() {
        return birds;
    }

    public List<Pig> getPigs() {
        return pigs;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    // Helper class for block metadata
    private static class BlockData implements Serializable {
        String type;
        Vector2 position;
        float rotation;

        BlockData(String type, Vector2 position, float rotation) {
            this.type = type;
            this.position = position;
            this.rotation = rotation;
        }
    }

    // Helper class for pig metadata
    private static class PigData implements Serializable {
        String type;
        Vector2 position;

        PigData(String type, Vector2 position) {
            this.type = type;
            this.position = position;
        }

        Pig createPig(World world) {
            switch (type) {
                case "BigPig":
                    return new BigPig(world, position);
                case "SmallPig":
                    return new SmallPig(world, position);
                case "ForemanPig":
                default:
                    return new ForemanPig(world, position);
            }
        }
    }

    // Helper class for bird metadata
    private static class BirdData implements Serializable {
        String type;
        Vector2 position;

        BirdData(String type, Vector2 position) {
            this.type = type;
            this.position = position;
        }

        Bird createBird(World world) {
            switch (type) {
                case "Red":
                    return new Red(world, position);
                case "Chuck":
                    return new Chuck(world, position);
                case "Bomb":
                default:
                    return new Bomb(world, position);
            }
        }
    }
}
