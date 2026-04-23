package game.action;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.item.Apple;
import game.item.SterilisationBox;
import game.status.PoisonStatus;

/**
 * An action for consuming an Apple from the actor's inventory.
 * If the actor carries a SterilisationBox, the apple is safe and heals 3 HP.
 * Otherwise, it is toxic and applies a PoisonStatus (1 damage/turn for 5 turns).
 * The apple is removed from the inventory after consumption.
 */
public class EatAppleAction extends Action {

    /** The apple being eaten. */
    private final Apple apple;

    /**
     * Constructor.
     *
     * @param apple the apple to eat
     */
    public EatAppleAction(Apple apple) {
        this.apple = apple;
    }

    /**
     * Consumes the apple. Effect depends on whether the actor carries a SterilisationBox.
     *
     * @param actor The actor eating the apple.
     * @param map   The map the actor is on.
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Remove apple from inventory regardless of outcome
        actor.getInventory().remove(apple);

        boolean hasSterilisationBox = !actor.getInventory()
                .getItemsAs(SterilisationBox.class).isEmpty();

        if (hasSterilisationBox) {
            actor.heal(3);
            return actor + " eats the sterilised apple and recovers 3 HP.";
        } else {
            actor.addStatus(new PoisonStatus(5));
            return actor + " eats the rotten apple and is poisoned! (1 damage/turn for 5 turns)";
        }
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats the Apple";
    }
}