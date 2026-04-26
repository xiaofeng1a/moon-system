package game.ground;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Slime;
import game.actor.Undead;

import java.util.Random;

/**
 * A hole in the moon's facility floor. Every 20 game turns, it spawns either
 * an Undead or a Slime with equal probability (50/50), provided
 * the tile is not already occupied by another actor.
 *
 * <p>Actors can walk over a hole freely — it does not block movement.
 */
public class Hole extends Ground {

    /** Tracks how many turns have passed since the last spawn. */
    private int turnCounter = 0;

    /** Number of turns between spawns. */
    private static final int SPAWN_INTERVAL = 20;

    private final Random random = new Random();

    public Hole() {
        super('o', "Hole");
    }

    /**
     * Called every turn. Increments the counter and spawns a creature every
     * {@value SPAWN_INTERVAL} turns if the tile is unoccupied.
     *
     * @param location the location of this hole
     */
    @Override
    public void tick(Location location) {
        turnCounter++;
        if (turnCounter >= SPAWN_INTERVAL) {
            turnCounter = 0;
            if (!location.containsAnActor()) {
                Actor creature = random.nextBoolean() ? new Undead() : new Slime();
                try {
                    location.addActor(creature);
                } catch (GameEngineException e) {
                    // If spawning fails for any reason, silently skip this turn
                }
            }
        }
    }
}