package org.uma.jmetal.problem.singleobjective;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class VRPTest {

    @Test
    public void testReadProblem() {
        try {
            VRP vrp = new VRP("/home/marcos/Documents/github/jMetalSP/outputTraffic/89-traffic", true, "\",\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}