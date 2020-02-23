package com.persoff68.fatodo;

import com.persoff68.fatodo.config.AppProfileUtil;
import com.persoff68.fatodo.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Properties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ExtendedMongoSkeletonApplication {

    public static void main(String[] args) {
        Properties defaultProfileProperties = AppProfileUtil.getDefaultProfile();
        SpringApplication app = new SpringApplication(ExtendedMongoSkeletonApplication.class);
        app.setDefaultProperties(defaultProfileProperties);
        app.run(args);
    }

}