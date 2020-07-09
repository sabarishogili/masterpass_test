package com.test.mastercard.restservice;

import com.test.mastercard.CityPairsLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NetworkController {

    private static final Logger logger = LoggerFactory.getLogger(NetworkController.class);

    @Autowired
    CityPairsLocator cityPairsLocator;

    private final String NOT_MATCH = "no";
    private final String MATCH = "yes";

    @GetMapping("/connected")
    public String connected(@RequestParam(value = "origin", defaultValue = "") String origin, @RequestParam(value = "destination", defaultValue = "") String destination) {
        try {
            if (cityPairsLocator.isConnected(origin, destination)) {
                return MATCH;
            }
        } catch (Exception e) {
            logger.error("Error while computing connection between cities {}, {}", origin, destination, e);
        }
        return NOT_MATCH;
    }
}
