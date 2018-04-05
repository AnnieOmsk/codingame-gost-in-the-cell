import java.util.*;
import java.io.*;
import java.math.*;

class Utils {
    public static final Integer DEFENDERS_COUNT = 5;
    public static final Integer FORESEE_TURNS_COUNT = 5;
    public static final Integer MIN_FACTORIES_COUNT_BEFORE_BOMBING = 3;
    public static final Integer MIN_TURNS_COUNT_BEFORE_BOMBING = 5;
    public static final Integer MAX_PRODUCTION = 3;
    public static final Integer MAX_BOMBS = 2;
    public static final Integer INCREASING_COST = 10;

    public static String numberFormat(Integer number) {

        String numberString;
        if (number < 10) {
            numberString = "  " + number.toString();
        } else if (number < 100) {
            numberString = " " + number.toString();
        } else {
            numberString = number.toString();
        }
        return numberString;
    }

    public static List<Factory> findFactoriesByProduction(List<Factory> factories, Integer production) {

        List<Factory> factoriesByProduction = new LinkedList<Factory>();
        for (Factory factory : factories) {
            if (factory.production == production) {
                factoriesByProduction.add(factory);
            }
        }

        return factoriesByProduction;
    }

    public static List<Factory> findFactoriesWithMaximumDefenders(List<Factory> factories) {

        List<Factory> factoriesWithMaxDefenders = new LinkedList<Factory>();
        Integer maxDefenders = 0;
        for (Factory factory : factories) {
            if (factory.cyborgsNumber > maxDefenders) {
                maxDefenders = factory.cyborgsNumber;
            }
        }
        for (Factory factory : factories) {
            if (factory.cyborgsNumber == maxDefenders) {
                factoriesWithMaxDefenders.add(factory);
            }
        }

        return factoriesWithMaxDefenders;
    }

    public static List<Factory> findTheNearestFactoriesToCertainFactory(
        List<Factory> potentialNearestFactories, Factory certainFactory, Integer[][] destinations, Integer bottomDestination, Integer topDestination
    ) {

        Integer minDestination = bottomDestination + topDestination; 
        for (Factory potentialFactory : potentialNearestFactories) {
            if ((destinations[potentialFactory.id][certainFactory.id] > bottomDestination) && (destinations[potentialFactory.id][certainFactory.id] < minDestination)) {
                minDestination = destinations[potentialFactory.id][certainFactory.id];
            }
        }
        List<Factory> nearestFactoriesToCertainFactory = new LinkedList<Factory>();
        for (Factory potentialFactory : potentialNearestFactories) {
            if (destinations[potentialFactory.id][certainFactory.id] == minDestination) {
                nearestFactoriesToCertainFactory.add(potentialFactory);
            }
        }

        return nearestFactoriesToCertainFactory;
    }
}

class Factory implements Comparable<Factory> {
    public Integer id;
    public Integer own;
    public Integer cyborgsNumber;
    public Integer production;
    public Integer turnsBeforeProduction;
    public Integer[] defendersByTurns;
    public Integer[] enemiesComingByTurns;
    public Integer attackingRank;
   
    Factory(Integer id, Integer own, Integer cyborgsNumber, Integer production, Integer turnsBeforeProduction) {
        this.id = id;
        this.own = own;
        this.cyborgsNumber = cyborgsNumber;
        this.production = production;
        this.turnsBeforeProduction = turnsBeforeProduction;
        this.defendersByTurns = new Integer[Utils.FORESEE_TURNS_COUNT];
        this.defendersByTurns[0] = cyborgsNumber;
        for (int i = 1; i < Utils.FORESEE_TURNS_COUNT; i++) {
            this.defendersByTurns[i] = cyborgsNumber;
        }
        this.enemiesComingByTurns = new Integer[Utils.FORESEE_TURNS_COUNT];
        for (int i = 0; i < Utils.FORESEE_TURNS_COUNT; i++) {
            this.enemiesComingByTurns[i] = 0;
        }
        this.attackingRank = this.production;
    }
    
