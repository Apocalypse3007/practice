package com.Angrybird;

import com.Angrybird.BirdPackage.Bird;
import com.Angrybird.StaticElements.Slingshot;
import com.Angrybird.StaticElements.Structure;
import com.Angrybird.StaticElements.ThreadLine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.Angrybird.Main.*;
import static com.badlogic.gdx.math.MathUtils.random;

public class LevelMenu implements Serializable {
    private transient Main main;
    private transient Texture background;
    private transient Texture randomLevelButton;
    private transient Texture lockedLevelTexture;
    private transient Texture unlockedLevelTexture;
    private transient Texture exitButton;
    private transient Viewport viewport;
    public transient CollisionManager collisionManager;

    private static final long serialVersionUID = 1L;

    // Status for each level (locked/unlocked)
    public static boolean[] levelStatuses = {true, false, false};
    private static List<Level> levels = new ArrayList<>();

    public LevelMenu(Main main, Viewport viewport) {
        this.main = main;
        this.viewport = viewport;

        background = new Texture("images/level_menu_page.png");
        randomLevelButton = new Texture("images/random_level.png");
        lockedLevelTexture = new Texture("images/locked_level.png");
        unlockedLevelTexture = new Texture("images/unlocked_level.png");
        exitButton = new Texture("images/exit_button.png");
        collisionManager = new CollisionManager();
        setLevels();
    }

    private void setLevels() {
        for (int i = 0; i < 3; i++) {
            World world = new World(new Vector2(0, GRAVITY), true);
            levels.add(new Level(main, viewport, getLevelStructure(i, world), world));
        }
    }

    private Structure getLevelStructure(int levelIndex, World world) {
        switch (levelIndex) {
            case 0:
                return new Structure(world, new Vector2(10, 3), 3, -1, "Glass", "Wood", 1);
            case 1:
                return new Structure(world, new Vector2(10, 3), 3, -1, "Wood", "Glass", 3);
            case 2:
                return new Structure(world, new Vector2(10, 3), 1, 3, "Stone", "Glass", 3);
            default:
                return new Structure(world, new Vector2(10, 3), 2, 0, "Stone", "Glass", 3);
        }
    }

