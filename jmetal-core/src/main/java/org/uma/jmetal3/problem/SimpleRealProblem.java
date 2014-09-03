package org.uma.jmetal3.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.impl.NumericalSolutionImpl;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class SimpleRealProblem extends Problem{
  public SimpleRealProblem() {
    numberOfVariables = 5 ;
    numberOfObjectives = 2 ;

    lowerLimit = new double[]{0.0, 0.0, 0.0, 0.0, 0.0} ;
    upperLimit = new double[]{1.0, 1.0, 1.0, 1.0, 1.0} ;
  }

  @Override
  public void evaluate(Solution solution) throws JMetalException {

  }
}
