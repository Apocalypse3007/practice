package com.Angrybird.StaticElements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.Angrybird.Main.PIXELS_TO_METERS;

public class ThreadLine {

    private Viewport viewport;
    private Texture texture;
    private Slingshot slingshot;
    private Vector2 birdPos;  // Store bird position instead of passing Bird object
    private boolean isVisible;  // Visibility flag

    public ThreadLine(Slingshot slingshot, Viewport viewport) {
        texture = new Texture("images/thread.png");
        this.slingshot = slingshot;
        this.viewport = viewport;
        this.birdPos = new Vector2();  // Initialize the bird position vector
        this.isVisible = true;  // Initially visible
    }

    public void render(SpriteBatch batch) {
        if (isVisible) {  // Only render when visible
            Vector2 slingStartPos = slingshot.getSlingStartPos();

            // Convert Box2D positions (meters) to pixel positions
            Vector2 slingStartPosPixels = viewport.project(new Vector2(slingStartPos).scl(PIXELS_TO_METERS));
            Vector2 birdPosPixels = viewport.project(new Vector2(birdPos).scl(PIXELS_TO_METERS));

            // Calculate direction and length in pixels
            Vector2 direction = birdPosPixels.cpy().sub(slingStartPosPixels);
            float slingLength = direction.len();
            float slingAngle = direction.angleDeg();  // Get angle in degrees for rendering

            // Begin drawing the texture
            batch.begin();
            // Draw the texture representing the thread stretched between the sling and the bird
            batch.draw(texture,
                slingStartPosPixels.x, slingStartPosPixels.y,  // Start position in pixels
                0, 0,  // Origin for rotation
                slingLength, texture.getHeight(),  // Width (distance between bird and sling) and height of the texture
                1, 1,  // Scaling factors
                slingAngle,  // Rotation angle
                0, 0,  // Source texture position (starting at 0,0 of texture)
                texture.getWidth(), texture.getHeight(),  // Texture size
                false, false);  // Do not flip the texture
            batch.end();
        }
    }

    // Update the position of the thread based on bird's new position (from LaunchHandler)
    public void updateThreadPosition(Vector2 birdPosition) {
        this.birdPos.set(birdPosition);  // Update the bird's position in the thread
    }

    public void dispose() {
        texture.dispose();
    }

    public Viewport getViewport() {
        return viewport;
    }

    // Method to set visibility
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
