package ga;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class MyProblemTest {
    Problem problem;

    @Before
    public void setUp() {
        problem = new MyProblem();
    }

    @After
    public void tearDown() {
        problem = null;
    }

    @Test
    public void testGetRandomDesignVector(){
        assertNotNull("Method returned null", problem.getRandomDesignVector());
    }

    @Test
    public void testEvaluate(){
        //implement later
        /*ArrayList<Boolean> testVector = new ArrayList<Boolean>();
        for (int i = 0; i < 50; i++)
            testVector.add(i % 2 == 0 ? true : false);*/

    }
}