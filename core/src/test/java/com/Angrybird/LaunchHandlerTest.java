package com.Angrybird;

import com.Angrybird.BirdPackage.*;
import com.Angrybird.BlockPackage.Block;
import com.Angrybird.LaunchHandler;
import com.Angrybird.Main;
import com.Angrybird.PigPackage.ForemanPig;
import com.Angrybird.PigPackage.Pig;
import com.Angrybird.StaticElements.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class LaunchHandlerTest {
    private LaunchHandler launchHandler;
    private List<Bird> birds;
    private List<Pig> pigs;
    private World world;
    private Viewport viewport;
    private Main main;


    @BeforeAll
    static void initLibGDX() {
        // Initialize the headless backend (useful for non-graphical tests)
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationAdapter() {}, config);
    }

    @BeforeEach
    public void setup() {
        world = new World(new Vector2(0, -9.8f), true);

        // Create birds
        birds = new ArrayList<>();
        birds.add(new Chuck(new Vector2(1, 1)));
        birds.add(new Chuck(new Vector2(1, 1)));
        birds.add(new Chuck(new Vector2(1, 1)));


        pigs = new ArrayList<>();
        pigs.add(new ForemanPig(new Vector2(1, 1)));
        pigs.add(new ForemanPig(new Vector2(1, 1)));
        pigs.add(new ForemanPig(new Vector2(1, 1)));

        viewport = new FitViewport(800, 600, new OrthographicCamera()); // Real viewport
        main = new Main(); // Real Main instance


//        MockitoAnnotations.initMocks(this);
//        ThreadLine inputProcessor;
//        when(inputProcessor.isTouched()).thenReturn(true); // Simulate touch being detected
        launchHandler = new LaunchHandler(main, birds, pigs, viewport);
    }

    @Test
    void isTestWorking() {
        assertTrue(true);
    }

    @Test
    public void testLaunchBird() {
        Bird bird = birds.get(0);
        bird.getBody().setTransform(new Vector2(0.5f, 0.5f), 0); // Drag the bird
        launchHandler.launch(bird, 100);

        assertTrue(bird.getBody().getType().equals(com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody));
        assertTrue(bird.getBody().getLinearVelocity().len() > 0, "Bird should have a velocity after launch.");
    }

    @Test
    public void testSpecialAbility() {
        Bird bird = birds.get(0);
        bird.getBody().setLinearVelocity(new Vector2(3,4));
        launchHandler.executeSpecialAbility(bird);

        assertEquals(7.5f, bird.getBody().getLinearVelocity().len(), 0.1f, "Chuck's velocity should increase with special ability.");
    }

    @Test
    public void testWinCondition() {
        for (Pig pig : pigs) {
            pig.takeDamage(100); // Destroy pig
        }
        boolean result = launchHandler.checkWinStatus();

        assertTrue(result, "Win screen should be shown when all pigs are destroyed.");
    }


}
