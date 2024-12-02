package com.Angrybird.BirdPackage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Red extends Bird {

    public Red(World world, Vector2 slingStartPos) {
        super(world, 10f, slingStartPos, "images/red.png", 1f, 0.2f);
    }

    @Override
    public void specialAbility() {

    }
}

//package com.Angrybird;
//
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.*;
//
//import static com.Angrybird.Main.PIXELS_TO_METERS;
//
//public class Bird {
//    private Texture texture;
//    private Body birdBody;
//
//    // Define the diameter of the bird in meters
//    private static final float BIRD_DIAMETER = 0.2f; // Diameter of the bird
//
//    public Bird(World world, Vector2 slingStartPos) {
//        texture = new Texture("images/red.png");
//        createBirdBody(world, slingStartPos);
//    }
//
//    private void createBirdBody(World world, Vector2 slingStartPos) {
//        BodyDef birdBodyDef = new BodyDef();
//        birdBodyDef.type = BodyDef.BodyType.DynamicBody;
//        birdBodyDef.position.set(slingStartPos);
//
//        birdBody = world.createBody(birdBodyDef);
//
//        CircleShape birdShape = new CircleShape();
//        birdShape.setRadius(BIRD_DIAMETER / 2 * PIXELS_TO_METERS);  // Set the radius in meters
//
//        FixtureDef birdFixtureDef = new FixtureDef();
//        birdFixtureDef.shape = birdShape;
//        birdFixtureDef.density = 1f;  // Adjust as needed
//
//        birdBody.createFixture(birdFixtureDef);
//        birdShape.dispose();
//    }
//
//    public void render(SpriteBatch batch) {
//        // Calculate the dimensions to render
//        float birdWidthInPixels = BIRD_DIAMETER * PIXELS_TO_METERS; // Diameter in pixels
//
//        batch.begin();
//        // Draw the texture centered at the body's position
//        batch.draw(texture, birdBody.getPosition().x - birdWidthInPixels / 2,
//            birdBody.getPosition().y - birdWidthInPixels / 2,
//            birdWidthInPixels, birdWidthInPixels);
//        batch.end();
//    }
//
//    public void dispose() {
//        texture.dispose();
//    }
//
//    public Body getBody() {
//        return birdBody;
//    }
//}
//
