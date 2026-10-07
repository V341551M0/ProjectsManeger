package com.projectsmaneger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProjectsManegerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProjectsManegerApplication.class, args);
  }
}
