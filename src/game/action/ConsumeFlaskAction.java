package game.action;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.item.Flask;

/**
 * An action representing the desperate, mid-combat decision to chug whatever
 * liquid is sloshing around inside a flask.
 * Because nothing cures catastrophic injuries quite like aggressive hydration.
 *
 * @see Action
 * @see Flask
 */
public class ConsumeFlaskAction extends Action {

    /** The flask being consumed. */
    private final Flask flask;

    /**
     * Constructor.
     *
     * @param flask the flask to consume
     */
    public ConsumeFlaskAction(Flask flask) {
        this.flask = flask;
    }

    /**
     * Consumes one use of the flask and heals the actor by 1 HP.
     *
     * @param actor The actor consuming the flask.
     * @param map   The map the actor is on.
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        flask.consume();
        actor.heal(1);
        return actor + " drinks from the flask, restoring 1 point of health.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes flask.";
    }
}