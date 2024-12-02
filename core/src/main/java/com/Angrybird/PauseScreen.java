package com.Angrybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PauseScreen {
    private Texture pauseButtonTexture;
    private Texture resumeButtonTexture;
    private Texture restartButtonTexture;
    private Texture exitButtonTexture;
    private Texture musicButtonTexture;
    private SpriteBatch spriteBatch;
    private Viewport viewport;
    private boolean isPaused;
    private Main main;

    // Button dimensions and positions
    private static final float BUTTON_WIDTH = 100f;
    private static final float BUTTON_HEIGHT = 100f;
    private static final float BUTTON_PADDING = 20f;

    public PauseScreen(Main main, Viewport viewport) {
        this.viewport = viewport;
        this.isPaused = false;
        this.main = main;

        // Load textures for each button
        pauseButtonTexture = new Texture(Gdx.files.internal("images/pause.png"));
        resumeButtonTexture = new Texture(Gdx.files.internal("images/resume_button.png"));
        restartButtonTexture = new Texture(Gdx.files.internal("images/restart_button.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("images/exit_button.png"));
        musicButtonTexture = new Texture(Gdx.files.internal("images/music_button.png"));

        spriteBatch = new SpriteBatch();
    }

    public PauseScreen() {
        this.isPaused = false;
    }

    public void togglePause() {
        isPaused = !isPaused;
        System.out.println("Game " + (isPaused ? "paused" : "resumed"));
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean handleInput() {
        if (Gdx.input.justTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            if (isPaused) {
                if (isTouchWithinButton(touchPos, getButtonX(0), getButtonY())) {
                    togglePause();
                    return true;
                } else if (isTouchWithinButton(touchPos, getButtonX(1), getButtonY())) {
                    restartGame(); // Restart button action
                    return true;
                } else if (isTouchWithinButton(touchPos, getButtonX(2), getButtonY())) {
                    exitLevel(); // Exit button action
                    return true;
                } else if (isTouchWithinButton(touchPos, getButtonX(3), getButtonY())) {
                    toggleMusic(); // Music button action
                    return true;
                }
            } else {
                if (isTouchWithinPauseButton(touchPos)) {
                    togglePause();
                    return true;
                }
            }
        }
        return false;
    }

    private void toggleMusic() {
        if (main != null) {
            System.out.println("Toggling music...");
            if (main.getMusic().isPlaying()) {
                main.disableMusic();
            } else {
                main.enableMusic();
            }
        }
    }

    private void restartGame() {
        main.navigateToLevelMenu();
        main.resetLevel();
        System.out.println("Game restarting...");
    }

    private void exitLevel() {
        System.out.println("Exiting Level...");
        main.navigateToLevelMenu();
    }

    public boolean isTouchWithinPauseButton(Vector2 touchPos) {
        Vector2 unprojectedTouch = new Vector2(touchPos);
        viewport.unproject(unprojectedTouch);

        float buttonX = viewport.getWorldWidth() - BUTTON_WIDTH;
        float buttonY = viewport.getWorldHeight() - BUTTON_HEIGHT;

        return unprojectedTouch.x >= buttonX &&
            unprojectedTouch.x <= (buttonX + BUTTON_WIDTH) &&
            unprojectedTouch.y >= buttonY - BUTTON_HEIGHT / 2 &&
            unprojectedTouch.y <= (buttonY + BUTTON_HEIGHT / 2);
    }

    private boolean isTouchWithinButton(Vector2 touchPos, float buttonX, float buttonY) {
        Vector2 unprojectedTouch = new Vector2(touchPos);
        viewport.unproject(unprojectedTouch);
        return unprojectedTouch.x >= buttonX && unprojectedTouch.x <= (buttonX + BUTTON_WIDTH) &&
            unprojectedTouch.y >= buttonY && unprojectedTouch.y <= (buttonY + BUTTON_HEIGHT);
    }

    private float getButtonX(int buttonIndex) {
        return (viewport.getWorldWidth() - (4 * BUTTON_WIDTH + 3 * BUTTON_PADDING)) / 2 +
            buttonIndex * (BUTTON_WIDTH + BUTTON_PADDING);
    }

    private float getButtonY() {
        return viewport.getWorldHeight() / 2 - BUTTON_HEIGHT / 2;
    }

    public void render() {
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        if (isPaused) {
            // Draw the resume, restart, exit, and music buttons
            spriteBatch.draw(resumeButtonTexture, getButtonX(0), getButtonY(), BUTTON_WIDTH, BUTTON_HEIGHT);
            spriteBatch.draw(restartButtonTexture, getButtonX(1), getButtonY(), BUTTON_WIDTH, BUTTON_HEIGHT);
            spriteBatch.draw(exitButtonTexture, getButtonX(2), getButtonY(), BUTTON_WIDTH, BUTTON_HEIGHT);
            spriteBatch.draw(musicButtonTexture, getButtonX(3), getButtonY(), BUTTON_WIDTH, BUTTON_HEIGHT);
        } else {
            // Draw the pause button only when the game is not paused
            float buttonX = viewport.getWorldWidth() - BUTTON_WIDTH;
            float buttonY = viewport.getWorldHeight() - BUTTON_HEIGHT;
            spriteBatch.draw(pauseButtonTexture, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        spriteBatch.end();
    }

    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
        if (pauseButtonTexture != null) pauseButtonTexture.dispose();
        if (resumeButtonTexture != null) resumeButtonTexture.dispose();
        if (restartButtonTexture != null) restartButtonTexture.dispose();
        if (exitButtonTexture != null) exitButtonTexture.dispose();
        if (musicButtonTexture != null) musicButtonTexture.dispose();
    }
}
