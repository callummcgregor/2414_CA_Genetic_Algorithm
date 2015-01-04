package ga;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class containing elite members and methods for handling the elite members list
 *
 * @author dev1 & dev2
 */
public class EliteMembers {
    private final int noOfElites;
    private ArrayList<Design> eliteList = new ArrayList<Design>();
    private volatile ArrayList<Design> eliteListCopy = new ArrayList<Design>(); // Elite list to be used if original one is locked
    private Lock lock = new ReentrantLock();

    public EliteMembers(int noOfElites) {
        this.noOfElites = noOfElites;
    }

    /**
     * Compares a given Design to those in the elite list, determines if it's good enough and inserts if so
     *
     * @param d - Design being compared to elite members
     */
    public synchronized void auditionCandidateDesign(CloneableDesign d) {
        if (eliteList.size() == 0) {
            eliteList.add(d.clone());
        } else {
            for (int i = eliteList.size() - 1; i >= 0; i--) {
                if (d.getValue().doubleValue() < eliteList.get(i).getValue().doubleValue()) {
                    eliteList.add(i + 1, d.clone());
                    break;
                }
            }
            if (d.getValue().doubleValue() >= eliteList.get(0).getValue().doubleValue()) {
                eliteList.add(0, d.clone());
            }
        }
        if (eliteList.size() > noOfElites)
            eliteList.remove(noOfElites);
        eliteListCopy = new ArrayList<Design>(eliteList);
    }

    /**
     * @return - a random Design from the Elite members list
     */
    public Design getRandomDesign() {
        Random ranGen = new Random();
        if (!lock.tryLock()){
            return eliteListCopy.get(ranGen.nextInt(eliteListCopy.size()));
        }
        else {
            lock.lock();
            int randIndex = ranGen.nextInt(getListSize());
            lock.unlock();
            return eliteList.get(randIndex);
        }
    }

    private synchronized int getListSize(){
        return eliteList.size();
    }

    public void writeValuesToFile(){
        try {
            File f = new File("results.txt");
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (Design d : eliteList) {
                bw.write(d.getValue().doubleValue() + "\n");
            }
            bw.close();
        } catch (IOException e){
            System.out.println("Unable to write to file");
        }
    }

    public void writeSerializedDesignsToFile() {
        try {
            FileOutputStream fos = new FileOutputStream("designs.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(eliteList.toArray());
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to write to file");
        }
    }
}
