package org.gk.gtdservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class GtdServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(GtdServiceApplication.class);

    void main() {
        logger.info("Starting GTD Service Application");
        SpringApplication.run(GtdServiceApplication.class);
        logger.info("GTD Service Application started successfully");
    }
}
