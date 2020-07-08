package com.test.mastercard;

import com.google.common.graph.Graph;
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

    MutableGraph<Object> cityAdjacencyGraph = null;

    CityPairsLocator() {
        cityAdjacencyGraph = GraphBuilder.undirected().build();
        loadCities();
    }

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

    public Boolean isConnected(String origin, String destination) {
        return breadthFirstTraversal(cityAdjacencyGraph, origin, destination);
    }

    Boolean breadthFirstTraversal(Graph graph, String root, String dest) {
        Set<String> visited = new LinkedHashSet<String>();
        Queue<String> queue = new LinkedList<String>();
        queue.add(root);
        visited.add(root);
        while (!queue.isEmpty()) {
            String vertex = queue.poll();
            for (Object v : graph.successors(vertex)) {
                if(dest.equals(v)) {
                    return true;
                }
                if (!visited.contains(v)) {
                    visited.add((String) v);
                    queue.add((String) v);
                }
            }
        }
        logger.debug("Visited: "+ visited);
        return false;
    }
}
