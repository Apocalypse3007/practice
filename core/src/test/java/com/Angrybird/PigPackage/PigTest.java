package com.Angrybird.PigPackage;

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

class PigTest {
    private Pig testPig;

    @BeforeAll
    static void initLibGDX() {
        // Initialize a headless application
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationAdapter() {}, config);
    }

    @BeforeEach
    void setup() {
        testPig = new ForemanPig(new Vector2(3, 3));
    }

    @Test
    void isTestWorking() throws FileNotFoundException {
        assertTrue(true);
    }

    @Test
    void testDestroy() {
        assertNotNull(testPig.getBody(), "Pig body initialized");
        testPig.destroy();
        assertNull(testPig.getBody(), "Pig body destroyed");
    }

    @Test
    void testCollisionStatus() {
        assertFalse(testPig.checkCollisionStatus(), "No Collision detected");
        testPig.collision = true; // Simulate collision
        assertTrue(testPig.checkCollisionStatus(), "Collision detected");
    }
}
