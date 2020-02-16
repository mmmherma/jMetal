package org.uma.jmetal.example.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.problem.singleobjective.VRP;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VRPRunner {
    public static void main(String[] args) {
        PermutationProblem<PermutationSolution<Integer>> problem;
        Algorithm<PermutationSolution<Integer>> algorithm;
        CrossoverOperator<PermutationSolution<Integer>> crossover;
        MutationOperator<PermutationSolution<Integer>> mutation;
        SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

        try {
            problem = new VRP("/resources/vrpdata/89-traffic", true, "\",\"", 100);

            crossover = new PMXCrossover(0.9) ;

            double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
            mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

            selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

            algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                    .setPopulationSize(100)
                    .setMaxEvaluations(250000)
                    .setSelectionOperator(selection)
                    .build() ;

            AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                    .execute() ;

            PermutationSolution<Integer> solution = algorithm.getResult() ;
            List<PermutationSolution<Integer>> population = new ArrayList<>(1) ;
            population.add(solution) ;

            long computingTime = algorithmRunner.getComputingTime() ;

            new SolutionListOutput(population)
                    //.setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                    .print();

            JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
            JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
            JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}