import java.util.*;
import java.io.*;
import java.math.*;

class Utils {
    public static final Integer DEFENDERS_COUNT = 5;
    public static final Integer MIN_FACTORIES_COUNT_BEFORE_BOMBING = 2;

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
}

class Factory implements Comparable<Factory> {
    public Integer id;
    public Integer own;
    public Integer cyborgsNumber;
    public Integer production;
   
    Factory(Integer id, Integer own, Integer cyborgsNumber, Integer production) {
        this.id = id;
        this.own = own;
        this.cyborgsNumber = cyborgsNumber;
        this.production = production;
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
                ownString = "Neurtal";
        }
        
        return ownString + ": " + this.id + ", " + Utils.numberFormat(this.cyborgsNumber) + ", " + this.production.toString();
    }

    public int compareTo(Factory anotherFactory) {
        return (this.cyborgsNumber - anotherFactory.cyborgsNumber);
    }

    public boolean equals(Factory anotherFactory) {
        return this.cyborgsNumber.intValue() == anotherFactory.cyborgsNumber.intValue();
    }
}

class Move {
    public Factory attackingFactory = null;
    public Factory factoryForAttack = null;
    public Integer troopSize = 0;

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
    public Factory bombingFactory = null;
    public Factory factoryForBombing = null;

    Bomb(Factory bombingFactory, Factory factoryForBombing) {
        this.bombingFactory = bombingFactory;
        this.factoryForBombing = factoryForBombing;
    }

