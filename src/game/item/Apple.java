package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.action.EatAppleAction;

/**
 * A spoiled fruit found on the moon's facility. Weighs 1 unit.
 * Toxic if eaten without a {@link SterilisationBox} — poisons the consumer
 * for 1 damage per turn for 5 turns.
 * Safe if the consumer carries a {@link SterilisationBox} — heals for 3 HP instead.
 * Disappears from the inventory once consumed.
 */
public class Apple extends Item {

    public Apple() {
        super("Apple", 'ó');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }

    /**
     * When carried, exposes an EatAppleAction.
     *
     * @param owner the actor carrying this apple
     * @param map   the current game map
     * @return an ActionList containing the eat action
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new EatAppleAction(this));
        return actions;
    }
}
