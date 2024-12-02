package com.Angrybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Main extends ApplicationAdapter {
    SpriteBatch batch;
    Viewport viewport;
    Texture background;
//    private Texture playButton;

    LevelMenu levelMenu;
    Level level;
    WinScreen winScreen;
    LoseScreen loseScreen;

    boolean isPlaying;
    boolean isInLevelMenu;
    boolean isInWinScreen;
    boolean isInLoseScreen;
    Music backgroundMusic;

    private Stage stage;
    private Skin skin;
    private TextButton playButton;
    private TextButton exitButton;
    private TextButton saveButton;
    private TextButton restoreButton;

    public static final float PIXELS_TO_METERS = 5f;
    public static final float GRAVITY = -8f;
    public static final float WORLD_WIDTH = 20f;
    public static final float WORLD_HEIGHT = 15f;
    public static final float BLOCK_WIDTH = 2.8f;  // Width of the block in meters
    public static final float BLOCK_HEIGHT = 0.4f;  // Height of the block in meters


    @Override
    public void create() {
        // Switch to full-screen mode
//        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        batch = new SpriteBatch();
        background = new Texture("images/landing_page.png");

        if (viewport==null) viewport = new FitViewport(800, 600); // Logical world size
        viewport.apply();
        System.out.println("Screen: " + Gdx.graphics.getWidth());
        System.out.println("Screen: " + Gdx.graphics.getHeight());

        // Initialize Skin and Stage
        skin = new Skin(Gdx.files.internal("uiskin/freezing-ui.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create TextButton for Play
        playButton = new TextButton("Play", skin);
        playButton.setSize(150, 60);
        playButton.setPosition(
            (Gdx.graphics.getWidth() - playButton.getWidth()) / 2,
            (Gdx.graphics.getHeight() - playButton.getHeight()) / 2 + 100
        );

        exitButton = new TextButton("Exit", skin);
        exitButton.setSize(150, 60);
        exitButton.setPosition(
            (Gdx.graphics.getWidth() - exitButton.getWidth()) / 2,
            (Gdx.graphics.getHeight() - exitButton.getHeight()) / 2 // Below Play button
        );

        saveButton = new TextButton("Save", skin);
        saveButton.setSize(150, 60);
        saveButton.setPosition(
            (Gdx.graphics.getWidth() - saveButton.getWidth()) / 2,
            (Gdx.graphics.getHeight() - saveButton.getHeight()) / 2 - 100 // Below Play button
        );

        restoreButton = new TextButton("Restore", skin);
        restoreButton.setSize(150, 60);
        restoreButton.setPosition(
            (Gdx.graphics.getWidth() - restoreButton.getWidth()) / 2,
            (Gdx.graphics.getHeight() - restoreButton.getHeight()) / 2 - 200 // Below Play button
        );


        // Add listener for the play button
        playButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                isInLevelMenu = true;
                return true;
            }
            return false;
        });

        exitButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                Gdx.app.exit();
                return true;
            }
            return false;
        });

        saveButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                if (levelMenu!=null) levelMenu.saveGame();
                return true;
            }
            return false;
        });

        restoreButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                if (levelMenu!=null) levelMenu.loadGame();
                return true;
            }
            return false;
        });

        // Add Play Button to Stage
        stage.addActor(playButton);
        stage.addActor(exitButton);
        stage.addActor(saveButton);
        stage.addActor(restoreButton);


        isPlaying = false;
        isInLevelMenu = false;
        isInWinScreen = false;
        isInLoseScreen = false;

        levelMenu = new LevelMenu(this, viewport);
    }



    @Override
    public void render() {
        if (isPlaying) {
            level.render();
        } else if (isInLevelMenu) {
            levelMenu.render(batch);
        } else if (isInWinScreen) {
            winScreen.render(batch);
        } else if (isInLoseScreen) {
            loseScreen.render(batch);
        } else {
            // Render landing page
            batch.begin();
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();

            // Render the stage (ImageButton)
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        }
    }


    private void renderLandingPage() {
        batch.begin();
        // Render your landing page elements...
        batch.end();
        checkPlayButtonClick();
    }

    public void navigateToLevelMenu() {
//        level.time();
        Gdx.input.setInputProcessor(null);
        isPlaying = false;
        isInLevelMenu = true;
        isInWinScreen = false;
        isInLoseScreen = false;
    }

    private void checkPlayButtonClick() {
        if (Gdx.input.justTouched()) {
            // Logic for detecting if play button is clicked
            levelMenu = new LevelMenu(this, viewport);
            isInLevelMenu = true;
        }
    }


    public void startLevel(Level level) {
        System.out.println("Starting level: " + level);  // Add this for debugging
        this.level = level;
        isPlaying = true;
        isInLevelMenu = false;
        Gdx.input.setInputProcessor(null);
        // Log to check if the level gets reset
        System.out.println("Level state before: " + this.level);
        isInWinScreen = false;
        isInLoseScreen = false;
    }


    public void showWinScreen() {
//        if (level!=null) {
//            level.dispose();
//        }
        isPlaying = false;
        isInWinScreen = true;
        isInLevelMenu = false;
        isInLoseScreen = false;
        this.winScreen = new WinScreen(this, viewport);
        levelMenu.unlockNextLevel();

        Gdx.input.setInputProcessor(winScreen.getStage());
        System.out.println("isInWinScreen: " + isInWinScreen);
    }

    public void startLatestLevel() {
        levelMenu.startLatestLevel();
    }

    public void showLoseScreen() {
        isPlaying = false;
        isInLoseScreen = true;
        isInLevelMenu = false;
        isInWinScreen = false;
        this.loseScreen = new LoseScreen(this, viewport);

        Gdx.input.setInputProcessor(loseScreen.getStage());
        System.out.println("isInLoseScreen: " + isInLoseScreen);
    }

    public void exitToLandingPage() {
        Gdx.input.setInputProcessor(this.stage);
        isPlaying = false;
        isInLevelMenu = false;
        isInWinScreen = false;
        isInLoseScreen = false;
    }

    public void pauseGame() {
        isPlaying = false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }


    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        if (backgroundMusic != null) {
            backgroundMusic.dispose(); // Dispose of music
        }

        if (level != null) {
            level.dispose();
        }
        if (levelMenu != null) {
            levelMenu.dispose();
        }
        stage.dispose();
        skin.dispose();
    }


    public Level getCurrentLevel() {
        return level;
    }

    public void resetLevel() {
        Level x = levelMenu.resetLevel(this.level);
        if (x!=null) {
            startLevel(x);
        } else {
            levelMenu.generateRandomLevel();
        }
    }

    public void enableMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    // Public method to disable music
    public void disableMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    public Music getMusic() {
        return backgroundMusic;
    }

}


// Button
// Testing

