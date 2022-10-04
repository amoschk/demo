package com.example.demo;


import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {
	
    private static final Logger logger 
      = LoggerFactory.getLogger(DemoApplication.class);
	
	public static void main(String[] args) {
		logger.info("\n");
		logger.info("Example log from {}", DemoApplication.class.getSimpleName());
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	public KieContainer kieContainer() {
		logger.info("----- in kieContainer -----");
		return KieServices.Factory.get().getKieClasspathContainer();
	}
}
