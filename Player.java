import java.util.*;
import java.io.*;
import java.math.*;


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
        String cyborgsNumberString;
        if (this.cyborgsNumber < 10) {
            cyborgsNumberString = "  " + this.cyborgsNumber.toString();
        } else if (this.cyborgsNumber < 100) {
            cyborgsNumberString = " " + this.cyborgsNumber.toString();
        } else {
            cyborgsNumberString = this.cyborgsNumber.toString();
        }
        
        return ownString + ": " + this.id + ", " + this.cyborgsNumber.toString() + ", " + this.production.toString();
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

            boolean moved = false;
            Factory attackingFactory = null;
            Factory factoryForAttack = null;
            Integer troopSize = 0;
            for (Factory enemyFactory: enemyFactories) {
                for (Factory myFactory: myFactories) {
                    if (2 * enemyFactory.cyborgsNumber < myFactory.cyborgsNumber - 10) {
                        attackingFactory = myFactory;
                        factoryForAttack = enemyFactory;
                        troopSize = (myFactory.cyborgsNumber - 5) / 2;
                        moved = true;
                        break;
                    }
                }
                if (moved) {
                    break;
                }
            }

            //When no enemy's factory for attack finding a neutral one
            if (!moved) {
                for (Factory neutralFactory: neutralFactories) {
                    for (Factory myFactory: myFactories) {
                        if (2 * neutralFactory.cyborgsNumber < myFactory.cyborgsNumber - 10) {
                            attackingFactory = myFactory;
                            factoryForAttack = neutralFactory;
                            troopSize = (myFactory.cyborgsNumber - 5) / 2;
                            moved = true;
                            break;
                        }
                    }
                    if (moved) {
                        break;
                    }
                }
            }

            if (moved && (attackingFactory != null) && (factoryForAttack != null)) {
                System.out.println("MOVE " + attackingFactory.id + " " + factoryForAttack.id + " " + troopSize);
            } else {
                System.out.println("WAIT");
            }
        }
    }
}