    public String toString() {
        String ownString = (own ==1) ? "My   " : "Enemy";
        switch (own) {
            case 1:
                ownString = "     My";
                break;
            case -1: 
                ownString = "  Enemy";
                break;
            default:
                ownString = "Neutral";
        }
        
        String defendersString = ", Defenders: ";
        for (int i = 0; i < Utils.FORESEE_TURNS_COUNT; i++) {
            defendersString += this.defendersByTurns[i].toString() + ", ";
        }
        String enemiesString = ", Enemies: ";
        for (int i = 0; i < Utils.FORESEE_TURNS_COUNT; i++) {
            enemiesString += this.enemiesComingByTurns[i].toString() + ", ";
        }

        return ownString + ": " + this.id + ", " + Utils.numberFormat(this.cyborgsNumber) + ", " + this.production.toString() /*+ ", " + this.turnsBeforeProduction.toString() + defendersString + enemiesString*/;
    }

    public int compareTo(Factory anotherFactory) {
        return (this.cyborgsNumber - anotherFactory.cyborgsNumber);
    }

    public boolean equals(Factory anotherFactory) {
        return this.cyborgsNumber.intValue() == anotherFactory.cyborgsNumber.intValue();
    }
}

class Move {
    public Factory attackingFactory;
    public Factory factoryForAttack;
    public Integer troopSize;

    Move(Factory attackingFactory, Factory factoryForAttack, Integer troopSize) {
        this.attackingFactory = attackingFactory;
        this.factoryForAttack = factoryForAttack;
        this.troopSize = troopSize;
    }

    public String toString() {
        return "MOVE " + this.attackingFactory.id + " " + this.factoryForAttack.id + " " + this.troopSize;
    }
}

class Bomb {
    public Factory bombingFactory;
    public Factory factoryForBombing;

    Bomb(Factory bombingFactory, Factory factoryForBombing) {
        this.bombingFactory = bombingFactory;
        this.factoryForBombing = factoryForBombing;
    }

    public String toString() {
        return "BOMB " + this.bombingFactory.id + " " + this.factoryForBombing.id;
    }
}
 
class Troop {
    public Integer id;
    public Integer own;
    public Integer sourceFactoryID;
    public Integer destinationFactoryID;
    public Integer troopSize;
    public Integer turnsBeforeComing;

    Troop(Integer id, Integer own, Integer sourceFactoryID, Integer destinationFactoryID, Integer troopSize, Integer turnsBeforeComing) {
        this.id = id;
        this.own = own;
        this.sourceFactoryID = sourceFactoryID;
        this.destinationFactoryID = destinationFactoryID;
        this.troopSize = troopSize;
        this.turnsBeforeComing = turnsBeforeComing;
    }

