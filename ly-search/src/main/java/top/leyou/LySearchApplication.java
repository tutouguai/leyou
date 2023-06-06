package top.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LySearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LySearchApplication.class, args);
    }
}




//@Component
//class MyRunner implements CommandLineRunner {
//
//    private static final Logger logger = LoggerFactory.getLogger(MyRunner.class);
//
//    @Value("${spring.data.elasticsearch.client.reactive.endpoints}")
//    private String elasticsearchEndpoints;
//
//    @Override
//    public void run(String... args) throws Exception {
//        logger.info("Elasticsearch endpoints: {}", elasticsearchEndpoints);
//    }
//}