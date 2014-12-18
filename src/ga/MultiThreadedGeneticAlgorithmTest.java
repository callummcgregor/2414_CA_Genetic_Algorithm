package ga;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MultiThreadedGeneticAlgorithmTest {

    MultiThreadedGeneticAlgorithm mtga;

    @Before
    public void setUp() throws Exception{
        mtga = new MultiThreadedGeneticAlgorithm("MyProblem", 20, 50, 0.2f, 0.1f, 10000);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testManyRuns() throws Exception {
        for (int i = 0; i < 1000; i++) {
            setUp();
            mtga.generateMembers();
        }
    }


}