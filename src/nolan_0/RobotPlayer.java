package nolan_0;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // Hello world! Standard output is very useful for debugging.
        // Everything you say here will be directly viewable in your terminal when you run a match!
      //  System.out.println("I'm a " + rc.getType() + " and I just got created! I have health " + rc.getHealth());

        // You can also use indicators to save debug notes in replays.
        rc.setIndicatorString("Hello world!");
        if (rc.getTeam() == Team.A) {
            rng.nextInt();
        }

        while (true) {
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.

            turnCount += 1;  // We have now been alive for one more turn!

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                // The same run() function is called for every robot on your team, even if they are
                // different types. Here, we separate the control depending on the RobotType, so we can
                // use different strategies on different robots. If you wish, you are free to rewrite
                // this into a different control structure!
                switch (rc.getType()) {
                    case HEADQUARTERS:     runHeadquarters(rc);  break;
                    case CARRIER: CarrierStrategy.runCarrier(rc);   break;
                    case LAUNCHER: LauncherStrategy.runLauncher(rc); break;
                    case BOOSTER: // Examplefuncsplayer doesn't use any of these robot types below.
                    case DESTABILIZER: // You might want to give them a try!
                    case AMPLIFIER:       break;
                }

            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

    /**
     * Run a single turn for a Headquarters.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runHeadquarters(RobotController rc) throws GameActionException {
        // boolean to see if HQ is under attack
        boolean underAttack = false;

        if (turnCount == 1) {
            Communication.addHeadquarter(rc);
        } else if (turnCount == 2) {
            Communication.updateHeadquarterInfo(rc);
        }

        // Check if any enemy robots are nearby.
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (RobotInfo robot : nearbyRobots) {
            // If there is a nearby enemy robot, attack it!
            if (rc.canAttack(robot.location)) {
                rc.attack(robot.location);
                underAttack = true;
            }
            // find direction of enemy robot and build a launcher in that direction
            Direction dir = rc.getLocation().directionTo(robot.location);
            // get location of square in direction of enemy robot
            MapLocation newLoc = rc.getLocation().add(dir);
            // if we can build a launcher, build it
            if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)){
                // try to build a launcher, if we can't build one, change directions and attempt up to 8 times
                for (int i = 0; i < 8; i++) {
                    if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                        rc.buildRobot(RobotType.LAUNCHER, newLoc);
                        break;
                    } else {
                        newLoc = newLoc.add(dir);
                    }
                }
            }
        }
        if (rc.canBuildAnchor(Anchor.STANDARD) && rc.getResourceAmount(ResourceType.ADAMANTIUM) > 100) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(Anchor.STANDARD));
        }
        // if early game, 75% chance to build a carrier, 25% chance to build a launcher
        // get random direction for building
        Direction dir = Direction.values()[rng.nextInt(8)];
        // get location of square with random direction
        MapLocation newLoc = rc.getLocation().add(dir);
        if (turnCount < 100) {
            if (rng.nextBoolean()) {
                // Let's try to build a carrier.
                rc.setIndicatorString("Trying to build a carrier");
                if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                    rc.buildRobot(RobotType.CARRIER, newLoc);
                }
            } else {
                // Let's try to build a launcher.
                if (rc.getActionCooldownTurns() != 0 || rc.getResourceAmount(ResourceType.MANA) < 5 * RobotType.LAUNCHER.buildCostMana)
                    return;
                int attempts = 0;
                int numPlaced = 0;
                while (numPlaced != 5 && attempts != 30){
                    attempts++;
                    if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                        rc.buildRobot(RobotType.LAUNCHER, newLoc);
                        numPlaced++;
                    }
                    else{
                        newLoc = newLoc.add(dir);
                    }
                }
            }
        }
        if (rng.nextBoolean()) {
            // Let's try to build a carrier.
            rc.setIndicatorString("Trying to build a carrier");
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }
        } else {
            // Let's try to build a launcher.
            if (rc.getActionCooldownTurns() != 0 || rc.getResourceAmount(ResourceType.MANA) < 5 * RobotType.LAUNCHER.buildCostMana)
                return;
            int attempts = 0;
            int numPlaced = 0;
            while (numPlaced != 5 && attempts != 30){
                attempts++;
                if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                    rc.buildRobot(RobotType.LAUNCHER, newLoc);
                    numPlaced++;
                }
                else{
                     dir = directions[rng.nextInt(directions.length)];
                    newLoc = rc.getLocation().add(dir);
                }
            }
            rc.setIndicatorString("Trying to build a launcher");
        }
        Communication.tryWriteMessages(rc);
    }

    static void moveRandom(RobotController rc) throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if(rc.canMove(dir)) rc.move(dir);
    }

    static void moveTowards(RobotController rc, MapLocation loc) throws GameActionException{
        Direction dir = rc.getLocation().directionTo(loc);
        if(rc.canMove(dir)) rc.move(dir);
        else moveRandom(rc);
    }
}