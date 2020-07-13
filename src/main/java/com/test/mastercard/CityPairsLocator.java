package com.test.mastercard;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

@Component
public class CityPairsLocator {

    private static final Logger logger = LoggerFactory.getLogger(CityPairsLocator.class);

    MutableGraph<String> cityAdjacencyGraph = null;

    CityPairsLocator() {
        cityAdjacencyGraph = GraphBuilder.undirected().build();
        loadCities();
    }

    /**
     * It loads the connected cities from the city.txt file from resources. And forms a Mutable graph data structure to
     * simply the the operation in the future and to store the data in an organised format.
     */
    private void loadCities() {
        InputStream resource = null;
        try {
            resource = new ClassPathResource(
                    "city.txt").getInputStream();
        } catch (IOException e) {
            logger.error("Error loading connected cities file", e);
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource))) {
            Consumer<String> addEdge = (s) -> {
                String[] cities = s.split(",", 2);
                cityAdjacencyGraph.putEdge(cities[0].trim(), cities[1].trim());
            };
            reader.lines()
                    .forEach(addEdge);
        } catch (IOException e) {
            logger.error("error while populating the cities from file.", e);
        }
    }

    /**
     * It checks whether the Origin city is connected to the Destination city in the graph. It uses Breadth First
     * Traversal to traverse the graph for the matching destination node. And returns true if the match found,
     * otherwise returns false.
     *
     * @param origin an name of the Origin city.
     * @param destination an name of the Destination city.
     * @return connected as true if origin is connected to destination otherwise returns false.
     *
     **/
    public Boolean isConnected(String origin, String destination) {
        Set<String> visited = new LinkedHashSet<String>();
        Queue<String> queue = new LinkedList<String>();
        queue.add(origin);
        visited.add(origin);
        while (!queue.isEmpty()) {
            String vertex = queue.poll();
            try {
                for (String v : cityAdjacencyGraph.successors(vertex)) {
                    if(destination.equals(v)) {
                        return true;
                    }
                    if (!visited.contains(v)) {
                        visited.add(v);
                        queue.add(v);
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.error("City not found", e);
                return false;
            }
        }
        logger.debug("Visited: "+ visited);
        return false;
    }
}
