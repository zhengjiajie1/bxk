package com.rabbiter.fm;

import com.rabbiter.fm.common.utils.OrderTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class FleaMarketApplication {

    public static void main(String[] args) throws UnknownHostException {
        Logger log = LoggerFactory.getLogger(FleaMarketApplication.class);
        ConfigurableApplicationContext applicationContext = SpringApplication.run(FleaMarketApplication.class, args);
        OrderTaskHandler.run();
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        log.info(
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port")
        );
        
    }

}
