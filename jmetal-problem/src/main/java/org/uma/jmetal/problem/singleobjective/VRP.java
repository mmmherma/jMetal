package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

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

    // Array of valid nodes. Check them when building the matrix
    private Integer []   validNodes = new Integer[] {1, 2, 4, 110, 124, 126, 129, 140, 141, 142, 145, 150, 153, 154,
            155, 159, 164, 165, 167, 169, 170, 171, 177, 184, 186, 199, 205, 207, 208, 211, 213, 215, 217, 221, 222,
            257, 258, 264, 298, 311, 318, 319, 324, 332, 338, 344, 349, 351, 354, 364, 375, 376, 377, 378, 381, 383,
            384, 402, 416, 418, 422, 425, 426, 428, 430, 431, 433, 434, 440, 448, 450, 453, 600, 601, 602, 603, 604,
            605, 606, 607, 608, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622, 623, 624, 625,
            626, 627, 628, 629, 630, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641};
    // Converto to List
    private List<Integer> listOfValidNodes = Arrays.asList(validNodes);
    // Timestamps maps. Prevent the problem to get old data
    Map<String, String> timestampMap;
    // Map to store distances between vertex
    Map<String, Double> distanceMap;
    // Map to store average time to travel the edge
    Map<String, String> timeMap;

    /**
     * VRP constructor
     * @param fileName  Absolute path to CSV file NY City DOT
     * @throws          IOException
     */
    public VRP(String fileName, boolean header, String separator, double capacity) throws IOException {
        // Get the distance matrix reading an instance of the problem
        distanceMatrix = readProblem(fileName, header, separator);
        vehicleCapacity = capacity;

        // Set client number (number of path * 2) - 1
        setNumberOfVariables(numberOfClients);
        setNumberOfObjectives(1);
        setName("VRP");
    }



    @Override
    public int getLength() {
        return numberOfClients;
    }

    @Override
    public void evaluate(PermutationSolution<Integer> solution) {
        // TODO How can I represent the problem
    }

    /**
     * Reads CSV file and builds the distance matrix to optimize
     * @param fileName      Absolute path to CSV file
     * @param header        true IF file has header ELSE false
     * @param separator     Character used to split CSV lines (usually ',')
     * @return              Distance matrix
     * @throws              IOException
     */
    private double [][] readProblem(String fileName, boolean header, String separator) throws IOException {
        System.out.println("Working Directory = " + System.getProperty("user.dir")+fileName);

        InputStream in = getClass().getResourceAsStream(fileName);
        // TODO Insert custom links to get a complete graph
        double [][] matrix = null;
        String []   headerLine = null;

        try {
            // Read CSV file from fileName
            FileReader fReader = new FileReader(System.getProperty("user.dir")+fileName);
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
            // Initialize distance map (store each link distence)
            distanceMap = new HashMap<String, Double>();
            // Initialize the time map (store each link average time)
            timeMap = new HashMap<String, String>();

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
                            timestampMap.put(fields[0], fields[4]);

                            // Compute element distance
                            double distance = computeDistance(fields[6]);
                            // Store link_id distance
                            distanceMap.put(fields[0], distance);
                            // Store average time
                            timeMap.put(fields[0], fields[2]);
                            System.out.println("Link " + fields[0] + " has " + distance + " meters");
                            System.out.println("Link " + fields[0] + " has " + fields[2] + " seconds");
                        }
                    } else {
                        // Insert new element
                        timestampMap.put(fields[0], fields[4]);

                        // Compute element distance
                        double distance = computeDistance(fields[6]);
                        // Store link_id distance
                        distanceMap.put(fields[0], distance);
                        // Store average time
                        timeMap.put(fields[0], fields[2]);
                        System.out.println("Link " + fields[0] + " has " + distance + " meters");
                        System.out.println("Link " + fields[0] + " has " + fields[2] + " seconds");
                    }
                }

                //String parsedLine = "";
                //for (int i = 0; i < fields.length; i++) {
                //    parsedLine += headerLine[i] + ": " + fields[i] + " ";
                //}
            }

            this.numberOfClients = (2*distanceMap.size()) - 1;
        } catch (Exception e) {
            System.out.println("VRP::readProblem::Error parsing " + e.toString());
        }

        return matrix;
    }

    /**
     * Given a path this function computes the distance (meters) of this path
     * @param path  The path to compute distance
     * @return      Distance (meters)
     */
    double computeDistance(String path) {
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

        // Compute clean path distance using haversine formula
        double distance = 0;
        for(int k = 0; k < coordinates.size()-1; k++) {
            String [] latitudeLongitude1 = coordinates.get(k).split(",");
            String [] latitudeLongitude2 = coordinates.get(k+1).split(",");
            distance += distanceFromCoordinates(Double.parseDouble(latitudeLongitude1[0]),
                    Double.parseDouble(latitudeLongitude1[1]),
                    Double.parseDouble(latitudeLongitude2[0]),
                    Double.parseDouble(latitudeLongitude2[1])
            );
        }

        return distance;
    }

    /**
     * Given 2 GPS coordinates this function computes the distance (meters) between them. Based on:
     *      https://www.movable-type.co.uk/scripts/latlong.html
     * @param latitude1     First GPS coordinate latitude
     * @param longitude1    First GPS coordinate longitude
     * @param latitude2     Secoond GPS coordinate latitude
     * @param longitude2    Second GPS coordinate longitude
     * @return              Distance between them (meters)
     */
    double distanceFromCoordinates(double latitude1, double longitude1, double latitude2, double longitude2) {
        // Set Earth radius
        double earthDIameter = 6371e3;

        // Latitudes to radians
        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);

        // Difference between latitudes and longitudes in radians
        double latitudeDifference = Math.toRadians(latitude2) - Math.toRadians(latitude1);
        double longitudeDifference = Math.toRadians(longitude2) - Math.toRadians(longitude1);

        // Compute distance
        double a = Math.sin(latitudeDifference/2) * Math.sin(latitudeDifference/2) +
                Math.cos(latitude1) * Math.cos(latitude2) * Math.sin(longitudeDifference/2) * Math.sin(longitudeDifference/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthDIameter * c;
    }
}