    public String toString() {
        return "BOMB " + this.bombingFactory.id + " " + this.factoryForBombing.id;
    }
}
 
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {

        Integer[][] destinations = new Integer[15][15];

        Scanner in = new Scanner(System.in);
        Integer factoryCount = in.nextInt(); // the number of factories
        Integer linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            Integer factory1 = in.nextInt();
            Integer factory2 = in.nextInt();
            Integer distance = in.nextInt();
            destinations[factory1][factory2] = distance;
            destinations[factory2][factory1] = distance;
        }
        for (int i = 0; i < factoryCount; i++) {
            destinations[i][i] = 0;
        }

        // game loop
        while (true) {
            List<Factory> myFactories = new LinkedList<Factory>();
            List<Factory> enemyFactories = new LinkedList<Factory>();
            List<Factory> neutralFactories = new LinkedList<Factory>();

            Factory[] allFactoriesByDefenders = new Factory[15];
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            int factoriesCount = 0;
            for (int i = 0; i < entityCount; i++) {
                int entityId = new Integer(in.nextInt());
                String entityType = in.next();
                int own = new Integer(in.nextInt());
                int cyborgsNumber = new Integer(in.nextInt());
                int production = new Integer(in.nextInt());
                int arg4 = new Integer(in.nextInt()); //unused
                int arg5 = new Integer(in.nextInt()); //unused
                if(entityType.equals("FACTORY")) {
                    Factory factory = new Factory(entityId, own, cyborgsNumber, production);
                    allFactoriesByDefenders[factoriesCount] = factory;
                    factoriesCount++;
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
                    //System.err.println(factory.toString());
                }
            }

            // Make a factories array ordered by defenders number ascending
            Arrays.sort(allFactoriesByDefenders, 0, factoryCount);
            for (int i = 0; i < factoryCount; i++) {
                System.err.println(allFactoriesByDefenders[i].toString());
            }

            // Detecting of minimal defenders for my factories
            Integer enemyMinimalDefenders = Utils.DEFENDERS_COUNT;
            if (enemyFactories.size() > 0) {
                enemyMinimalDefenders = enemyFactories.get(0).cyborgsNumber;
                for (Factory enemyFactory : enemyFactories) {
                    if (enemyFactory.cyborgsNumber < enemyMinimalDefenders) {
                        enemyMinimalDefenders = enemyFactory.cyborgsNumber;
                    }
                }
            }

            // Making bombs
            List<Bomb> bombs = new LinkedList<Bomb>();
            // Detecting my bomber as a factory with minimum defenders (maybe medium?? maybe the most distant from enemies??)
            Factory bombingFactory = null;
            if (myFactories.size() > Utils.MIN_FACTORIES_COUNT_BEFORE_BOMBING) {
                int factoryNumber = 0;
                while ((factoryNumber < factoryCount) && (allFactoriesByDefenders[factoryNumber].own != 1)) {
                    factoryNumber++;
                }
                if (allFactoriesByDefenders[factoryNumber].own == 1) {
                    bombingFactory = allFactoriesByDefenders[factoryNumber];
                }
    
                // Detecting an enemy factory for bombing with maximum defenders (maybe with the best defenders/destination form my bomber ratio??)
                Factory factoryForBombing = null;
                factoryNumber = factoryCount - 1;
                while ((factoryNumber > 0) && (allFactoriesByDefenders[factoryNumber].own != -1)) {
                    factoryNumber--;
                }
                if (allFactoriesByDefenders[factoryNumber].own == -1) {
                    factoryForBombing = allFactoriesByDefenders[factoryNumber];
                }

                if ((bombingFactory != null) && (factoryForBombing != null)) {
                    bombs.add(new Bomb(bombingFactory, factoryForBombing));
                }
            }

            Integer myMinimalDefenders = Math.min(enemyMinimalDefenders, Utils.DEFENDERS_COUNT);
            Set<Integer> attackedFactoriesIDs = new TreeSet<Integer>();

            // Making moves
            List<Move> moves = new LinkedList<Move>();
            for (Factory myFactory : myFactories) {
                Integer cyborgsForAttack = (myFactory.cyborgsNumber - myMinimalDefenders > 0) ? myFactory.cyborgsNumber - myMinimalDefenders : 0;
                //System.err.println(myFactory.toString() + ", cyborgsForAttack: " + cyborgsForAttack);
                if (cyborgsForAttack > 0) {
                    for (Factory enemyFactory : enemyFactories) {
                        if (!attackedFactoriesIDs.contains(enemyFactory.id) && (cyborgsForAttack > 2 * enemyFactory.cyborgsNumber + 1)) {
                            Integer troopSize = enemyFactory.cyborgsNumber + 1;
                            moves.add(new Move(myFactory, enemyFactory, troopSize));
                            myFactory.cyborgsNumber -= troopSize;
                            cyborgsForAttack -= troopSize;
                            attackedFactoriesIDs.add(enemyFactory.id);
                        }
                        if (cyborgsForAttack <= 0) {
                            break;
                        }
                    }
                }
                //System.err.println(myFactory.toString() + ", cyborgsNumber: " + myFactory.cyborgsNumber);
            }

            for (Factory myFactory : myFactories) {
                Integer cyborgsForAttack = (myFactory.cyborgsNumber - myMinimalDefenders > 0) ? myFactory.cyborgsNumber - myMinimalDefenders : 0;
                //System.err.println(myFactory.toString() + ", cyborgsForAttack: " + cyborgsForAttack);
                if (cyborgsForAttack > 0) {
                    for (Factory neutralFactory: neutralFactories) {
                        if (!attackedFactoriesIDs.contains(neutralFactory.id) && (cyborgsForAttack > neutralFactory.cyborgsNumber + 1)) {
                            Integer troopSize = neutralFactory.cyborgsNumber + 1;
                            moves.add(new Move(myFactory, neutralFactory, troopSize));
                            myFactory.cyborgsNumber -= troopSize;
                            cyborgsForAttack -= troopSize;
                            attackedFactoriesIDs.add(neutralFactory.id);
                        }
                        if (cyborgsForAttack <= 0) {
                            break;
                        }
                    }
                }
               // System.err.println(myFactory.toString() + ", cyborgsNumber: " + myFactory.cyborgsNumber);
            }

            StringBuilder turnStringBuilder = new StringBuilder(moves.size() * 15 + bombs.size() * 10 + 10);
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
            System.out.println(turnStringBuilder.toString());
        }
    }
}