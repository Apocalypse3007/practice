package com.Angrybird.BirdPackage;

import com.Angrybird.GameObject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.Angrybird.Main.PIXELS_TO_METERS;

public abstract class Bird extends GameObject implements java.io.Serializable {
    private static final long serialVersionUID = 1L; // Serialization version

    private transient Texture texture; // Marked as transient
    private transient Body birdBody; // Marked as transient
    private float density;
    private float diameter;
    private transient World world; // Marked as transient
    private String texturePath; // Save texture path for reinitialization
    private Vector2 initialPosition; // Save bird's initial position for reinitialization

    public Bird(World world, float health, Vector2 slingStartPos, String image_url, float bird_density, float bird_diameter) {
        this.world = world;
        this.texturePath = image_url; // Store the texture path for later use
        this.initialPosition = slingStartPos.cpy(); // Save initial position
        this.density = bird_density;
        this.diameter = bird_diameter;

        // Initialize texture and body
        texture = new Texture(image_url);
        createBirdBody(world, slingStartPos);
        birdBody.setUserData(this);
    }

    public Bird(float health, Vector2 slingStartPos, float bird_density, float bird_diameter) {
        this.world = new World (new Vector2(0,-9.8f), true);
        this.initialPosition = slingStartPos.cpy(); // Save initial position
        this.density = bird_density;
        this.diameter = bird_diameter;

        // Initialize texture and body
        createBirdBody(world, slingStartPos);
        birdBody.setUserData(this);
    }



    public Boolean checkCollisionStatus() {
        return this.collision;
    }

    @Override
    public void destroy() {
        System.out.println("Bird collapsed! Removing texture and destroying body.");
        if (birdBody != null && world != null) {
            world.destroyBody(birdBody);
            birdBody = null;
        }

        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    private void createBirdBody(World world, Vector2 slingStartPos) {
        BodyDef birdBodyDef = new BodyDef();
        birdBodyDef.type = BodyDef.BodyType.DynamicBody;
        birdBodyDef.position.set(slingStartPos);

        birdBody = world.createBody(birdBodyDef);

        CircleShape birdShape = new CircleShape();
        birdShape.setRadius((diameter / 2) * PIXELS_TO_METERS);

        FixtureDef birdFixtureDef = new FixtureDef();
        birdFixtureDef.shape = birdShape;
        birdFixtureDef.density = density;

        birdBody.createFixture(birdFixtureDef);
        birdShape.dispose();
    }

    public void render(SpriteBatch batch) {
        if (texture == null) return;

        float birdWidthInPixels = diameter * PIXELS_TO_METERS;

        float birdPosX = birdBody.getPosition().x;
        float birdPosY = birdBody.getPosition().y;

        batch.begin();
        batch.draw(texture, birdPosX - birdWidthInPixels / 2,
            birdPosY - birdWidthInPixels / 2,
            birdWidthInPixels, birdWidthInPixels);
        batch.end();
    }

    public abstract void specialAbility();

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    public Body getBody() {
        if (birdBody == null) {
            return null;
        }
        return birdBody;
    }

    // Custom serialization logic
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        oos.defaultWriteObject(); // Serialize non-transient fields
    }

    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserialize non-transient fields

        // Reinitialize transient fields
        texture = new Texture(texturePath); // Reload texture
        if (world != null) {
            createBirdBody(world, initialPosition); // Recreate body
        }
    }

    public void initializeBody(World world) {
        if (birdBody == null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(new Vector2(3,3));

            CircleShape shape = new CircleShape();
            shape.setRadius(0.5f); // Example radius for the bird

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1.0f;

            birdBody = world.createBody(bodyDef);
            birdBody.createFixture(fixtureDef);

            shape.dispose();
        }
    }
}
