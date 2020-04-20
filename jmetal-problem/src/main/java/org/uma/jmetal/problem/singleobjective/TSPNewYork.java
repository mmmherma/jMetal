package org.uma.jmetal.problem.singleobjective;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

/**
 * Class representing a single-objective TSP (Traveling Salesman Problem) problem.
 * It accepts data files from NY DOT JSON format
 */
@SuppressWarnings("serial")
public class TSPNewYork extends AbstractIntegerPermutationProblem {
  private int         numberOfCities ;
  private double [][] distanceMatrix ;

  /**
   * Creates a new TSP problem instance
   */
  public TSPNewYork(String distanceFile) throws IOException {
    distanceMatrix = readProblem(distanceFile) ;

    setNumberOfVariables(numberOfCities) ;
    setNumberOfObjectives(1) ;
    setName("TSP") ;
  }

  /** Evaluate() method */
  public void evaluate(PermutationSolution<Integer> solution){
    double fitness1 ;

    fitness1 = 0.0 ;

    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x ;
      int y ;

      x = solution.getVariable(i) ;
      y = solution.getVariable(i+1) ;

      fitness1 += distanceMatrix[x][y] ;
    }
    int firstCity ;
    int lastCity  ;

    firstCity = solution.getVariable(0) ;
    lastCity = solution.getVariable(numberOfCities - 1) ;

    fitness1 += distanceMatrix[firstCity][lastCity] ;

    solution.setObjective(0, fitness1);
  }

  private double [][] readProblem(String file) throws IOException {
    double [][] matrix = null;

    try {
      // Create JSON parser
      JSONParser jsonParser = new JSONParser();

      // Read file from resources
      FileReader jsonCities = new FileReader(file);

      // Parse cities
      Object citiesObj = jsonParser.parse(jsonCities);
      JSONArray cities = (JSONArray) citiesObj;

      // Get te latest updated unique cities
      Map<String, JSONObject> uniqueCities = new HashMap<>();
      cities.forEach(city -> {
        String cityId = ((JSONObject) city).get("id").toString();
        String timestamp = ((JSONObject) city).get("data_as_of").toString();
        
        if (!uniqueCities.containsKey(cityId)) {
          uniqueCities.put(cityId, (JSONObject) city);
        } else {
          if (uniqueCities.get(cityId).get("data_as_of").toString().compareTo(timestamp) <= 0) {
            uniqueCities.replace(cityId, (JSONObject) city);
          }
        }
      });

      // Clean GPS coordinates
      Map<String, JSONObject> cleanCitiesMap = new HashMap<>();
      uniqueCities.forEach( (id, city) -> cleanCitiesMap.put(id, cleanCoordinates(city)) );

      // Compute distances
      Map<String, Double> linkDistance = new HashMap<>();
      cleanCitiesMap.forEach( (id, city) -> linkDistance.put(id, computeDistance(city)) );
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return matrix;
  }

  private JSONObject cleanCoordinates(JSONObject city) {
    String clean = new String();

    // Get coordinates from JSONObject
    String coordinates = city.get("link_points").toString();

    // Split coordinates using spaces
    String[] splitCoordinates = coordinates.split("\\s+");

    for (int i = 0; i < splitCoordinates.length; i++) {
      String[] latLon = splitCoordinates[i].split(",");

      if (latLon.length == 2) {
        clean += splitCoordinates[i] + " ";
      }
    }

    city.put("link_points", clean.substring(0, clean.length()-1));

    return city;
  }

  private Double computeDistance(JSONObject city) {
    Double distance = 0.0;

    // Get coordinates
    String coordinates = city.get("link_points").toString();
    // Split coordinates
    String[] splitCoordinates = coordinates.split("\\s+");

    for (int i = 0; i < splitCoordinates.length-2; i++) {
      String[] latlon1 = splitCoordinates[i].split(",");
      String[] latlon2 = splitCoordinates[i+1].split(",");

      distance += distanceFromCoordinates(
              Double.parseDouble(latlon1[0]), Double.parseDouble(latlon1[1]),
              Double.parseDouble(latlon2[0]), Double.parseDouble(latlon2[1])
      );
    }

    return distance;
  }

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

  @Override public int getLength() {
    return numberOfCities ;
  }
}
