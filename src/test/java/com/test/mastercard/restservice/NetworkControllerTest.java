package com.test.mastercard.restservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class NetworkControllerTest {

    @InjectMocks
    private NetworkController controller;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    final private String server = "http://localhost:";
    final private String URL_SEPERATOR = "/";
    final static private String QUERY_SEPERATOR = "?";
    final static private String PATH_SEPERATOR = "&";
    final private String END_POINT = "connected";
    private StringBuilder url;

    public static Stream<String> incorrectParams() {
        return Stream.of(
                QUERY_SEPERATOR + "origin=Boston",
                QUERY_SEPERATOR + "origin=",
                QUERY_SEPERATOR + "origin=null",
                QUERY_SEPERATOR
        );
    }

    @BeforeEach
    public void setup() {
        url = new StringBuilder();
        url.append(server).append(port);
        url.append(URL_SEPERATOR);
        url.append(END_POINT);
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void whenDirectlyConnectedShouldReturnYes() throws Exception {
        assertThat(this.restTemplate.getForObject(url
                        .append(QUERY_SEPERATOR)
                        .append( "origin=New York" )
                        .append("&destination=Boston")
                        .toString(),
                String.class)).isEqualTo("yes");
    }

    @Test
    public void whenInDirectlyConnectedShouldReturnYes() throws Exception {
        assertThat(this.restTemplate.getForObject(url
                        .append(QUERY_SEPERATOR)
                        .append("origin=Boston")
                        .append("&destination=Philadelphia")
                        .toString(),
                String.class)).isEqualTo("yes");
    }

    @Test
    public void whenNoConnectionShouldReturnNo() throws Exception {
        assertThat(this.restTemplate.getForObject(url
                        .append(QUERY_SEPERATOR)
                        .append("origin=Boston")
                        .append("&destination=Seattle")
                        .toString(),
                String.class)).isEqualTo("no");
    }

    @ParameterizedTest
    @EmptySource
    @MethodSource("incorrectParams")
    public void whenIncorrectParamsShouldReturnNo(String params) throws Exception {
        assertThat(this.restTemplate.getForObject(url
                        .append(params)
                        .toString(),
                String.class)).isEqualTo("no");
    }

    @Test
    public void whenCityDoesntExistsShouldReturnNo() throws Exception {
        assertThat(this.restTemplate.getForObject(url
                        .append(QUERY_SEPERATOR)
                        .append("origin=Random")
                        .append("&destination=City")
                        .toString(),
                String.class)).isEqualTo("no");
    }

}