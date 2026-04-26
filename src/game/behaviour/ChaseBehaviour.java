package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.*;

/**
 * A behaviour that moves an actor toward the nearest actor of a specified type,
 * using Breadth-First Search (BFS) to find the shortest path.
 *
 * <p>Used by Undead when the alarm is active — instead of wandering
 * randomly, Undead actively hunts the nearest ContractedWorker.
 *
 * <p>SOLID — Single Responsibility: this class only handles pathfinding.
 * It does not know about the alarm, about Undead, or about workers specifically.
 *
 * <p>SOLID — Open/Closed: any future actor that needs to chase a target type
 * can reuse this behavior by passing a different {@code targetType}.
 */
public class ChaseBehaviour implements Behaviour<Actor, Action> {

    /** The type of actor to chase. */
    private final Class<? extends Actor> targetType;

    /**
     * Constructor.
     *
     * @param targetType the class of actor to chase (e.g. ContractedWorker.class)
     */
    public ChaseBehaviour(Class<? extends Actor> targetType) {
        this.targetType = targetType;
    }

    /**
     * Uses BFS from the actor's current location to find the nearest target.
     * Returns a move action toward the target, or null if no target is reachable.
     *
     * @param actor    the actor performing the chase
     * @param location the actor's current location
     * @return a MoveActorAction toward the nearest target, or null
     */
    @Override
    public Action operate(Actor actor, Location location) {
        // find the shortest path to nearest target
        Queue<Location> queue = new LinkedList<>();
        Map<Location, Location> cameFrom = new HashMap<>();

        queue.add(location);
        cameFrom.put(location, null);

        Location targetLocation = null;

        while (!queue.isEmpty()) {
            Location current = queue.poll();

            // Check if this location has a matching target actor
            Actor occupant = current.getActor();
            if (occupant != null && targetType.isInstance(occupant) && current != location) {
                targetLocation = current;
                break;
            }

            // Expand neighbors
            for (Exit exit : current.getExits()) {
                Location neighbour = exit.getDestination();
                if (!cameFrom.containsKey(neighbour)) {
                    // Include passable tiles AND target location (even if occupied)
                    if (neighbour.canActorEnter(actor) || targetType.isInstance(neighbour.getActor())) {
                        cameFrom.put(neighbour, current);
                        queue.add(neighbour);
                    }
                }
            }
        }

        if (targetLocation == null) return null;

        // Trace back to find the first step from the actor's location
        Location step = targetLocation;
        while (cameFrom.get(step) != location) {
            step = cameFrom.get(step);
            if (step == null) return null;
        }

        // Return a move action toward the first step
        for (Exit exit : location.getExits()) {
            if (exit.getDestination() == step) {
                return step.getMoveAction(actor, exit.getName(), exit.getHotKey());
            }
        }

        return null;
    }
}