package game.ground;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.action.Consumable;
import game.action.ConsumeAction;
import game.item.SterilisationBox;
import game.status.PoisonStatus;

/**
 * A small, stationary body of mysterious liquid on the ground.
 * In a standard video game, this would just be water. On a deprecated moon
 * in the Eclipse Nebula, it could be anything from spilled engine coolant to
 * highly corrosive alien saliva. Step in it at your own risk.
 *
 * <p>Implements {@link Consumable} so workers standing directly on it can drink
 * from it via the shared {@link ConsumeAction}, without needing a dedicated action class.
 */
public class Puddle extends Ground implements Consumable {

    public Puddle() {
        super('~', "Puddle");
    }

    /**
     * Drinking from the puddle poisons the actor for 3 turns, or heals 1 HP
     * if they carry a {@link SterilisationBox}.
     *
     * @param actor the actor drinking from this puddle
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String consume(Actor actor, GameMap map) {
        boolean hasSterilisationBox = !actor.getInventory()
                .getItemsAs(SterilisationBox.class).isEmpty();

        if (hasSterilisationBox) {
            actor.heal(1);
            return actor + " drinks purified puddle water and recovers 1 HP.";
        } else {
            actor.addStatus(new PoisonStatus(3));
            return actor + " drinks from the puddle and is poisoned! (1 damage/turn for 3 turns)";
        }
    }

    @Override
    public String consumeMenuDescription(Actor actor) {
        return actor + " drinks from the Puddle";
    }

    /**
     * Offers a {@link ConsumeAction} only when the actor is standing directly
     * on this puddle tile (direction is empty string).
     *
     * @param actor     the actor acting
     * @param location  the current location
     * @param direction empty string if the actor is on this tile, otherwise a compass direction
     * @return an ActionList containing the drink action if on the tile
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty()) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }
}
