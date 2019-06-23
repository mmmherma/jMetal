package org.uma.jmetal.problem.singleobjective;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class VRPTest {

    @Test
    public void testReadProblem() {
        try {
            System.out.println("Working Directory = " +
                    System.getProperty("user.dir"));
            VRP vrp = new VRP("src/main/resources/vrpdata/89-traffic", true, "\",\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}