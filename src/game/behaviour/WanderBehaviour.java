package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A behavior that makes an actor wander randomly around the map.
 * Shared by any creature that needs random movement — currently Undead and Slime.
 * Define once, reuse everywhere. If wander logic changes, update here only.
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {

    private final Random random = new Random();

    /**
     * Collects all valid adjacent moves and picks one at random.
     * Returns null if no valid move exists.
     *
     * @param actor    the actor that wants to wander
     * @param location the actor's current location
     * @return a random MoveActorAction, or null if surrounded
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Action> moves = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(actor)) {
                moves.add(dest.getMoveAction(actor, exit.getName(), exit.getHotKey()));
            }
        }
        if (moves.isEmpty()) return null;
        return moves.get(random.nextInt(moves.size()));
    }
}