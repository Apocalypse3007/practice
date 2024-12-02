package com.Angrybird;

import com.Angrybird.BirdPackage.Bird;
import com.Angrybird.BirdPackage.Bomb;
import com.Angrybird.BirdPackage.Chuck;
import com.Angrybird.BirdPackage.Red;
import com.Angrybird.BlockPackage.Block;
import com.Angrybird.PigPackage.Pig;
import com.Angrybird.StaticElements.Slingshot;
import com.Angrybird.StaticElements.Structure;
import com.Angrybird.StaticElements.ThreadLine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

public class LaunchHandler {
    public List<Bird> birds;
    public int currentBirdIndex;
    public Slingshot slingshot;
    public ThreadLine thread;
    public boolean enableDrag;
    public boolean birdInMotion;
    public PauseScreen pauseScreen;
    public boolean specialAbilityUsed;
    public Structure structure;
    public Main main;

    // Constants
    public static final float MAX_DRAG_DISTANCE = 1f;
    public static final float LAUNCH_FORCE_MULTIPLIER = 550f;
    public static final float MIN_VELOCITY_THRESHOLD = 0.1f;
    public static final float WIN_CHECK_DELAY = 5f; // 5 seconds delay after the last bird is launched

    // Cached vectors for reuse
    public final Vector2 touchPos = new Vector2();
    public final Vector2 worldCoords = new Vector2();
    public final Vector2 dragPos = new Vector2();
    public final Vector2 launchDirection = new Vector2();
    public final Viewport viewport;
    public Vector2 slingPOS = new Vector2();

    public boolean hasLaunched; // To track if the current bird has been launched
    public float launchTimer; // To track time since the bird was launched
    public float winTimer = -1f;
    public Object currentBird;
    private InputProcessor inputProcessor;

    public LaunchHandler(Main main, List<Bird> birds, Structure structure, Slingshot slingshot, ThreadLine thread, PauseScreen pauseScreen, Viewport viewport) {
        this.birds = birds;
        this.currentBirdIndex = 0;
        this.slingshot = slingshot;
        this.thread = thread;
        this.viewport = viewport;
        this.enableDrag = false;
        this.pauseScreen = pauseScreen;
        this.specialAbilityUsed = false;
        this.slingPOS = slingshot.getSlingStartPos();
        this.structure = structure;
        this.main = main;
        this.hasLaunched = false; // Initialize the launch state
        this.launchTimer = 0f; // Initialize the launch timer
    }

    public LaunchHandler(Main main, List<Bird> birds, List<Pig> pigs, Viewport viewport) {
        this.birds = birds;
        this.currentBirdIndex = 0;
        this.viewport = viewport;
        this.enableDrag = false;
        this.specialAbilityUsed = false;
        this.slingPOS = new Vector2(3,3);
        this.main = main;
        this.hasLaunched = false; // Initialize the launch state
        this.launchTimer = 0f; // Initialize the launch timer
        this.pauseScreen = new PauseScreen();
        this.structure = new Structure(birds, pigs);
        this.inputProcessor = inputProcessor;
    }

