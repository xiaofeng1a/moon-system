package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

/**
 * A class representing a solid wall. Yes, that's it.
 */
public class Wall extends Ground {
    public Wall() {
        super('#', "Wall");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}