package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A status effect that deals 1 damage per turn for a fixed number of turns.
 * Applied by toxic apples, unsterilised puddles, and any other poisonous source.
 * There is no way to remove this status early; it must be waited out.
 */
public class PoisonStatus implements Status {

    /** Turns of poison damage remaining. */
    private int turnsRemaining;

    /**
     * Constructor.
     *
     * @param turns the number of turns this poison lasts
     */
    public PoisonStatus(int turns) {
        this.turnsRemaining = turns;
    }

    /**
     * Deals 1 damage to the entity each tick and decrements the remaining turn counter.
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
     * @return true while there are turns of poison remaining
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
}