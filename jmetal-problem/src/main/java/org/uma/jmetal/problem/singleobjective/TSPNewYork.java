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

      uniqueCities.forEach((id, city) -> System.out.println(id + " " + city.get("link_points")));

      System.out.println("Cities size(): " + cities.size());
      System.out.println("Unique cities size(): " + uniqueCities.size());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return matrix;
  }

  /*private double [][] getDistanceMatrix(Map<String, JSONObject> cities) {
    double [][] matrix = ;

    return matrix;
  }*/

  @Override public int getLength() {
    return numberOfCities ;
  }
}
