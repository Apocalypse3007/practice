package com.Angrybird.StaticElements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Ground {
    private Texture groundTexture;
    private Body groundBody;
    private float width, height;

    ShapeRenderer shapeRenderer;

    public Ground(World world, float groundWidth, float groundHeight) {
        this.width = groundWidth;
        this.height = groundHeight;

        // Load ground texture
        groundTexture = new Texture("images/GROUND.png");

        // Create the ground body in the Box2D world
        createGroundBody(world);
        shapeRenderer = new ShapeRenderer();
    }

    private void createGroundBody(World world) {
        // Define the ground body as a static body at the bottom of the world
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(new Vector2(0, 0));

        groundBody = world.createBody(groundBodyDef);

        // Create a rectangular shape for the ground
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(width, height);

        // Define the physical properties of the ground
        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 2000f;  // Add friction so objects don't slide too much

        // Attach the shape to the body
        groundBody.createFixture(groundFixtureDef);

        // Dispose of the shape as it's no longer needed after being used
        groundShape.dispose();
    }

    // Render the ground texture
    public void render(SpriteBatch batch) {
//        batch.begin();
//        batch.draw(groundTexture, groundBody.getPosition().x - width / 2, groundBody.getPosition().y, width, height);
//        batch.end();

        // Draw the black rectangle representing the ground area
//        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix()); // Sync with the camera
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(0, 0, 0, 1); // Set color to black
//        shapeRenderer.rect(groundBody.getPosition().x - width / 2, groundBody.getPosition().y, width, height); // Black rectangle
//        shapeRenderer.end();

        // Render the ground texture
        batch.begin();
        batch.draw(groundTexture, groundBody.getPosition().x, groundBody.getPosition().y, width, height);
        batch.end();
    }

    // Dispose of the texture
    public void dispose() {
        groundTexture.dispose();
    }

    // Get the ground body for Box2D collision purposes
    public Body getBody() {
        return groundBody;
    }
}
