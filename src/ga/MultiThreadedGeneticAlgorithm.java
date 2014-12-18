package ga;

import java.util.EventListener;

/**
 * @author dev1 & dev2
 * @version 1.0
 */
public class MultiThreadedGeneticAlgorithm implements EventListener {

    private Problem problem;
    private EliteMembers eliteMembers;
    private float crossoverProb, mutationProb;
    private int noOfMembers, noOfEvolutions;
    private volatile int numberOfEvolutionsComplete = 0;
    private volatile int numberOfThreadsCompleted = 0;
    public boolean complete;

    /**
     * @param problemName - the Problem class to use for the problem
     * @param noOfMembers - number of members to be simultaneously be processed
     * @param noOfElites - number of best solutions to return
     * @param crossoverProb - probability of class being crossed over with parent
     * @param mutationProb - probability of attributes being mutated
     * @param noOfEvolutions - total number of evolutions for each member to undergo
     */
    public MultiThreadedGeneticAlgorithm(String problemName, int noOfMembers, int noOfElites, float crossoverProb,
                                         float mutationProb, int noOfEvolutions) {
        if (noOfMembers >= 1) {
            this.noOfMembers = noOfMembers;
        } else {
            System.out.println("Number of members must be greater than or equal to 1.");
            System.exit(1);
        }
        if (noOfElites >= 1) {
            eliteMembers = new EliteMembers(noOfElites);
        }
        else {
            System.out.println("Number of elites must be greater than or equal to 1.");
            System.exit(1);
        }
        if ((crossoverProb >= 0) && (crossoverProb <= 1))
            this.crossoverProb = crossoverProb;
        else {
            System.out.println("Crossover probability must be within the range [0,1].");
            System.exit(1);
        }
        if ((mutationProb >= 0) && (mutationProb <= 1))
            this.mutationProb = mutationProb;
        else {
            System.out.println("Mutation probability must be within the range [0,1].");
            System.exit(1);
        }
        if (noOfEvolutions >= noOfElites) {
            this.noOfEvolutions = noOfEvolutions;
        } else {
            System.out.println("Number of evaluations must be greater than or equal to the number of elites.");
            System.exit(1);
        }

        try {
            this.problem = Helper.getProblem("ga." + problemName);
        } catch(GAInitiationException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Creates and runs a new instance of MTGA with params from command line
     * @param args
     */
    public static void main(String[] args) {
        MultiThreadedGeneticAlgorithm mtga = new MultiThreadedGeneticAlgorithm(args[0], Integer.parseInt(args[1]),
                Integer.parseInt(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4]), Integer.parseInt(args[5]));

        mtga.generateMembers();
    }

    /**
     * Creates anonymous Threads for each member, each containing a Design and starts each
     */
    public void generateMembers() {
        for (int i=0; i < noOfMembers; i++) {
            new Thread() {
                private CloneableDesign design;

                @Override
                public void run() {
                    design = new CloneableDesign(problem);
                    design.evaluate();
                    eliteMembers.addToEliteIfGood(design);

                    while (!checkForTerminationCriteria()) {
                        design.evolve(eliteMembers.getRandomDesign(), crossoverProb, mutationProb);
                        design.evaluate();
                        eliteMembers.addToEliteIfGood(design);
                    }
                    fireEvolutionsCompleteEvent(new EvolutionsComplete(this));
                }

                private synchronized boolean checkForTerminationCriteria() {
                    if (numberOfEvolutionsComplete < noOfEvolutions) {
                        numberOfEvolutionsComplete += 1;
                        return false;
                    }
                    else if (numberOfEvolutionsComplete == noOfEvolutions) {
                        return true;
                    } else {
                        System.out.println("Too many evolutions!");
                        System.exit(1);
                        return true;
                    }
                }

                /**
                 * Fires an event to containing class
                 * @param evt
                 */
                private void fireEvolutionsCompleteEvent(EvolutionsComplete evt) {
                    evolutionsCompleteOccurance(evt);
                }
            }.start();
        }
    }


    /**
     * Method to handle event which is fired when a Thread running a Design has completed
     */
    public void evolutionsCompleteOccurance(EvolutionsComplete evt) {
        synchronized (this) {
            Object o = evt.getSource();
            if (o instanceof Thread) {
                numberOfThreadsCompleted += 1;
                if (numberOfThreadsCompleted == noOfMembers) {
                    System.out.println("Complete after " + numberOfEvolutionsComplete + " evolutions.");
//                    eliteMembers.printEliteList();
                    eliteMembers.writeValuesToFile();
                    eliteMembers.writeSerializedFile();
                    complete = true;
                }
            }
        }
    }

    public int getNumberOfEvolutionsComplete() {
        return numberOfEvolutionsComplete;
    }
}