package game.ground;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * A small, stationary body of mysterious liquid on the ground.
 * In a standard video game, this would just be water. On a deprecated moon
 * in the Eclipse Nebula, it could be anything from spilled engine coolant to
 * highly corrosive alien saliva. Step in it at your own risk.
 */
public class Puddle extends Ground {
    public Puddle() {
        super('~', "Puddle");
    }
}
