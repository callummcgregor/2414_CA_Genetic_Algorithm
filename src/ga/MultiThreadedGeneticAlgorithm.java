package ga;

import java.util.EventListener;

/**
 * Runs main program.
 *
 * @author dev1 & dev2
 */
public class MultiThreadedGeneticAlgorithm implements EventListener {
    private Problem problem;
    private EliteMembers eliteMembers;
    private float crossoverProb, mutationProb;
    private int noOfMembers, noOfEvolutions;
    private volatile int numberOfEvolutionsComplete = 0;
    private volatile int numberOfThreadsCompleted = 0;

    /**
     * @param problemName - the implementation of Problem to be used
     * @param noOfMembers - number of members in the "gene pool"
     * @param noOfElites - number of best solutions to return
     * @param crossoverProb - probability of attributes being crossed over with parent
     * @param mutationProb - probability of attributes being mutated
     * @param noOfEvolutions - total number of evolutions to complete before returning
     */
    public MultiThreadedGeneticAlgorithm(String problemName, int noOfMembers, int noOfElites, float crossoverProb,
                                         float mutationProb, int noOfEvolutions) {
        try {
            this.problem = Helper.getProblem("ga." + problemName);
        } catch(GAInitiationException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
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
            System.out.println("Crossover probability must be within the range [0,1], inclusive.");
            System.exit(1);
        }
        if ((mutationProb >= 0) && (mutationProb <= 1))
            this.mutationProb = mutationProb;
        else {
            System.out.println("Mutation probability must be within the range [0,1], inclusive.");
            System.exit(1);
        }
        if (noOfEvolutions >= noOfElites) {
            this.noOfEvolutions = noOfEvolutions;
        } else {
            System.out.println("Number of evaluations must be greater than or equal to the number of elites.");
            System.exit(1);
        }
    }

    /**
     * Creates and runs a new instance of MTGA with parameters from command line
     * @param args - system parameters
     */
    public static void main(String[] args) {
        MultiThreadedGeneticAlgorithm mtga = new MultiThreadedGeneticAlgorithm(args[0], Integer.parseInt(args[1]),
                Integer.parseInt(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4]), Integer.parseInt(args[5]));
        mtga.generateMembers();
    }

    /**
     * Creates and starts an anonymous Thread for each member, containing a Design
     */
    public void generateMembers() {
        for (int i=0; i < noOfMembers; i++) {
            new Thread() {
                private CloneableDesign design;

                /**
                 * Performs flow chart described in Figure 1 of specification
                 */
                @Override
                public void run() {
                    design = new CloneableDesign(problem);
                    design.evaluate();
                    eliteMembers.auditionCandidateDesign(design);

                    while (!checkForTerminationCriteria()) {
                        design.evolve(eliteMembers.getRandomDesign(), crossoverProb, mutationProb);
                        design.evaluate();
                        eliteMembers.auditionCandidateDesign(design);
                    }
                    fireEvolutionsCompleteEvent(new ThreadCompleteEvent(this));
                }

                /**
                 * Fires an event to containing class when termination criteria met
                 * @param evt
                 */
                private void fireEvolutionsCompleteEvent(ThreadCompleteEvent evt) {
                    evolutionsCompleteOccurance(evt);
                }
            }.start();
        }
    }

    /**
     * Synchronized in order that two Threads do not enter the above while loop simultaneously
     * @return - whether or not the termination criteria has been met
     */
    private synchronized boolean checkForTerminationCriteria() {
        if (numberOfEvolutionsComplete < noOfEvolutions) {
            numberOfEvolutionsComplete += 1;
            return false;
        }
        else if (numberOfEvolutionsComplete == noOfEvolutions) {
            return true;
        } else {    // implies numberOfEvolutionsComplete > noOfEvolutions
            System.out.println("Error in calculating complete, evolutions complete: " + numberOfEvolutionsComplete);
            System.exit(2);
            return true;    // Should never be reached!
        }
    }

    /**
     * Method to handle event which is fired when a Thread running a Design has completed
     */
    public void evolutionsCompleteOccurance(ThreadCompleteEvent evt) {
        synchronized (this) {
            Object o = evt.getSource();
            if (o instanceof Thread) {
                numberOfThreadsCompleted += 1;
                if (numberOfThreadsCompleted == noOfMembers) {
                    System.out.println("Complete after " + numberOfEvolutionsComplete + " evolutions.");
                    eliteMembers.writeValuesToFile();
                    eliteMembers.writeSerializedDesignsToFile();
                }
            }
        }
    }

    /**
     * @return - number of completed evolutions
     */
    public int getNumberOfEvolutionsComplete() {
        return numberOfEvolutionsComplete;
    }
}