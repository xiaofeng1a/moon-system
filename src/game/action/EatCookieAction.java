package game.action;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.item.Cookies;
import game.item.SterilisationBox;

/**
 * An action for eating one cookie from a Cookies pack in the actor's inventory.
 * Without a SterilisationBox, each cookie permanently reduces the actor's max HP by 1.
 * With one, each cookie heals the actor for 1 HP.
 * The pack is removed from the inventory once all 5 cookies are consumed.
 */
public class EatCookieAction extends Action {

    /** The cookie pack being eaten from. */
    private final Cookies cookies;

    /**
     * Constructor.
     *
     * @param cookies the cookie pack to eat from
     */
    public EatCookieAction(Cookies cookies) {
        this.cookies = cookies;
    }

    /**
     * Eats one cookie. Effect depends on whether the actor carries a SterilisationBox.
     * Removes the pack from the inventory if all cookies are consumed.
     *
     * @param actor The actor eating the cookie.
     * @param map   The map the actor is on.
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        cookies.consume();

        boolean hasSterilisationBox = !actor.getInventory()
                .getItemsAs(SterilisationBox.class).isEmpty();

        String result;
        if (hasSterilisationBox) {
            actor.heal(1);
            result = actor + " eats a sterilised cookie and recovers 1 HP.";
        } else {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, 1);
            result = actor + " eats a stale cookie — maximum HP permanently reduced by 1.";
        }

        if (cookies.isDepleted()) {
            actor.getInventory().remove(cookies);
            result += " The empty cookie pack is discarded.";
        }

        return result;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats a Cookie (" + cookies.getCookiesRemaining() + " remaining)";
    }
}