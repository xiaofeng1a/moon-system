package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * An action for using the {@link FirstAidKit}.
 * Permanently increases the actor's maximum health by 1 point and immediately restores
 * their current health to the new maximum, then starts the kit's cooldown timer.
 */
public class UseFirstAidKitAction extends Action {

    /** The first aid kit being used. */
    private final FirstAidKit firstAidKit;

    /**
     * Constructor.
     *
     * @param firstAidKit the kit to use
     */
    public UseFirstAidKitAction(FirstAidKit firstAidKit) {
        this.firstAidKit = firstAidKit;
    }

    /**
     * Increases the actor's maximum health by 1, restores health to full,
     * and starts the kit's cooldown.
     *
     * @param actor The actor using the kit.
     * @param map   The map the actor is on.
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Increase max HP by 1 and restore to new maximum
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, 1);
        int newMax = actor.getMaximumStatistic(ActorStatistics.HEALTH);
        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.UPDATE, newMax);

        firstAidKit.triggerCooldown();

        return actor + " uses the First Aid Kit: maximum health permanently increased by 1 and health fully restored.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses First Aid Kit (permanently +1 max HP, restore to full)";
    }
}