    public void handleInput() {
        if (birds!=null && currentBirdIndex >= birds.size()) {
            System.out.println("Reached");
            if (!birdInMotion && winTimer == -1f) {
                winTimer = WIN_CHECK_DELAY;
            }
            if (winTimer >= 0f) {
                System.out.println(winTimer);
                float deltaTime = Gdx.graphics.getDeltaTime();
                winTimer -= deltaTime;
                if (winTimer <= 0f) {
                    if (checkWinStatus()) {
                        System.out.println("You win!");
                        main.showWinScreen();
                    } else {
                        System.out.println("You lost! No more birds left.");
                        main.showLoseScreen();
                    }
                    winTimer = -1f; // Reset the win timer
                }
            }
            return; // Prevent further processing after all birds are used
        }

        Bird currentBird = null;
        if (birds!=null) {currentBird = birds.get(currentBirdIndex);}
        float deltaTime = Gdx.graphics.getDeltaTime();
        Vector2 slingStartPos = new Vector2(slingPOS).add(new Vector2(1, 1.7f));

        if (pauseScreen.handleInput()) {
            return;
        }



        if (!hasLaunched) {
            // Handling touch input for dragging and launching the bird
            if (Gdx.input.justTouched() && !pauseScreen.isPaused()) {
                touchPos.set(Gdx.input.getX(), Gdx.input.getY());
                worldCoords.set(viewport.unproject(new Vector2(touchPos)));

                if (currentBird!=null && currentBird.getBody() != null) {
                    currentBird.getBody().setType(BodyDef.BodyType.KinematicBody);
                    float distanceToSling = worldCoords.dst(slingStartPos);
                    dragPos.set(distanceToSling > MAX_DRAG_DISTANCE ?
                        worldCoords.sub(slingStartPos).nor().scl(MAX_DRAG_DISTANCE).add(slingStartPos) :
                        worldCoords);

                    enableDrag = true;
                    currentBird.getBody().setTransform(dragPos, 0);
                    thread.setVisible(true);
                }
            } else if (Gdx.input.isTouched() && enableDrag && !pauseScreen.isPaused()) {
                touchPos.set(Gdx.input.getX(), Gdx.input.getY());
                worldCoords.set(viewport.unproject(new Vector2(touchPos)));

                if (currentBird!=null && currentBird.getBody() != null) {
                    float distanceToSling = worldCoords.dst(slingStartPos);
                    dragPos.set(distanceToSling > MAX_DRAG_DISTANCE ?
                        worldCoords.sub(slingStartPos).nor().scl(MAX_DRAG_DISTANCE).add(slingStartPos) :
                        worldCoords);

                    currentBird.getBody().setTransform(dragPos, 0);
                    thread.setVisible(true);
                }
            } else if (enableDrag && !pauseScreen.isPaused()) {
                enableDrag = false;

                if (currentBird!=null && currentBird.getBody() != null) {
                    Vector2 birdPos = currentBird.getBody().getPosition();
                    launchDirection.set(slingStartPos).sub(birdPos).nor();
                    float stretchDistance = slingStartPos.dst(birdPos);

                    currentBird.getBody().setType(BodyDef.BodyType.DynamicBody);
                    currentBird.getBody().applyLinearImpulse(launchDirection.scl(stretchDistance * LAUNCH_FORCE_MULTIPLIER * deltaTime),
                        currentBird.getBody().getWorldCenter(), true);

                    birdInMotion = true;
                    hasLaunched = true; // Mark the bird as launched
                    launchTimer = 0f; // Reset the launch timer
                    specialAbilityUsed = false;
                    thread.setVisible(false);
                }
            }
        }

        if (birdInMotion && Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !specialAbilityUsed) {
            executeSpecialAbility(currentBird);
            specialAbilityUsed = true;
        }


        if (birdInMotion) {
            Vector2 velocity = currentBird.getBody() != null ? currentBird.getBody().getLinearVelocity() : new Vector2(0, 0);
            launchTimer += deltaTime; // Increment the launch timer

            if (currentBird.checkCollisionStatus() || velocity.len() < MIN_VELOCITY_THRESHOLD || launchTimer >= 5f) {
                // Move to the next bird after collision, low velocity, or 5 seconds
                dealDamageIfNeeded(currentBird);
                moveToNextBird();
            }
        }

        // Check for out of bounds pigs
        checkForOutOfBoundsPigs();

        // Check for win condition before each bird launch
        if (checkWinStatus()) {
            checkForOutOfBoundsPigs();
            System.out.println("You win!");
            main.showWinScreen();
        }
    }

    public void launch(Bird bird, int stretchDistance) {
        bird.getBody().setLinearVelocity(new Vector2(5,5));
        birds.remove(bird);
    }


    // Move to the next bird after the current bird finishes
    public void moveToNextBird() {
        birdInMotion = false;
        hasLaunched = false; // Reset the launch state for the next bird
        launchTimer = 0f; // Reset the launch timer

        currentBirdIndex++;

    }

    // Utility method to check if all pigs are destroyed (win condition)
    public boolean checkWinStatus() {
        for (Pig pig : structure.getPigs()) {
            if (pig.isAlive()) {
                return false; // Not all pigs are destroyed
            }
        }
        return true; // All pigs are destroyed
    }

    // Check if any pig is out of bounds and destroy it
    public void checkForOutOfBoundsPigs() {
        for (Pig pig : structure.getPigs()) {
            if (isOutOfViewport(pig.getPosition())) {
                pig.destroy();
                System.out.println("Out of bound pig destroyed");
            }
        }
    }

    // Check if an object is out of the viewport bounds
    public boolean isOutOfViewport(Vector2 position) {
        float viewportWidth = viewport.getWorldWidth();
        float viewportHeight = viewport.getWorldHeight();
        return position.x < 0 || position.x > viewportWidth || position.y < 0 || position.y > viewportHeight;
    }



    public void dealDamageIfNeeded(Bird currentBird) {
        if (currentBird instanceof Bomb) {
            dealDamageInRadius(currentBird.getBody().getPosition(), 2.0f);
        }
    }

    public void dealDamageInRadius(Vector2 position, float radius) {
        for (Pig pig : structure.getPigs()) {
            if (pig.getPosition().dst(position) <= radius) {
                pig.takeDamage(5);
                System.out.println("Damaged pig at " + pig.getPosition() + " within radius.");
            }
        }

        for (Block block : structure.getBlocks()) {
            if (block.getPosition().dst(position) <= radius) {
                block.takeDamage(5);
                System.out.println("Damaged block at " + block.getPosition() + " within radius.");
            }
        }
    }

    public void executeSpecialAbility(Bird bird) {
        if (bird instanceof Red) {
            System.out.println("Red bird special ability: None");
        } else if (bird instanceof Chuck) {
            Vector2 velocity = bird.getBody().getLinearVelocity();
            bird.getBody().setLinearVelocity(velocity.scl(1.5f));
            System.out.println("Chuck bird special ability: Speed boost");
        } else if (bird instanceof Bomb) {
            dealDamageInRadius(bird.getBody().getPosition(), 2.0f);
            bird.getBody().setLinearVelocity(0, 0);
            System.out.println("Bomb bird special ability: Explosive damage");
        }

        moveToNextBird();
    }


}
