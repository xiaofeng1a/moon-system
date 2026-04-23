package game.ground;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Not lava. Not spikes. Not an elaborate trap. Just a perfectly flat surface
 * whose sole responsibility is preventing the {@code ContractedWorker} from
 * plummeting into the infinite vacuum of the Eclipse Nebula.
 *
 * @author Adrian Kristanto
 */
public class Floor extends Ground {
    public Floor() {
        super('_', "Floor");
    }
}
