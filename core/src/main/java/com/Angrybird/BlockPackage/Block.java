package com.Angrybird.BlockPackage;

import com.Angrybird.GameObject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.Angrybird.Main.*;

public class Block extends GameObject {
    private static final long serialVersionUID = 1L; // Serialization version

    private transient Texture block; // Marked as transient for serialization
    private transient TextureRegion blockRegion; // Marked as transient
    private transient Body blockBody; // Marked as transient
    private transient World world; // Marked as transient

    private String texturePath; // Path to reload the texture during deserialization
    private Vector2 initialPosition; // Save position to recreate block body
    private float initialHealth; // Save initial health
    private float blockWidth = BLOCK_WIDTH; // Width of the block
    private float blockHeight = BLOCK_HEIGHT; // Height of the block

    public Block(World world, Vector2 position, String image_url, float health) {
        this.world = world;
        this.texturePath = image_url; // Save the texture path for reinitialization
        this.initialPosition = position.cpy(); // Save the initial position
        this.initialHealth = health; // Save the initial health
        this.health = health;

        // Initialize the texture and body
        block = new Texture(image_url);
        blockRegion = new TextureRegion(block);
        createBlockBody(world, position);
        blockBody.setUserData(this);
    }

    @Override
    public void destroy() {
        System.out.println("Block collapsed! Removing texture and destroying body.");

        if (blockBody != null && world != null) {
            world.destroyBody(blockBody);
            blockBody = null;
        }

        if (block != null) {
            block.dispose();
            block = null;
        }
    }

    public void takeDamage(float damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.destroy();
        }
    }

    private void createBlockBody(World world, Vector2 position) {
        BodyDef blockBodyDef = new BodyDef();
        blockBodyDef.position.set(position);
        blockBodyDef.type = BodyDef.BodyType.DynamicBody;

        blockBody = world.createBody(blockBodyDef);
        PolygonShape blockShape = new PolygonShape();
        blockShape.setAsBox(blockWidth / 2, blockHeight / 2); // Use global width and height

        FixtureDef blockFixtureDef = new FixtureDef();
        blockFixtureDef.shape = blockShape;
        blockFixtureDef.density = 0.5f;

        blockBody.createFixture(blockFixtureDef);
        blockShape.dispose();
    }

    public void render(SpriteBatch batch) {
        if (health <= 0) {
            return; // Skip rendering if health is zero
        }
        Vector2 position = blockBody.getPosition(); // Get blockBody's position in meters

        float x = position.x - blockWidth / 2; // Offset for center of texture
        float y = position.y - blockHeight / 2; // Offset for center of texture
        float rotation = blockBody.getAngle() * MathUtils.radiansToDegrees; // Convert from radians to degrees

        batch.begin();
        batch.draw(blockRegion, x, y, blockWidth / 2, blockHeight / 2, blockWidth, blockHeight, 1, 1, rotation);
        batch.end();
    }

    public void dispose() {
        if (block != null) {
            block.dispose();
            block = null;
        }
    }

    public Body getBody() {
        return blockBody;
    }

    public Vector2 getPosition() {
        if (blockBody != null) {
            return blockBody.getPosition();
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
        block = new Texture(texturePath); // Reload texture
        blockRegion = new TextureRegion(block); // Recreate texture region
        if (world != null) {
            createBlockBody(world, initialPosition); // Recreate block body
        }
    }
}
