package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.*;

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
    Map<String, String> timestampMap;

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

            // Initialize matrix
            matrix = new double[listOfValidNodes.size()][listOfValidNodes.size()];
            // Initialize timestamp map (avoids null pointer exception)
            timestampMap = new HashMap<>();

            // Remove " from first and last field
            String line = "";
            while ((line = br.readLine()) != null) {
                //line = line.substring(1, 1);
                line = line.substring(1, line.length()-1);
                String [] fields = line.split(separator);

                // Check if ode is a valid node
                if (listOfValidNodes.contains(Integer.parseInt(fields[0]))) {
                    // Check if is already inserted and timestamp is newer
                    // IF already exists THEN compare timestamps ELSE insert to map
                    if(timestampMap.containsKey(fields[0])) {
                        if(fields[4].compareTo(timestampMap.get(fields[0])) < 0) {
                            // Substitude new timestamp
                            //timestampMap.put(fields[0], fields[4]);

                            // Compute element distance
                            double distance = computeDistance(fields[6]);
                        }
                    } else {
                        // Insert new element
                        //timestampMap.put(fields[0], fields[4]);

                        // Compute element distance
                        double distance = computeDistance(fields[6]);
                    }
                }

                String parsedLine = "";
                for (int i = 0; i < fields.length; i++) {
                    parsedLine += headerLine[i] + ": " + fields[i] + " ";
                }
                //System.out.println(parsedLine);
                //System.out.println("*******");
            }
        } catch (Exception e) {
            System.out.println("VRP::readProblem::Error parsing " + e.toString());
        }

        return matrix;
    }

    /**
     * Given a path this function computes the distance (meters) of this path
     * @param path The path to compute distance
     * @return Distance (meters)
     */
    double computeDistance(String path) {
        double distance = 0;

        // Get path
        // Split coordinates by one or more spaces
        List<String> coordinates = new LinkedList<String>(Arrays.asList(path.split("\\s+")));

        // Clean coordinates
        //      Remove precision lower than 4 decimals at latitude or longitude
        //      Clean coordinates within only latitude or longitude
        int i = 0;
        while (i < coordinates.size()) {
            String [] latitudeLongitude = coordinates.get(i).split(",");
            if(latitudeLongitude.length == 2) {
                // Get latitude and longitude
                String latitude = latitudeLongitude[0];
                String longitude = latitudeLongitude[1];

                // Get latitude and longitude precision
                int latitudePrecision = latitude.length() - latitude.indexOf('.') - 1;
                int longitudePrecision = longitude.length() - longitude.indexOf('.') - 1;

                // Check latitude and longitude precision
                if(latitudePrecision < 4 || longitudePrecision < 4) {
                    // Remove item
                    coordinates.remove(i);
                } else {
                    i++;
                }
            } else {
                // Remove item
                coordinates.remove(i);
            }
        }

        // Compute distance using haversine formula

        return distance;
    }

    double distanceFromCoordinates(double latitude1, double longitude1, double latitude2, double longitude2) {
        double earthRadius = 6371e3;
        double toRadians = Math.PI / 180.0;

        latitude1 = latitude1 * toRadians;
        latitude2 = latitude2 * toRadians;

        double latitudeDifference = latitude2*toRadians - latitude1*toRadians;
        double longitudeDifference = longitude2*toRadians - longitude1*toRadians;

        double a = Math.sin(latitudeDifference/2) * Math.sin(latitudeDifference/2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.sin(longitudeDifference/2) * Math.sin(longitudeDifference/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius * c;
    }
}