package com.Angrybird.BirdPackage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class BirdTest {
    private Bird testBird;

@BeforeAll
static void initLibGDX() {
    // Initialize a headless application
    HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
    new HeadlessApplication(new ApplicationAdapter() {}, config);
}

    @BeforeEach
    void setup() {
        testBird = new Chuck(new Vector2(3, 3));
    }

    @Test
    void isTestWorking() {
        assertTrue(true);
    }

    @Test
    void testDestroy() {
        assertNotNull(testBird.getBody(), "Bird body should be initialized.");
        testBird.destroy();
        assertNull(testBird.getBody(), "Bird body should be null after destruction.");
    }

    @Test
    void testCollisionStatus() {
        assertFalse(testBird.checkCollisionStatus(), "No Collision detected");
        testBird.collision = true; // Simulate collision
        assertTrue(testBird.checkCollisionStatus(), "Collision detected");
    }
}
