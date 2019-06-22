package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Class to solve single objective VRP using static CSV from NY City DOT files:
 *      https://data.cityofnewyork.us/resource/i4gi-tjb9.csv
 */
public class VRP extends AbstractIntegerPermutationProblem {
    private int         numberOfClients;
    private double      vehicleCapacity;
    private double [][] clientsDemand;
    private double [][] distanceMatrix;

    private Integer []   validNodes = new Integer[] {1, 2, 4, 110, 124, 126, 129, 140, 141, 142, 145, 150, 153, 154,
            155, 159, 164, 165, 167, 169, 170, 171, 177, 184, 186, 199, 205, 207, 208, 211, 213, 215, 217, 221, 222,
            257, 258, 264, 298, 311, 318, 319, 324, 332, 338, 344, 349, 351, 354, 364, 375, 376, 377, 378, 381, 383,
            384, 402, 416, 418, 422, 425, 426, 428, 430, 431, 433, 434, 440, 448, 450, 453};
    private List<Integer> listOfValidNodes = Arrays.asList(validNodes);

    /**
     * VRP constructor
     * @param fileName Absolute path to CSV file NY City DOT
     * @throws IOException
     */
    public VRP(String fileName, boolean header, String separator) throws IOException {
        distanceMatrix = readProblem(fileName, header, separator);

        setNumberOfVariables(numberOfClients);
        setNumberOfObjectives(1);
        setName("VRP");
    }

    @Override
    public int getPermutationLength() {
        return numberOfClients;
    }

    @Override
    public void evaluate(PermutationSolution<Integer> solution) {

    }

    /**
     * Reads CSV file and builds the distance matrix to optimize
     * @param fileName Absolute path to CSV file
     * @param header true IF file has header ELSE false
     * @param separator Character used to split CSV lines (usually ',')
     * @return Distance matrix
     * @throws IOException
     */
    private double [][] readProblem(String fileName, boolean header, String separator) throws IOException {
        double [][] matrix = null;
        String []   headerLine = null;
        System.out.println(listOfValidNodes.size());

        try {
            // Read CSV file from fileName
            FileReader fReader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fReader);

            if(header) {
                // Header fields:
                //      id, speed, travel_time, status, data_as_of, link_id, link_points,
                //      encoded_poly_line, encoded_poly_line_lvls, owner, transcom_id, borough, link_name
                headerLine = br.readLine().split(separator);

                // Remove " from first and last field
                headerLine[0] = headerLine[0].substring(1);
                headerLine[headerLine.length-1] = headerLine[headerLine.length-1].substring(0, headerLine[headerLine.length-1].length()-1);
            }

            // Remove " from first and last field
            String line = "";
            while ((line = br.readLine()) != null) {
                //line = line.substring(1, 1);
                line = line.substring(1, line.length()-1);
                String [] fields = line.split(separator);

                if (listOfValidNodes.contains(Integer.parseInt(fields[0]))) {
                    // Node is a valid node
                    // IF not already exists at matrix THEN insert ELSE discard
                    for ()
                }

                String parsedLine = "";
                for (int i = 0; i < fields.length; i++) {
                    parsedLine += headerLine[i] + ": " + fields[i] + " ";
                }
                System.out.println(parsedLine);
                System.out.println("*******");
            }
        } catch (Exception e) {
            System.out.println("VRP::readProblem::Error parsing " + e.toString());
        }

        return matrix;
    }
}