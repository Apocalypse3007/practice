package com.Angrybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class GameObject implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures compatibility during deserialization

    public float health;
    public transient Body body; // Transient because Body is not serializable
    public Texture texture; // Texture of the object
    public transient World world; // Transient because World is not serializable
    public boolean collision = false;
    public boolean launched = false;

    // Global list to store GameObjects that need to be destroyed
    private static List<GameObject> objectsToDestroy = new ArrayList<>();

    public GameObject() {
        this.health = 10f; // Default health
    }

    public void onCollision(Body other, float collisionForce) {
        if (launched) collision = true;

        float damageThreshold = 0.4f; // Example threshold
        if (collisionForce >= damageThreshold) {
            float damage = (collisionForce - damageThreshold) * 0.5f; // Scale damage
            health -= damage;
            System.out.println("Collision! Force: " + collisionForce + ", Damage: " + damage + ", Health: " + health);

            if (health <= 0) {
                collapse();
            }
        }
    }

    public void collapse() {
        objectsToDestroy.add(this);
    }

    // Process the objects that need to be destroyed after the world step
    public static void updateObjectsToDestroy() {
        for (GameObject obj : objectsToDestroy) {
            obj.destroy(); // Call destroy logic
        }
        objectsToDestroy.clear(); // Clear the list after processing
    }

    public Vector2 getPosition() {
        return body != null ? body.getPosition() : new Vector2(0, 0); // Return the position of the Box2D body
    }

    // Abstract method for destruction
    public abstract void destroy();

    // Custom serialization for GameObject class
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();  // Serialize default fields
        // Serialize body position and angle (or other important state)
        if (body != null) {
            out.writeFloat(body.getPosition().x);
            out.writeFloat(body.getPosition().y);
            out.writeFloat(body.getAngle());
        } else {
            out.writeFloat(0);  // Default position if body is null
            out.writeFloat(0);
            out.writeFloat(0);
        }
    }

    // Custom deserialization for GameObject class
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();  // Deserialize default fields

        // Deserialize body position and angle
        float posX = in.readFloat();
        float posY = in.readFloat();
        float angle = in.readFloat();

    }
}
