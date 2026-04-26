package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.action.Consumable;
import game.action.ConsumeAction;
import game.status.PoisonStatus;

/**
 * A spoiled fruit found on the moon's facility. Weighs 1 unit.
 * Toxic if eaten without a {@link SterilisationBox} — poisons the consumer
 * for 1 damage per turn for 5 turns.
 * Safe if the consumer carries a {@link SterilisationBox} — heals for 3 HP instead.
 * Disappears from the inventory once consumed.
 */
public class Apple extends Item implements Consumable {

    public Apple() {
        super("Apple", 'ó');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }

    /**
     * Consumes the apple. Poisons the actor for 5 turns, or heals 3 HP if they
     * carry a {@link SterilisationBox}. Removes itself from the inventory after use.
     *
     * @param actor the actor consuming this apple
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String consume(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

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
    public String consumeMenuDescription(Actor actor) {
        return actor + " eats the Apple";
    }

    /**
     * Exposes a generic ConsumeAction when carried.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }
}