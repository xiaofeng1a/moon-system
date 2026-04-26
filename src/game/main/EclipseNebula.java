package game.main;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actor.ContractedWorker;
import game.ground.*;
import game.inventory.WeightLimitedInventory;
import game.item.*;

import java.util.Arrays;
import java.util.List;

/**
 * This class handles the miracle of creation, translating a bunch of periods
 * and hashtags into a sprawling, functional sci-fi facility.
 *
 * <p>Each worker starts with their own {@link WeightLimitedInventory} (cap: 50 units)
 * and a personal {@link Flask}. The three shared items — {@link AccessCard},
 * {@link FirstAidKit}, and {@link SterilisationBox} — are each instantiated exactly
 * once and placed aboard the armored ship, forcing players to split responsibilities.
 */
public class EclipseNebula extends World {

    public EclipseNebula(Display display) {
        super(display);
    }

    /**
     * Initialize maps, actors, items, and grounds of the game world.
     *
     * @throws Exception if anything goes wrong during setup
     */
    public void initialise() throws Exception {
        // --- Ground types ---
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', Door::new);

        // --- Map layout ---
        List<String> moon99Deprecated = Arrays.asList(
                "....................########################################",
                "...#######..........#__________________#___________________#",
                "...#_____#..........=__________________=___________________#",
                "...#_____=...~......#__________________#___________________#",
                "...#_____#..~~~.....########=#####=#####___#############___#",
                "...#######.~~~~.....#______#_#_________#___#___________#___#",
                ".........~~~~.......#______#_#_________#####___________#####",
                "....................#______=_#_________#___________________#",
                "......~.............#______#_#_________#___________________#",
                ".....~~~............#______#_###########___#############___#",
                ".....~..............#______#___________#___#___________#___#",
                "....................=______#___________=___=___________=___#",
                "....................#______#############___#############___#",
                ".........~~~~.......#______#___________#####################",
                "........~~~~~~......#______#___________=___________________#",
                ".........~~~~.......#______#___________#___________________#",
                "....................#______#############___#############___#",
                "....................#______#___________#___#___________#___#",
                "..~.................#______=___________=___=___________=___#",
                "....................########################################"
        );

        GameMap moon99DeprecatedMap = new GameMap("99-Deprecated", groundCreator, moon99Deprecated);
        this.addGameMap(moon99DeprecatedMap);

        // --- Shared items aboard the armored ship (one of each) ---
        // Workers must decide who carries what; weight limits enforce cooperation.
        moon99DeprecatedMap.at(4, 2).addItem(new AccessCard());
        moon99DeprecatedMap.at(4, 3).addItem(new FirstAidKit());
        moon99DeprecatedMap.at(4, 4).addItem(new SterilisationBox());
        // --- Apples scattered across the facility (3x) ---
        moon99DeprecatedMap.at(30, 1).addItem(new Apple());
        moon99DeprecatedMap.at(45, 10).addItem(new Apple());
        moon99DeprecatedMap.at(50, 17).addItem(new Apple());

        // --- Cookies scattered across the facility (3x) ---
        moon99DeprecatedMap.at(25, 5).addItem(new Cookies());
        moon99DeprecatedMap.at(35, 14).addItem(new Cookies());
        moon99DeprecatedMap.at(48, 5).addItem(new Cookies());

        // --- Lanterns scattered across the facility (2x) ---
        moon99DeprecatedMap.at(22, 8).addItem(new Lantern());
        moon99DeprecatedMap.at(42, 15).addItem(new Lantern());

        // --- Lanterns scattered across the facility (2x) ---
        moon99DeprecatedMap.at(25, 10).addItem(new CRTMonitor());
        moon99DeprecatedMap.at(46, 18).addItem(new FloppyDisk());

        // --- Workers: each gets their own inventory and flask ---
        ContractedWorker contractedWorker1 = createWorker("#1 Bob");
        ContractedWorker contractedWorker2 = createWorker("#2 Tom");
        ContractedWorker contractedWorker3 = createWorker("#3 Sarah");
        ContractedWorker contractedWorker4 = createWorker("#4 Julie");
        ContractedWorker contractedWorker5 = createWorker("#5 Rick");

        this.addPlayer(contractedWorker1, moon99DeprecatedMap.at(6, 2));
        this.addPlayer(contractedWorker2, moon99DeprecatedMap.at(7, 2));
        this.addPlayer(contractedWorker3, moon99DeprecatedMap.at(5, 2));
        this.addPlayer(contractedWorker4, moon99DeprecatedMap.at(6, 4));
        this.addPlayer(contractedWorker5, moon99DeprecatedMap.at(7, 4));
    }

    /**
     * Helper method that creates a {@link ContractedWorker} with a personal
     * {@link WeightLimitedInventory} (cap: 50 units) preloaded with a fresh {@link Flask}.
     *
     * @param name the worker's display name
     * @return a fully initialised ContractedWorker
     */
    private ContractedWorker createWorker(String name) {
        WeightLimitedInventory inventory = new WeightLimitedInventory(50);
        inventory.add(new Flask()); // every worker starts with their own flask (3 units)
        return new ContractedWorker(name, 'ඞ', 10, inventory);
    }
}