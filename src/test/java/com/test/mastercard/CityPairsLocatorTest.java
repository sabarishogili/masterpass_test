package com.test.mastercard;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
class CityPairsLocatorTest {

    @Mock
    private MutableGraph<Object> cityAdjacencyGraph;
    @InjectMocks
    private CityPairsLocator cityPairsLocator;

    @Test
    public void contexLoads() throws Exception {
        assertThat(cityPairsLocator).isNotNull();
    }

    private void setUpInitialGraph() {
        cityPairsLocator.cityAdjacencyGraph = GraphBuilder.undirected().build();
        cityPairsLocator.cityAdjacencyGraph.putEdge("Orlando","Las Vegas");
        cityPairsLocator.cityAdjacencyGraph.putEdge("New York","Las Vegas");
        cityPairsLocator.cityAdjacencyGraph.putEdge("Seattle","Orlando");
        cityPairsLocator.cityAdjacencyGraph.putEdge("Wailuku","Lanai City");
    }

    @Nested
    class WhenCheckingIfIsConnected {
        private final String ORIGIN = "Seattle";
        private final String DESTINATION = "Las Vegas";

        @BeforeEach
        void setup() {
            setUpInitialGraph();
        }

        @Test
        void validDirectConnectedCities() {
            assertTrue("A road should exists between Origin and Destination cities",
                    cityPairsLocator.isConnected("Seattle", "Orlando"));
        }

        @Test
        void validInDirectConnectedCities() {
            assertTrue("A connecting road should exists between Origin and Destination cities",
                    cityPairsLocator.isConnected("Seattle", "Las Vegas"));
        }

        @Test
        void validNotConnectedCities() {
            assertFalse("A connecting road should Not exists between cities",
                    cityPairsLocator.isConnected("Wailuku", "New York"));
        }

        @Test
        void invalidCities() {
            assertFalse("A connecting road should exists between Origin and Destination cities",
                    cityPairsLocator.isConnected("Bothell", "Bellevue"));
        }

        @Test
        void invalidDestinationCity() {
            assertFalse("A connecting road should exists between Origin and Destination cities",
                    cityPairsLocator.isConnected("Seattle", "Bellevue"));
        }

    }
}