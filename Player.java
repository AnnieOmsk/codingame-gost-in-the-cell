import java.util.*;
import java.io.*;
import java.math.*;

class Utils {
    public static final Integer DEFENDERS_COUNT = 5;

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

class Factory {
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

 
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int factoryCount = in.nextInt(); // the number of factories
        int linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();
        }
        // game loop
        while (true) {
            List<Factory> myFactories = new LinkedList<Factory>();
            List<Factory> enemyFactories = new LinkedList<Factory>();
            List<Factory> neutralFactories = new LinkedList<Factory>();
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
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
                    System.err.println(factory.toString());
                }
            }

            Integer enemyMinimalDefenders = Utils.DEFENDERS_COUNT;
            if (enemyFactories.size() > 0) {
                enemyMinimalDefenders = enemyFactories.get(0).cyborgsNumber;
                for (Factory enemyFactory : enemyFactories) {
                    if (enemyFactory.cyborgsNumber < enemyMinimalDefenders) {
                        enemyMinimalDefenders = enemyFactory.cyborgsNumber;
                    }
                }
            }

            Integer myMinimalDefenders = Math.min(enemyMinimalDefenders, Utils.DEFENDERS_COUNT);

            List<Move> moves = new LinkedList<Move>();
            for (Factory myFactory : myFactories) {
                Integer cyborgsForAttack = (myFactory.cyborgsNumber - myMinimalDefenders > 0) ? myFactory.cyborgsNumber - myMinimalDefenders : 0;
                System.err.println(myFactory.toString() + ", cyborgsForAttack: " + cyborgsForAttack);
                if (cyborgsForAttack > 0) {
                    for (Factory enemyFactory : enemyFactories) {
                        if (cyborgsForAttack > 2 * enemyFactory.cyborgsNumber + 1) {
                            Integer troopSize = enemyFactory.cyborgsNumber + 1;
                            moves.add(new Move(myFactory, enemyFactory, troopSize));
                            myFactory.cyborgsNumber -= troopSize;
                            cyborgsForAttack -= troopSize;
                        }
                        if (cyborgsForAttack <= 0) {
                            break;
                        }
                    }
                }
                System.err.println(myFactory.toString() + ", cyborgsNumber: " + myFactory.cyborgsNumber);
            }

            for (Factory myFactory : myFactories) {
                Integer cyborgsForAttack = (myFactory.cyborgsNumber - myMinimalDefenders > 0) ? myFactory.cyborgsNumber - myMinimalDefenders : 0;
                System.err.println(myFactory.toString() + ", cyborgsForAttack: " + cyborgsForAttack);
                if (cyborgsForAttack > 0) {
                    for (Factory neutralFactory: neutralFactories) {
                        if (cyborgsForAttack > neutralFactory.cyborgsNumber + 1) {
                            Integer troopSize = neutralFactory.cyborgsNumber + 1;
                            moves.add(new Move(myFactory, neutralFactory, troopSize));
                            myFactory.cyborgsNumber -= troopSize;
                            cyborgsForAttack -= troopSize;
                        }
                        if (cyborgsForAttack <= 0) {
                            break;
                        }
                    }
                }
                System.err.println(myFactory.toString() + ", cyborgsNumber: " + myFactory.cyborgsNumber);
            }

            if (moves.size() > 0) {
                int movesCount = 0;
                StringBuilder moveStringBuilder = new StringBuilder(moves.size() * 15);
                for (Move move : moves) {
                    moveStringBuilder.append(move.toString());
                    if (movesCount < moves.size() - 1) {
                        moveStringBuilder.append(";");    
                    }                
                    movesCount++;
                }
                System.out.println(moveStringBuilder.toString());
            } else {
                System.out.println("WAIT");
            }
        }
    }
}