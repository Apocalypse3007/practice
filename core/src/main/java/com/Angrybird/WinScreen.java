package com.Angrybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.Angrybird.Main.WORLD_HEIGHT;
import static com.Angrybird.Main.WORLD_WIDTH;

public class WinScreen {
    private Main main;
    private Stage stage;
    private Sprite background;
    private Skin skin;
    private TextButton nextButton;
    private TextButton exitButton;

    public WinScreen(Main main, Viewport viewport) {
        this.main = main;

        // Initialize stage and skin
        stage = new Stage(viewport);
        skin = new Skin(Gdx.files.internal("uiskin/freezing-ui.json"));

        // Set background
        background = new Sprite(new Texture("images/win.png"));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Set to full screen size

        // Create TextButtons
        nextButton = new TextButton("Next Level", skin);
        exitButton = new TextButton("Exit", skin);

        // Position buttons
        float buttonWidth = 200;
        float buttonHeight = 60;
        float spacing = 20;
        float centerX = (Gdx.graphics.getWidth()) / 2; // Center based on screen width
        float centerY = (Gdx.graphics.getHeight()) / 2; // Center based on screen height

        nextButton.setSize(buttonWidth, buttonHeight);
        nextButton.setPosition(centerX, centerY + spacing + buttonHeight);

        exitButton.setSize(buttonWidth, buttonHeight);
        exitButton.setPosition(centerX, centerY - spacing - buttonHeight);

        // Add listeners
        nextButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                nextLevel();
                return true;
            }
            return false;
        });

        exitButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                exitGame();
                return true;
            }
            return false;
        });

        // Add buttons to the stage
        stage.addActor(nextButton);
        stage.addActor(exitButton);
    }

    public void render(SpriteBatch batch) {
        // Draw background
        batch.begin();
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Ensure size updates dynamically
        background.setPosition(0, 0); // Align to the bottom-left corner
        background.draw(batch);
        batch.end();

        // Draw buttons
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        background.getTexture().dispose();
        skin.dispose();
    }

    private void nextLevel() {
        System.out.println("Next Level button clicked.");
        main.startLatestLevel();
        // Navigate to the next level
    }

    private void exitGame() {
        System.out.println("Exit button clicked.");
        Gdx.input.setInputProcessor(null);
        main.navigateToLevelMenu(); // Navigate back to the landing page
    }

    public Stage getStage() {
        return stage;
    }
}
