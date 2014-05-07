package test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.IBEA_Settings;
import jmetal.operators.crossover.SBXCrossover;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 17/06/13
 * Time: 22:47
 * To change this template use File | Settings | File Templates.
 */
public class IBEA_SettingsTest {
  Properties configuration_ ;

  @Before
  public void init() throws FileNotFoundException, IOException {
    configuration_ = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(ClassLoader.getSystemResource("IBEA.conf").getPath()));
    configuration_.load(isr);
  }

  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings ibeaSettings = new IBEA_Settings("Fonseca");
    Algorithm algorithm = ibeaSettings.configure() ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    //MutationLocalSearch improvement = (MutationLocalSearch)algorithm.getOperator("improvement") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    Assert.assertEquals("IBEA_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("IBEA_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    Assert.assertEquals("IBEA_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    Assert.assertEquals("IBEA_SettingsTest", 0.9, pc, epsilon);
    Assert.assertEquals("IBEA_SettingsTest", 20.0, dic, epsilon);

    Assert.assertEquals("IBEA_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals("IBEA_SettingsTest", 20.0, dim, epsilon);
  }

  @Test
  public void testConfigure2() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings ibeaSettings = new IBEA_Settings("Fonseca");
    Algorithm algorithm = ibeaSettings.configure(configuration_) ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    //MutationLocalSearch improvement = (MutationLocalSearch)algorithm.getOperator("improvement") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    Assert.assertEquals("IBEA_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    Assert.assertEquals("IBEA_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    Assert.assertEquals("IBEA_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    Assert.assertEquals("IBEA_SettingsTest", 0.9, pc, epsilon);
    Assert.assertEquals("IBEA_SettingsTest", 20.0, dic, epsilon);

    Assert.assertEquals("IBEA_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    Assert.assertEquals("IBEA_SettingsTest", 20.0, dim, epsilon);
  }
}
