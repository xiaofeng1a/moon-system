package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.ground.Fire;
/**
 * A status effect applied to actors standing on a {@link Fire} tile.
 * Deals 1 damage per turn for 5 turns.
 */
public class BurnStatus implements Status {

    /** Number of burn turns remaining. */
    private int turnsRemaining;

    /** The number of turns this burn lasts. */
    private static final int BURN_TURNS = 5;

    /**
     * Constructor. Always lasts {@value BURN_TURNS} turns.
     */
    public BurnStatus() {
        this.turnsRemaining = BURN_TURNS;
    }

    /**
     * Deals 1 damage to the entity each tick and decrements the turn counter.
     *
     * @param currEntity the entity this status is attached to
     * @param location   the location of the entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (currEntity instanceof Actor actor) {
            actor.hurt(1);
        }
        turnsRemaining--;
    }

    /**
     * @return true while there are burn turns remaining
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
}