    public String toString() {
        return "Troop: " + this.sourceFactoryID + " " + this.destinationFactoryID + " " + this.troopSize + " " + this.turnsBeforeComing;
    }
}

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {

        Integer[][] destinations = new Integer[15][15];
        Integer topDestination = 0;

        Scanner in = new Scanner(System.in);
        Integer factoryCount = in.nextInt(); // the number of factories
        Integer linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            Integer factory1 = in.nextInt();
            Integer factory2 = in.nextInt();
            Integer distance = in.nextInt();
            destinations[factory1][factory2] = distance;
            destinations[factory2][factory1] = distance;
            if (distance > topDestination) {
                topDestination = distance;
            }
        }
        for (int i = 0; i < factoryCount; i++) {
            destinations[i][i] = 0;
        }

        Set<Integer> bombedFactoriesIDs = new TreeSet<Integer>();
        
        // game loop
        int turnNumber = 0;
        while (true) {
            // Read all data
            List<Factory> myFactories = new LinkedList<Factory>();
            List<Factory> enemyFactories = new LinkedList<Factory>();
            List<Factory> neutralFactories = new LinkedList<Factory>();
            Map<Integer, Factory> factories = new HashMap<Integer, Factory>();

            List<Troop> myTroops = new LinkedList<Troop>();
            List<Troop> enemyTroops = new LinkedList<Troop>();

            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            for (int entityNumber = 0; entityNumber < entityCount; entityNumber++) {
                int entityId = new Integer(in.nextInt());
                String entityType = in.next();
                if(entityType.equals("FACTORY")) {
                    int own = new Integer(in.nextInt());
                    int cyborgsNumber = new Integer(in.nextInt());
                    int production = new Integer(in.nextInt());
                    int turnsBeforeProduction = new Integer(in.nextInt());
                    int arg5 = new Integer(in.nextInt()); //unused
                    Factory factory = new Factory(entityId, own, cyborgsNumber, production, turnsBeforeProduction);
                    switch (own) {
                        case 1:
                            myFactories.add(factory);
                            break;
                        case -1: 
                            enemyFactories.add(factory);
                            break;
                        default:
                            neutralFactories.add(factory);
                    }
                    factories.put(entityId, factory);
                    if (factory.own != 0) {
                        for (int turn = 1; turn < Utils.FORESEE_TURNS_COUNT; turn++) {
                            int futureCyborgsNumber = cyborgsNumber;
                            if (turnsBeforeProduction < turn) {
                                futureCyborgsNumber += (turn - turnsBeforeProduction) * production;
                            }
                            factory.defendersByTurns[turn] = futureCyborgsNumber;
                        }
                    }
                    System.err.println(factory.toString());
                } else if (entityType.equals("TROOP")) {
                    int own = new Integer(in.nextInt());
                    int sourceFactoryID = new Integer(in.nextInt());
                    int destinationFactoryID = new Integer(in.nextInt());
                    int troopSize = new Integer(in.nextInt());
                    int turnsBeforeComing = new Integer(in.nextInt());
                    Troop troop = new Troop(entityId, own, sourceFactoryID, destinationFactoryID, troopSize, turnsBeforeComing);
                    switch (own) {
                        case 1:
                            myTroops.add(troop);
                            break;
                        case -1: 
                            enemyTroops.add(troop);
                            break;
                    }
                    System.err.println(troop.toString());
                    if (turnsBeforeComing < Utils.FORESEE_TURNS_COUNT) {
                        Factory factory = factories.get(destinationFactoryID);
                        if ((factory.own == 0) || (factory.own * troop.own < 0)) {
                            factory.defendersByTurns[turnsBeforeComing] = Math.abs(factory.defendersByTurns[turnsBeforeComing] - troop.troopSize);
                        } else if (factory.own * troop.own > 0) {
                            factory.defendersByTurns[turnsBeforeComing] += troop.troopSize;
                        }
                        System.err.println(factory.toString());
                    }
                } else if(entityType.equals("BOMB")) {
                    int own = new Integer(in.nextInt());
                    int sourceFactoryID = new Integer(in.nextInt());
                    int destinationFactoryID = new Integer(in.nextInt());
                    int turnsBeforeComing = new Integer(in.nextInt());
                    int arg5 = new Integer(in.nextInt()); //unused
                }
            }

            // Find the best enemy's factory for bombing: maximum of defenders, the closest, maximum production
            List<Bomb> bombs = new LinkedList<Bomb>();
            if (bombedFactoriesIDs.size() < Utils.MAX_BOMBS) {
                Factory factoryForBombing = null;
                Factory bombingFactory = null;
                if (turnNumber == 0) {
                    // We always bomb the first enemy factory
                    for (Factory enemyFactory : enemyFactories) {
                        factoryForBombing = enemyFactory;
                        break;
                    }
                    for (Factory myFactory : myFactories) {
                        bombingFactory = myFactory;
                        break;
                    }
                } else if (enemyFactories.size() >= Utils.MIN_FACTORIES_COUNT_BEFORE_BOMBING) {
                    for (Integer production = Utils.MAX_PRODUCTION; production >= 0; production--) {
                        List<Factory> bestEnemyProductionFactories = Utils.findFactoriesByProduction(enemyFactories, production);
                        List<Factory> enemyFactoriesWithMaximumDefenders = Utils.findFactoriesWithMaximumDefenders(bestEnemyProductionFactories);
                        for (Factory enemyFactory : enemyFactoriesWithMaximumDefenders) {
                            if (!bombedFactoriesIDs.contains(enemyFactory.id)) {
                                List<Factory> myNearestFactories = Utils.findTheNearestFactoriesToCertainFactory(myFactories, enemyFactory, destinations, 0, topDestination);
                                for (Factory myFactory : myNearestFactories) {
                                    bombingFactory = myFactory;
                                    factoryForBombing = enemyFactory;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (factoryForBombing != null) {
                    Bomb bomb = new Bomb(bombingFactory, factoryForBombing);
                    bombs.add(bomb);
                    bombedFactoriesIDs.add(factoryForBombing.id);
                }
            }

            List<Move> moves = new LinkedList<Move>();
            Set<Integer> attackedFactoriesIDs = new TreeSet<Integer>();

            // Find the best neutral factories for attack: maximum production. Find my factories for attack: nearest and enough defenders
            System.err.println("------------ Attacking neutrals ------------");
            if (neutralFactories.size() > 0) {
                for (Integer production = Utils.MAX_PRODUCTION; production >= 0; production--) {
                    List<Factory> bestProductionNeutralFactories = Utils.findFactoriesByProduction(neutralFactories, production);
                    for (Factory neutralFactory : bestProductionNeutralFactories) {
                        System.err.println(neutralFactory.toString());
                        Integer bottomDestination = 0;
                        while (bottomDestination <= topDestination / 2) {
                            List<Factory> myNearestFactories = Utils.findTheNearestFactoriesToCertainFactory(myFactories, neutralFactory, destinations, bottomDestination, topDestination);
                            for (Factory myFactory : myNearestFactories) {
                                System.err.println(myFactory.toString());
                                Troop existingTroop = null;
                                for (Troop myTroop : myTroops) {
                                    if (myTroop.destinationFactoryID.equals(neutralFactory.id)) {
                                        existingTroop = myTroop;
                                        break;
                                    }
                                }
                                Integer advantage = 1;
                                if (neutralFactory.production == 0) {
                                    advantage = Utils.INCREASING_COST + 1;
                                }
                                if ((existingTroop == null) && (!attackedFactoriesIDs.contains(neutralFactory.id)) && (myFactory.cyborgsNumber > neutralFactory.cyborgsNumber + advantage)) {
                                    Integer cyborgsForAttack = neutralFactory.cyborgsNumber + advantage;
                                    System.err.println(cyborgsForAttack.toString());
                                    attackedFactoriesIDs.add(neutralFactory.id);
                                    Move move = new Move(myFactory, neutralFactory, cyborgsForAttack);
                                    moves.add(move);
                                    myFactory.cyborgsNumber -= cyborgsForAttack;
                                    for (int i = 0; i < Utils.FORESEE_TURNS_COUNT; i++) {
                                        myFactory.defendersByTurns[i] -= cyborgsForAttack;
                                    }
                                    if (cyborgsForAttack == neutralFactory.cyborgsNumber + advantage) {
                                        break;
                                    }
                                }
                            }
                            bottomDestination++;
                        }
                    }
                }
            }

            // Find the best enemy's factories for attack: already bombed or maximum production, minimum defenders, the closest, not under bombing
            // Find the best my attacking factories: the closest to attacked enemy's factories, maximun defenders
            System.err.println("------------ Attacking enemies ------------");
            if (enemyFactories.size() > 0) {
                for (Integer production = Utils.MAX_PRODUCTION; production >= 0; production--) {
                    List<Factory> bestProductionEnemyFactories = Utils.findFactoriesByProduction(enemyFactories, production);
                    for (Factory enemyFactory : bestProductionEnemyFactories) {
                        System.err.println(enemyFactory.toString());
                        Integer bottomDestination = 0;
                        while (bottomDestination <= topDestination) {
                            List<Factory> myNearestFactories = Utils.findTheNearestFactoriesToCertainFactory(myFactories, enemyFactory, destinations, bottomDestination, topDestination);
                            for (Factory myFactory : myNearestFactories) {
                                System.err.println(myFactory.toString());
                                Troop existingTroop = null;
                                for (Troop myTroop : myTroops) {
                                    if (myTroop.destinationFactoryID.equals(enemyFactory.id)) {
                                        existingTroop = myTroop;
                                        break;
                                    }
                                }
                                Integer advantage = 1;
                                if (enemyFactory.production == 0) {
                                    advantage = Utils.INCREASING_COST + 1;
                                }
                                if ((existingTroop == null) && (!attackedFactoriesIDs.contains(enemyFactory.id)) && (myFactory.cyborgsNumber > enemyFactory.cyborgsNumber + advantage)) {
                                    Integer cyborgsForAttack = enemyFactory.cyborgsNumber + advantage;
                                    System.err.println(cyborgsForAttack.toString());
                                    attackedFactoriesIDs.add(enemyFactory.id);
                                    Move move = new Move(myFactory, enemyFactory, cyborgsForAttack);
                                    moves.add(move);
                                    myFactory.cyborgsNumber -= cyborgsForAttack;
                                    for (int i = 0; i < Utils.FORESEE_TURNS_COUNT; i++) {
                                        myFactory.defendersByTurns[i] -= cyborgsForAttack;
                                    }
                                    if (cyborgsForAttack == enemyFactory.cyborgsNumber + advantage) {
                                        break;
                                    }
                                }
                            }
                            bottomDestination++;
                        }
                    }
                }
            }
            // Find my factories to send troops to my attacking factories: the closest


            // Increasing of factories production
            List<Integer> factoriesForIncreasingIDs = new LinkedList<Integer>();
            for (Factory myFactory : myFactories) {
                if ((myFactory.production == 0) && (myFactory.cyborgsNumber > Utils.INCREASING_COST) && (myFactory.turnsBeforeProduction == 0)) {
                    factoriesForIncreasingIDs.add(myFactory.id);
                    break;
                }
                if ((myFactory.production > 0) && (myFactory.production < Utils.MAX_PRODUCTION) && (myFactory.cyborgsNumber > Utils.INCREASING_COST * 2) && (myFactory.turnsBeforeProduction == 0)) {
                    factoriesForIncreasingIDs.add(myFactory.id);
                    break;
                }
            }

            // Making turn output
            StringBuilder turnStringBuilder = new StringBuilder(moves.size() * 15 + bombs.size() * 10 + factoriesForIncreasingIDs.size() * 10 + 10);
            turnStringBuilder.append("WAIT");
            if (bombs.size() > 0) {
                turnStringBuilder.append(";");
                int bombsCount = 0;
                for (Bomb bomb : bombs) {
                    turnStringBuilder.append(bomb.toString());
                    if (bombsCount < bombs.size() - 1) {
                        turnStringBuilder.append(";");    
                    }                
                    bombsCount++;
                }
            }
            if (moves.size() > 0) {
                turnStringBuilder.append(";");
                int movesCount = 0;
                for (Move move : moves) {
                    turnStringBuilder.append(move.toString());
                    if (movesCount < moves.size() - 1) {
                        turnStringBuilder.append(";");    
                    }                
                    movesCount++;
                }
            }
            if (factoriesForIncreasingIDs.size() > 0) {
                turnStringBuilder.append(";");
                int factoriesCount = 0;
                for (Integer factoryID : factoriesForIncreasingIDs) {
                    turnStringBuilder.append("INC " + factoryID);
                    if (factoriesCount < factoriesForIncreasingIDs.size() - 1) {
                        turnStringBuilder.append(";");    
                    }                
                    factoriesCount++;
                }
            }
            System.out.println(turnStringBuilder.toString());

            turnNumber++;
        }
    }
}