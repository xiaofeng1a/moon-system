package game.ground;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * While other classes get to be security doors, mysterious flasks, or highly
 * stressed {@code ContractedWorker}s, this class humbly accepts its role as
 * the thing everyone walks all over.
 *
 * @author Adrian Kristanto
 */
public class Dirt extends Ground {
    public Dirt() {
        super('.', "Dirt");
    }
}
