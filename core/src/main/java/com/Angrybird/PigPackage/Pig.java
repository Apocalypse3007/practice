package com.Angrybird.PigPackage;

import com.Angrybird.GameObject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.Angrybird.Main.PIXELS_TO_METERS;

public class Pig extends GameObject implements java.io.Serializable {
    private static final long serialVersionUID = 1L; // Serialization version UID

    private transient Texture texture; // Marked as transient for serialization
    private transient Body pigBody; // Marked as transient for serialization
    private transient World world; // Marked as transient

    private String texturePath; // Path to reload the texture during deserialization
    private Vector2 initialPosition; // Save the initial position for body recreation
    private float initialDensity; // Save density for body recreation
    private float initialDiameter; // Save diameter for body recreation
    private float initialHealth; // Save health for recreation

    public Pig(World world, Vector2 position, float density, float health, float diameter, String image_url) {
        this.world = world;
        this.texturePath = image_url; // Save texture path for reloading
        this.initialPosition = position.cpy(); // Save the initial position
        this.initialDensity = density; // Save density for recreation
        this.initialDiameter = diameter; // Save diameter
        this.initialHealth = health; // Save health
        this.health = health;

        // Initialize texture and body
        this.texture = new Texture(image_url);

        createPigBody(world, position);
        pigBody.setUserData(this);
    }

    public Pig(Vector2 position, float density, float health, float diameter) {
        this.world = new World (new Vector2(0,-9.8f), true);
        this.initialPosition = position.cpy(); // Save the initial position
        this.initialDensity = density; // Save density for recreation
        this.initialDiameter = diameter; // Save diameter
        this.initialHealth = health; // Save health
        this.health = health;
        createPigBody(world, position);
        pigBody.setUserData(this);
    }

    public Boolean checkCollisionStatus() {
        return this.collision;
    }

    @Override
    public void destroy() {
        System.out.println("Pig collapsed! Removing texture and destroying body.");
        this.health=-1;

        if (pigBody != null) {
            world.destroyBody(pigBody);
            pigBody = null;
        }
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    public void takeDamage(float damage) {
        this.health -= damage;
    }

    private void createPigBody(World world, Vector2 position) {
        BodyDef pigBodyDef = new BodyDef();
        pigBodyDef.type = BodyDef.BodyType.DynamicBody;
        pigBodyDef.position.set(position);

        pigBody = world.createBody(pigBodyDef);

        CircleShape pigShape = new CircleShape();
        pigShape.setRadius((initialDiameter / 2) * PIXELS_TO_METERS);

        FixtureDef pigFixtureDef = new FixtureDef();
        pigFixtureDef.shape = pigShape;
        pigFixtureDef.density = initialDensity;

        pigBody.createFixture(pigFixtureDef);
        pigShape.dispose();
    }

    public void render(SpriteBatch batch) {
        if (health > 0 && this!=null) {
            float pigDiameterInPixels = initialDiameter * PIXELS_TO_METERS;
            float pigPosX = pigBody.getPosition().x;
            float pigPosY = pigBody.getPosition().y;

            batch.begin();
            batch.draw(texture, pigPosX - pigDiameterInPixels / 2,
                pigPosY - pigDiameterInPixels / 2,
                pigDiameterInPixels, pigDiameterInPixels);
            batch.end();
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    public Body getBody() {
        return pigBody;
    }

    public Vector2 getPosition() {
        if (pigBody != null) {
            return pigBody.getPosition();
        }
        return new Vector2(0, 0);
    }

    // Custom serialization logic
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        oos.defaultWriteObject(); // Serialize non-transient fields
    }

    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserialize non-transient fields

        // Reinitialize transient fields
        this.texture = new Texture(texturePath); // Reload texture
        if (world != null) {
            createPigBody(world, initialPosition); // Recreate body
        }
    }
}
