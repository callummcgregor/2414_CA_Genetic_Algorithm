package ga;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author dev1 & dev2
 * @version 1.0
 * TODO: delme, for development purposes
 */
public class MyProblem implements Problem, Serializable {
    private final double vectorLen = 100;

    public Number evaluate(Design d) {
        Boolean[] designVector = d.getDesignParameters();
        double count = 0;
        for (Boolean b : designVector) {
            if (b)
                count++;
        }
        return new Double(count / vectorLen);
    }

    public ArrayList<Boolean> getRandomDesignVector() {
        Random randGen = new Random();
        ArrayList<Boolean> binaryStr = new ArrayList<Boolean>();
        for (int i = 0; i < vectorLen; i++)
            binaryStr.add(randGen.nextBoolean());

        return binaryStr;
    }
}
