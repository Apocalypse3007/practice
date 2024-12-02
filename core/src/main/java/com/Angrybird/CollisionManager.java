package com.Angrybird;

import com.badlogic.gdx.physics.box2d.*;

public class CollisionManager implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        // No changes needed in beginContact for force calculation
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Object objA = contact.getFixtureA().getBody().getUserData();
        Object objB = contact.getFixtureB().getBody().getUserData();

        // Check if the ground is involved in the collision
        if (objA instanceof GameObject && contact.getFixtureB().getBody().getType() == BodyDef.BodyType.StaticBody) {
            // objA is a dynamic body colliding with ground (objB is the static body)
            GameObject gameObject = (GameObject) objA;
            float totalImpulse = 0f;
            for (float normalImpulse : impulse.getNormalImpulses()) {
                totalImpulse += normalImpulse;
            }
            gameObject.onCollision(contact.getFixtureB().getBody(), totalImpulse);
        } else if (objB instanceof GameObject && contact.getFixtureA().getBody().getType() == BodyDef.BodyType.StaticBody) {
            // objB is a dynamic body colliding with ground (objA is the static body)
            GameObject gameObject = (GameObject) objB;
            float totalImpulse = 0f;
            for (float normalImpulse : impulse.getNormalImpulses()) {
                totalImpulse += normalImpulse;
            }
            gameObject.onCollision(contact.getFixtureA().getBody(), totalImpulse);
        }
    }

}
