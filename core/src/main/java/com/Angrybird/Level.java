package com.Angrybird;

import com.Angrybird.BirdPackage.Bird;
import com.Angrybird.StaticElements.Slingshot;
import com.Angrybird.StaticElements.Structure;
import com.Angrybird.StaticElements.ThreadLine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.*;
import java.time.LocalTime;
import java.util.List;

import static com.Angrybird.Main.*;

public class Level implements Serializable {
    public static final long serialVersionUID = 1L;

    public boolean levelUnlocked;
    public transient World world; // World is transient and must be recreated
    public transient SpriteBatch batch;
    public transient Viewport viewport;
    public transient Box2DDebugRenderer debugRenderer;
    public transient Texture background;
    public transient Slingshot slingshot;
    public Structure structure;
    public transient Body groundBody;
    public transient LaunchHandler launchHandler;
    public transient List<Bird> birds; // Birds don't need to be transient because they are part of the structure
    public transient ThreadLine thread;
    public transient PauseScreen pauseScreen;
    public transient Main main;

    public LocalTime t = LocalTime.now();
    public byte[] vanillaState; // Stores the initial state of the level

    // Constructor
    public Level(Main main, Viewport viewport, Structure structure, World world) {
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.batch = new SpriteBatch();
        this.background = new Texture("images/world3.jpg");

        this.main = main;
        this.world = world;
        this.structure = structure;
        this.birds = structure.getBirds();

        this.debugRenderer = new Box2DDebugRenderer();
        this.pauseScreen = new PauseScreen(main, viewport);

        this.slingshot = new Slingshot(viewport, world);
        this.thread = new ThreadLine(slingshot, viewport);

        createGround();

        this.launchHandler = new LaunchHandler(main, birds, structure, slingshot, thread, pauseScreen, viewport);

        this.levelUnlocked = false;

    }

    // Getters and setters
    public boolean getLevelStatus() {
        return levelUnlocked;
    }

    public void unlockLevel() {
        levelUnlocked = true;
    }

    public void time() {
        System.out.println("Time: " + t);
    }

    // Ground creation
    public void createGround() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT * 0.06f);

        groundBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WORLD_WIDTH / 2, WORLD_HEIGHT * 0.06f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0.5f;

        groundBody.createFixture(fixtureDef);
        shape.dispose();
    }

    // Rendering
    public void render() {
        ScreenUtils.clear(Color.BLACK);

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        if (!pauseScreen.isPaused()) {
            world.step(1 / 60f, 6, 2);
            GameObject.updateObjectsToDestroy();
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.end();

        slingshot.render(batch);
        thread.render(batch);

        if (birds!=null){
            for (Bird bird : birds) {
                bird.render(batch);
            }
        }


        structure.render(batch);
        launchHandler.handleInput();

        debugRenderer.render(world, viewport.getCamera().combined);

        pauseScreen.render();
    }

    // Disposal
    public void dispose() {
        world.dispose();
        batch.dispose();
        background.dispose();
        debugRenderer.dispose();
        structure.dispose();
        pauseScreen.dispose();
    }

    // Serialization
    public void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        if (launchHandler.currentBird != null) {
            oos.writeObject(launchHandler.currentBird);
        } else {
            oos.writeObject(null);  // Or handle appropriately if it's null
        }        System.out.println("Level being stored");
    }

    void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        System.out.println("Level being Loaded");

        this.world = new World(new Vector2(0, -9.8f), true);
        this.batch = new SpriteBatch();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.debugRenderer = new Box2DDebugRenderer();
        this.pauseScreen = new PauseScreen(main, viewport);
        this.background = new Texture("images/world3.jpg");
        this.slingshot = new Slingshot(viewport, world);
        this.thread = new ThreadLine(slingshot, viewport);

        createGround(); // Recreate the ground
        this.launchHandler = new LaunchHandler(main, birds, structure, slingshot, thread, pauseScreen, viewport);
    }
}