    public void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("savedGame.dat"))) {
            oos.writeObject(levelStatuses);
            oos.writeObject(levels);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("savedGame.dat"))) {
            levelStatuses = (boolean[]) ois.readObject();
            List<Level> deserializedLevels = (List<Level>) ois.readObject();

            levels.clear(); // Clear existing levels
            for (int i = 0; i < deserializedLevels.size(); i++) {
                Level deserializedLevel = deserializedLevels.get(i);
                deserializedLevel.world = new World(new Vector2(0, GRAVITY), true);
                deserializedLevel.main = this.main;
                deserializedLevel.batch = new SpriteBatch();

                deserializedLevel.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

                deserializedLevel.viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Ensure it's updated

                deserializedLevel.debugRenderer = new Box2DDebugRenderer();
                deserializedLevel.pauseScreen = new PauseScreen(main, viewport);
                deserializedLevel.background = new Texture("images/world3.jpg");
                deserializedLevel.slingshot = new Slingshot(viewport, deserializedLevel.world);
                deserializedLevel.thread = new ThreadLine(deserializedLevel.slingshot, viewport);
                deserializedLevel.structure = getLevelStructure(i, deserializedLevel.world);
                deserializedLevel.background = new Texture("images/world3.jpg");
                deserializedLevel.background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                System.out.println("Screen Width: " + Gdx.graphics.getWidth());
                System.out.println("Screen Height: " + Gdx.graphics.getHeight());

                // Initialize birds, if any
                if (deserializedLevel.birds != null) {
                    for (Bird bird : deserializedLevel.birds) {
                        bird.initializeBody(deserializedLevel.world); // Recreate bird physics bodies
                    }
                }

                deserializedLevel.createGround(); // Recreate the ground
                deserializedLevel.launchHandler = new LaunchHandler(
                    main, deserializedLevel.birds, deserializedLevel.structure,
                    deserializedLevel.slingshot, deserializedLevel.thread,
                    deserializedLevel.pauseScreen, viewport
                );

                levels.add(deserializedLevel); // Add the updated level to the list
            }

            System.out.println("Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    public void startCollisionHandle(final World world) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                world.setContactListener(collisionManager); // Start the collision handling after the delay
            }
        }, 2f);
    }


    public void generateRandomLevel() {
        int floors1 = random.nextInt(5) - 1; // Random number between -1 and 3
        int TypeIndex1 = random.nextInt(3); // Random index for base type (0: Wood, 1: Glass, 2: Stone)
        int TypeIndex2 = random.nextInt(3); // Random index for roof type (0: Wood, 1: Glass, 2: Stone)
        String[] materials = {"Wood", "Glass", "Stone"};
        String Type1 = materials[TypeIndex1];
        int floors2 = random.nextInt(5) - 1; // Random number between -1 and 3
        String Type2 = materials[TypeIndex2];
        int birdnum = random.nextInt(4)+2;

        World world = new World(new Vector2(0, GRAVITY), true);
        startCollisionHandle(world);
        Structure structure = new Structure(world, new Vector2(10, 3), floors1, floors2, Type1, Type2, birdnum);  // Adjust structure position as needed
        main.startLevel(new Level(main, viewport, structure, world));

    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw Random Level button
        batch.draw(randomLevelButton, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 + 50, 100, 50);

        // Draw unlocked and locked levels
        float levelYPos = Gdx.graphics.getHeight() / 2 - 50;
        for (int i = 0; i < 3; i++) {
            float levelXPos = (Gdx.graphics.getWidth() / 4) * (i + 1) - 50;
            if (levelStatuses[i]) {
                batch.draw(unlockedLevelTexture, levelXPos, levelYPos, 100, 100);
            } else {
                batch.draw(lockedLevelTexture, levelXPos, levelYPos, 100, 100);
            }
        }

        // Draw Exit button
        batch.draw(exitButton, Gdx.graphics.getWidth() - 120, 20, 100, 50);
        batch.end();

        if (Gdx.input.justTouched()) {
            handleInput();
        }
    }

    private void handleInput() {
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Random Level button
        if (touchX > Gdx.graphics.getWidth() / 2 - 50 && touchX < Gdx.graphics.getWidth() / 2 + 50 &&
            touchY > Gdx.graphics.getHeight() / 2 + 50 && touchY < Gdx.graphics.getHeight() / 2 + 100) {
            generateRandomLevel();
        }

        // Level buttons
        float levelYPos = Gdx.graphics.getHeight() / 2 - 50;
        for (int i = 0; i < 3; i++) {
            float levelXPos = (Gdx.graphics.getWidth() / 4) * (i + 1) - 50;
            if (touchX > levelXPos && touchX < levelXPos + 100 &&
                touchY > levelYPos && touchY < levelYPos + 100) {
                if (levelStatuses[i]) {
                    startCollisionHandle(levels.get(i).world);
                    main.startLevel(levels.get(i));
                } else {
                    System.out.println("Level " + (i + 1) + " is locked!");
                }
            }
        }

        // Exit button
        if (touchX > Gdx.graphics.getWidth() - 120 && touchX < Gdx.graphics.getWidth() - 20 &&
            touchY > 50 && touchY < 100) {
            main.exitToLandingPage();
        }

    }

    public Level resetLevel(Level level) {
        for (int i=0;i<3;i++) {
            Level cmp = levels.get(i);
            if (cmp.equals(level)) {
                World world = new World(new Vector2(0, GRAVITY), true);
                startCollisionHandle(world);
                levels.set(i, (new Level(main, viewport, getLevelStructure(i, world), world)));
                return levels.get(i);
            }
        }
        return null;
    }

    public void unlockNextLevel() {
        for (int i=0;i<3;i++) {
            if (levelStatuses[i]==false) {
                levelStatuses[i] = true;
                break;
            }
        }
    }

    public void startLatestLevel() {
        // Check if all levels are unlocked
        boolean allLevelsUnlocked = true;
        for (boolean levelStatus : levelStatuses) {
            if (!levelStatus) {
                allLevelsUnlocked = false;
                break;
            }
        }

        // If all predefined levels are unlocked, generate and start a random level
        if (allLevelsUnlocked) {
            generateRandomLevel();
        } else {
            // Otherwise, start the next unlocked level in sequence
            for (int i = 0; i < 3; i++) {
                if (levelStatuses[i] == false) {
                    main.startLevel(levels.get(i-1));
                    return;
                }
            }
            main.startLevel(levels.get(2));
        }
    }

    public void dispose() {
        background.dispose();
        randomLevelButton.dispose();
        lockedLevelTexture.dispose();
        unlockedLevelTexture.dispose();
        exitButton.dispose();
    }
}
