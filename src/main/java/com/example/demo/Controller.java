package com.example.demo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//https://docs.jboss.org/jbpm/v6.0.Beta2/javadocs/org/kie/api/KieServices.html
//https://docs.jboss.org/jbpm/v6.3/userguide/ch05.html#d0e1850

//make sure old container is stopped before loading new container

@RestController
public class Controller {

	@Autowired
	KieContainer kieContainer;
	
	File file;

	Resource resource;
	KieServices kieServices1 = KieServices.Factory.get();
	KieFileSystem kfs1 = kieServices1.newKieFileSystem();
	List<String> listStr = new ArrayList<String>();
	KieRepository kr1 = kieServices1.getRepository();
	KieSession kieSession;

	
    private static final Logger logger 
    = LoggerFactory.getLogger(DemoApplication.class);
    
 
    
    // http://localhost:8080/getValue?type=a&colType=1
	@GetMapping(value="getValue")
	public Test getValue(@RequestParam String type, @RequestParam String colType) {
		logger.info("----- START getValue() with param: type: " + type + ", colType: " + colType + " -----");
		Test test = new Test();
		test.setType(type);
		test.setColType(colType);
		
		kieSession = kieContainer.newKieSession();
		kieSession.insert(test);
		kieSession.startProcess("com.example.demo.process.HelloWorld");
		kieSession.fireAllRules();
		kieSession.dispose();
		
// Method2: try dynamic change of bpmn2 file (hardcode bpmn2 and drl files) 
//https://groups.google.com/g/drools-setup/c/qfXd9rBU_2w
//		KieServices kieServices = KieServices.Factory.get();
//		KieFileSystem kfs = kieServices.newKieFileSystem();
//		KieRepository kr = kieServices1.getRepository();
		
//		updateResFiles();
		
//		KieBuilder kb = kieServices1.newKieBuilder(kfs1);
//		kb.buildAll();
//		kieContainer = kieServices1.newKieContainer(kr1.getDefaultReleaseId());
//		KieSession kieSession = kieContainer.newKieSession();
//		kieSession.insert(test);
//		kieSession.startProcess("com.example.demo.process.HelloWorld");
//		kieSession.fireAllRules();
//		kieSession.dispose();
	
		logger.info("----- END getValue with test: " + test.toString() + " -----");
		return test;
	}
	
