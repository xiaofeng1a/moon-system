package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.status.BurnStatus;

/**
 * A fire tile spawned when a Lantern leaks and ignites the ground.
 * The fire lasts for 5 turns. Any actor standing on a fire tile receives
 * a {@link BurnStatus} (1 damage per turn for 5 turns) each turn they remain on it.
 */
public class Fire extends Ground {

    /** Number of turns this fire tile will remain before burning out. */
    private int turnsRemaining;

    private static final int FIRE_DURATION = 5;

    /**
     * Constructor.
     */
    public Fire() {
        super('^', "Fire");
        this.turnsRemaining = FIRE_DURATION;
    }

    /**
     * Each turn, applies a {@link BurnStatus} to any actor standing on this tile,
     * then decrements the fire's lifetime. When expired, restores the tile to {@link Dirt}.
     *
     * @param location the location of this fire tile
     */
    @Override
    public void tick(Location location) {
        Actor actor = location.getActor();
        if (actor != null) {
            actor.addStatus(new BurnStatus());
        }

        turnsRemaining--;

        // Once expired, replace this tile with floor
        if (turnsRemaining <= 0) {
            location.setGround(new Floor());
        }
    }
}