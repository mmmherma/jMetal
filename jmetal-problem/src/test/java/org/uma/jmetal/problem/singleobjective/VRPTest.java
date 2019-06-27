package org.uma.jmetal.problem.singleobjective;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class VRPTest {

    @Test
    public void testReadProblem() {
        try {
            System.out.println("INIT TEST");
            VRP vrp = new VRP("src/main/resources/vrpdata/89-traffic", true, "\",\"", 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}