	// default loading of res files on startup
		@EventListener(ApplicationReadyEvent.class)
	    public List<String> defaultResFiles() {
	    	logger.info("----- START defaultResFiles() -----"); 
	    	logger.info("----- old resource files: " + listStr + " -----");
	    	kfs1 = kieServices1.newKieFileSystem();
	    	listStr.clear();
	    	
//	    	for (String f : searchFileDirForRes().get(0)) {
//	    		logger.info("----- BPMN2 file: " + f + " -----");
//	    		listStr.add(f);
//	    		file = new File(f);
//	    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
//	    		kfs1.write(resource);
//	    	}
//	    	for (String f : searchFileDirForRes().get(1)) {
//	    		logger.info("----- DRL file: " + f + " -----");
//	    		listStr.add(f);
//	    		file = new File(f);
//	    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
//	    		kfs1.write(resource);
//	    	}
	    	
	    	String path = "C:\\Users\\G528951\\winsw\\demo-test\\resources\\";
	    	
    		listStr.add(path+"HelloWorldA.bpmn2");
    		file = new File(path+"HelloWorldA.bpmn2");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
    		kfs1.write(resource);
    		listStr.add(path+"rulesA1.drl");
    		file = new File(path+"rulesA1.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		listStr.add(path+"rulesA2.drl");
    		file = new File(path+"rulesA2.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		
	    	logger.info("----- new resource files: " + listStr + " -----");
			KieBuilder kb = kieServices1.newKieBuilder(kfs1);
			kb.buildAll();
			kieContainer = kieServices1.newKieContainer(kr1.getDefaultReleaseId());
	    	logger.info("----- END defaultResFiles() -----");
	    	return (listStr);
	    }
	
	// http://localhost:8080/updateResFiles?config=A
    @GetMapping("updateResFiles")
    public List<String> updateResFiles(@RequestParam String config) {
    	logger.info("----- START updateResFiles() -----"); 
    	logger.info("----- old resource files: " + listStr + " -----");
    	kfs1 = kieServices1.newKieFileSystem();
    	listStr.clear();
    	
    	String path = "C:\\Users\\G528951\\winsw\\demo-test\\resources\\";
    	
    	switch (config.toLowerCase()) {
    	case "test":
    		listStr.add(path+"HelloWorld.bpmn2");
    		file = new File(path+"HelloWorld.bpmn2");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
    		kfs1.write(resource);
    		listStr.add(path+"rulesA1.drl");
    		file = new File(path+"rulesA1.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		listStr.add(path+"rulesA2.drl");
    		file = new File(path+"rulesA2.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		break;
    		
    	case "event":
    		listStr.add(path+"HelloWorldEvent.bpmn2");
    		file = new File(path+"HelloWorldEvent.bpmn2");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
    		kfs1.write(resource);
    		listStr.add(path+"rulesA1.drl");
    		file = new File(path+"rulesA1.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		listStr.add(path+"rulesA2.drl");
    		file = new File(path+"rulesA2.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		break;
    		
    	case "a":
    		listStr.add(path+"HelloWorldA.bpmn2");
    		file = new File(path+"HelloWorldA.bpmn2");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
    		kfs1.write(resource);
    		listStr.add(path+"rulesA1.drl");
    		file = new File(path+"rulesA1.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		listStr.add(path+"rulesA2.drl");
    		file = new File(path+"rulesA2.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		break;
    		
    	case "b":
    		listStr.add(path+"HelloWorldB.bpmn2");
    		file = new File(path+"HelloWorldB.bpmn2");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
    		kfs1.write(resource);
    		listStr.add(path+"rulesB1.drl");
    		file = new File(path+"rulesB1.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		listStr.add(path+"rulesB2.drl");
    		file = new File(path+"rulesB2.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		break;
    		
    	case "c":
    		listStr.add(path+"HelloWorldC.bpmn2");
    		file = new File(path+"HelloWorldC.bpmn2");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
    		kfs1.write(resource);
    		listStr.add(path+"rulesC1.drl");
    		file = new File(path+"rulesC1.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		listStr.add(path+"rulesC2.drl");
    		file = new File(path+"rulesC2.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		break;

    	default:
    		file = new File(path+"HelloWorldA.bpmn2");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
    		kfs1.write(resource);
    		file = new File(path+"rulesA1.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);
    		file = new File(path+"rulesA2.drl");
    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
    		kfs1.write(resource);    	
    	}

//    	for (String f : searchFileDirForRes().get(0)) {
//    		logger.info("----- BPMN2 file: " + f + " -----");
//    		listStr.add(f);
//    		file = new File(f);
//    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
//    		kfs1.write(resource);
//    	}
//    	for (String f : searchFileDirForRes().get(1)) {
//    		logger.info("----- DRL file: " + f + " -----");
//    		listStr.add(f);
//    		file = new File(f);
//    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
//    		kfs1.write(resource);
//    	}
    	logger.info("----- new resource files: " + listStr + " -----");
		KieBuilder kb = kieServices1.newKieBuilder(kfs1);
		kb.buildAll();
		kieContainer = kieServices1.newKieContainer(kr1.getDefaultReleaseId());
    	logger.info("----- END updateResFiles() -----");
    	return (listStr);
    }
	
	@GetMapping(value="getResFileName")
	public List<List<String>> searchFileDirForRes() {
		List<List<String>> files = new ArrayList<List<String>>();
		try {
			List<String>filesBPMN2 = findFiles(Paths.get("C:\\Users\\G528951\\winsw\\demo-test\\resources"), "bpmn2");
			List<String>filesDRL = findFiles(Paths.get("C:\\Users\\G528951\\winsw\\demo-test\\resources"), "drl");
			files.add(filesBPMN2);
			files.add(filesDRL);

        } catch (Exception e) {
            e.printStackTrace();
        }
		return files;
	}
	
	public static List<String> findFiles(Path path, String fileExtension)
	        throws Exception {

	        if (!Files.isDirectory(path)) {
	            throw new IllegalArgumentException("Path must be a directory!");
	        }
	        List<String> result;
	        try (Stream<Path> walk = Files.walk(path)) {
	            result = walk
	                    .filter(p -> !Files.isDirectory(p))
	                    .map(p -> p.toString().toLowerCase())
	                    .filter(f -> f.endsWith(fileExtension))
	                    .collect(Collectors.toList());
	        }

	        return result;
	    }
	
}
