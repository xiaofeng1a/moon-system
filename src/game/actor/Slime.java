package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.action.Consumable;
import game.behaviour.WanderBehaviour;
import game.inventory.BasicInventory;

import java.util.List;

/**
 * A non-hostile, gluttonous slime. Has 25 HP.
 *
 * <p>Cannot attack players or other creatures. Wanders the facility and consumes
 * Consumable items directly from the ground without picking them up.
 * The same effects apply to the Slime as they would to a worker.
 *
 * <p>Behaviour priority:
 * <ol>
 *   <li>If a Consumable item is on the current tile, eat it directly.</li>
 *   <li>Otherwise, wander randomly using shared WanderBehaviour.</li>
 * </ol>
 *
 * <p>Eat-from-ground logic is inlined as an anonymous Action since only Slime
 * needs this — no separate class needed.
 */
public class Slime extends Actor {

    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();

    /**
     * Constructor.
     */
    public Slime() {
        super("Slime", 'S', 25, new BasicInventory());
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            map.removeActor(this);
            return new DoNothingAction();
        }

        Location location = map.locationOf(this);

        // Priority 1: eat a consumable directly from the current tile
        // Inlined as anonymous Action — only Slime needs this logic
        List<Item> items = location.getItems();
        for (Item item : items) {
            if (item instanceof Consumable consumable) {
                return new Action() {
                    @Override
                    public String execute(Actor actor, GameMap map) {
                        String result = consumable.consume(actor, map);
                        location.removeItem(item);
                        return result;
                    }
                    @Override
                    public String menuDescription(Actor actor) {
                        return actor + " consumes " + item + " from the ground";
                    }
                };
            }
        }

        // Priority 2: wander randomly
        Action wander = wanderBehaviour.operate(this, location);
        if (wander != null) return wander;

        return new DoNothingAction();
    }

    @Override
    public String unconscious(GameMap map) {
        map.removeActor(this);
        return this + " dissolves into a puddle of goo.";
    }
}