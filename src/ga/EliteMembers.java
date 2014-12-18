package ga;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dev1 & dev2
 * @version 1.0
 */
public class EliteMembers {
    private final int noOfElites;
    private ArrayList<Design> eliteList = new ArrayList<Design>();
    private ArrayList<Design> eliteListCopy = new ArrayList<Design>();
    private Lock lock = new ReentrantLock();

    public EliteMembers(int noOfElites) {
        this.noOfElites = noOfElites;
    }

    /**
     * Compares the Design to those in the elite list and inserts into it's ordered position if necessary
     *
     * @param d the Design being compared to elite members
     */
    public synchronized void addToEliteIfGood(CloneableDesign d) {
        if (eliteList.size() == 0) {
            eliteList.add(d.clone());
        } else {
            for (int i = eliteList.size() - 1; i >= 0; i--) {
                if (d.getValue().doubleValue() < eliteList.get(i).getValue().doubleValue()) { // TODO: comparison method doesn't seem to work!
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

    // TODO: delme, development purposes only
    public void printEliteList() {
        int count = 0;
        for (Design d : eliteList) {
            System.out.println("Elite " + count + ": " + d.getValue());
            count += 1;
        }
        System.out.println("Best value: " + eliteList.get(0).getValue().doubleValue());
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

    public void writeSerializedFile() {
        try {
            FileOutputStream fos = new FileOutputStream("designs.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(eliteList.toArray());
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to write designs.ser");
        }
    }
}
