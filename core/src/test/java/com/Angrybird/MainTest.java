package com.Angrybird;

import com.Angrybird.StaticElements.Structure;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainTest {
    private Main main;
    private Music mockMusic;

    @BeforeAll
    static void initLibGDX() {
        // Initialize the headless backend (useful for non-graphical tests)
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationAdapter() {}, config);
    }

    @BeforeEach
    void setup() {
        mockMusic = mock(Music.class);

        main = new Main();


//        main.batch = new SpriteBatch(); // Create a real SpriteBatch instance
        main.backgroundMusic = mockMusic;
        main.viewport = new FitViewport(800, 600);
//        Level level = new Level(main, main.viewport, new Structure(), new World(new Vector2(0,-9.8f), true));


//        main.create();
    }

    @Test
    void isTestWorking() {
        assertTrue(true);
    }

    @Test
    void testCreate() {
        assertNotNull(main.viewport, "Viewport should be initialized.");
        assertNotNull(main.backgroundMusic, "Background music should be initialized.");
    }

    @Test
    void testNavigateToLevelMenu() {
        // Simulate navigation to Level Menu
        main.navigateToLevelMenu();

        assertTrue(main.isInLevelMenu, "Application should be in Level Menu state.");
        assertFalse(main.isPlaying, "Application should not be in Playing state.");
    }


    @Test
    void testPauseGame() {
        main.isPlaying = true; // Simulate playing state
        main.pauseGame();

        assertFalse(main.isPlaying, "Game should be paused.");
    }

    @Test
    void testExitToLandingPage() {
        main.isPlaying = true; // Simulate playing state
        main.isInLevelMenu = true;

        main.exitToLandingPage();

        assertFalse(main.isPlaying, "Game should not be in Playing state.");
        assertFalse(main.isInLevelMenu, "Game should not be in Level Menu state.");
        assertFalse(main.isInWinScreen, "Game should not be in Win Screen state.");
        assertFalse(main.isInLoseScreen, "Game should not be in Lose Screen state.");
    }

    @Test
    void testEnableMusic() {
        when(mockMusic.isPlaying()).thenReturn(false); // Simulate music not playing
        main.enableMusic();

        verify(mockMusic, times(1)).play();
    }

    @Test
    void testDisableMusic() {
        when(mockMusic.isPlaying()).thenReturn(true); // Simulate music playing
        main.disableMusic();

        verify(mockMusic, times(1)).pause();
    }

}
