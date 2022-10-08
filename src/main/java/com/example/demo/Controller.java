package com.example.demo;

import com.example.demo.workitemhandler.ActionTaskHandler;
import com.example.demo.workitemhandler.PassportScanWorkItemHandler;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.jbpm.workflow.instance.node.WorkItemNodeInstance;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//https://docs.jboss.org/jbpm/v6.0.Beta2/javadocs/org/kie/api/KieServices.html
//https://docs.jboss.org/jbpm/v6.3/userguide/ch05.html#d0e1850

@RestController
@Slf4j
public class Controller {

    @Autowired
    private static KieContainer kieContainer;

    private static File file;

    private static Resource resource;
    private static KieServices kieServices1 = KieServices.Factory.get();
    private static KieFileSystem kfs1 = kieServices1.newKieFileSystem();
    private static List<String> listStr = new ArrayList<String>();
    private static KieRepository kr1 = kieServices1.getRepository();
    private static KieSession kieSession;

    //	private static String path = "C:\\Users\\G528951\\winsw\\demo-test\\resources\\";
    private static String path = "src/main/resources/com/example/demo/process/";

    // http://localhost:8180/getValue?type=a&colType=1
    @GetMapping(value = "startProcess")
    public void startProcess(@RequestParam String processName) {
        kieSession = kieContainer.newKieSession();
//        kieSession.getWorkItemManager().registerWorkItemHandler("CustomTask", new PassportScanWorkItemHandler("helloworld.exc_process", "RETRY"));
//        kieSession.getWorkItemManager().registerWorkItemHandler("Wait", new WaitWorkItemHandler());
        kieSession.getWorkItemManager().registerWorkItemHandler("ActionTask", new ActionTaskHandler());

        Map<String, Object> map = new HashMap<>();
//        map.put("retry", 3);
        kieSession.startProcess(processName, map);

//        kieSession.fireAllRules();
//        kieSession.dispose();

    }

    @GetMapping(value = "continue")
    public void continueProcess(@RequestParam String signal, @RequestParam String data, @RequestParam Long processId){
        ProcessInstance pi = kieSession.getProcessInstance(processId);
        pi.signalEvent(signal, data);
    }

    // http://localhost:8180/runProcess?process=
    @GetMapping(value = "runProcess")
    public String getValue(@RequestParam String process) {

        kieSession = kieContainer.newKieSession();
        kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new PassportScanWorkItemHandler());
        kieSession.startProcess(process);
        kieSession.fireAllRules();
        kieSession.dispose();

        return "done";
    }

    // default loading of res files on startup
    @EventListener(ApplicationReadyEvent.class)
    public List<String> defaultResFiles() {
        log.info("----- START defaultResFiles() -----");
        log.info("----- old resource files: " + listStr + " -----");
        kfs1 = kieServices1.newKieFileSystem();
        listStr.clear();


        listStr.add(path + "gate_orc_main_v2.bpmn");
        file = new File(path + "gate_orc_main_v2.bpmn");
        resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
        kfs1.write(resource);

        listStr.add(path + "EMS_process.bpmn");
        file = new File(path + "EMS_process.bpmn");
        resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
        kfs1.write(resource);

        listStr.add(path + "retry_process.bpmn");
        file = new File(path + "retry_process.bpmn");
        resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
        kfs1.write(resource);

        log.info("----- new resource files: " + listStr + " -----");
        KieBuilder kb = kieServices1.newKieBuilder(kfs1);
        kb.buildAll();
        kieContainer = kieServices1.newKieContainer(kr1.getDefaultReleaseId());
        log.info("----- END defaultResFiles() -----");
        return (listStr);
    }

    @GetMapping("runResFile")
    public String runResFile(@RequestParam String fileName) {
        file = new File(path + fileName);
        resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
        kfs1.write(resource);

        return file.getAbsolutePath();
    }

    // http://localhost:8080/updateResFiles?config=A
    @GetMapping("updateResFiles")
    public List<String> updateResFiles(@RequestParam String config) {
        log.info("----- START updateResFiles() -----");
        log.info("----- old resource files: " + listStr + " -----");
        kfs1 = kieServices1.newKieFileSystem();
        listStr.clear();

        switch (config.toLowerCase()) {
            case "test":
                listStr.add(path + "HelloWorld.bpmn2");
                file = new File(path + "HelloWorld.bpmn2");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                listStr.add(path + "rulesA1.drl");
                file = new File(path + "rulesA1.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                listStr.add(path + "rulesA2.drl");
                file = new File(path + "rulesA2.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                break;

            case "event":
                listStr.add(path + "HelloWorldEvent.bpmn2");
                file = new File(path + "HelloWorldEvent.bpmn2");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                listStr.add(path + "rulesA1.drl");
                file = new File(path + "rulesA1.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                listStr.add(path + "rulesA2.drl");
                file = new File(path + "rulesA2.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                break;

            case "a":
                listStr.add(path + "HelloWorldA.bpmn2");
                file = new File(path + "HelloWorldA.bpmn2");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                listStr.add(path + "rulesA1.drl");
                file = new File(path + "rulesA1.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                listStr.add(path + "rulesA2.drl");
                file = new File(path + "rulesA2.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                break;

            case "b":
                listStr.add(path + "HelloWorldB.bpmn2");
                file = new File(path + "HelloWorldB.bpmn2");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                listStr.add(path + "rulesB1.drl");
                file = new File(path + "rulesB1.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                listStr.add(path + "rulesB2.drl");
                file = new File(path + "rulesB2.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                break;

            case "c":
                listStr.add(path + "HelloWorldC.bpmn2");
                file = new File(path + "HelloWorldC.bpmn2");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                listStr.add(path + "rulesC1.drl");
                file = new File(path + "rulesC1.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                listStr.add(path + "rulesC2.drl");
                file = new File(path + "rulesC2.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                break;

            case "demoprocessone":
                listStr.add(path + "demo_process_one.bpmn");
                file = new File(path + "demo_process_one.bpmn");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                listStr.add(path + "br_one.drl");
                file = new File(path + "br_one.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                break;

            case "restprocess":
                listStr.add(path + "main_flow.bpmn");
                file = new File(path + "main_flow.bpmn");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                break;

            case "taskprocess":
                listStr.add(path + "main_flow_two.bpmn");
                file = new File(path + "main_flow_two.bpmn");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                break;

            case "mainprocess":
                listStr.add(path + "main_process.bpmn");
                file = new File(path + "main_process.bpmn");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                listStr.add(path + "exc_process.bpmn");
                file = new File(path + "exc_process.bpmn");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                break;

            default:
                file = new File(path + "HelloWorldA.bpmn2");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
                kfs1.write(resource);
                file = new File(path + "rulesA1.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
                file = new File(path + "rulesA2.drl");
                resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
                kfs1.write(resource);
        }

//    	for (String f : searchFileDirForRes().get(0)) {
//    		log.info("----- BPMN2 file: " + f + " -----");
//    		listStr.add(f);
//    		file = new File(f);
//    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.BPMN2);
//    		kfs1.write(resource);
//    	}
//    	for (String f : searchFileDirForRes().get(1)) {
//    		log.info("----- DRL file: " + f + " -----");
//    		listStr.add(f);
//    		file = new File(f);
//    		resource = kieServices1.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
//    		kfs1.write(resource);
//    	}
        log.info("----- new resource files: " + listStr + " -----");
        KieBuilder kb = kieServices1.newKieBuilder(kfs1);
        kb.buildAll();
        kieContainer = kieServices1.newKieContainer(kr1.getDefaultReleaseId());
        log.info("----- END updateResFiles() -----");
        return (listStr);
    }

    @GetMapping(value = "getResFileName")
    public List<List<String>> searchFileDirForRes() {
        List<List<String>> files = new ArrayList<List<String>>();
        try {
            List<String> filesBPMN2 = findFiles(Paths.get(path), "bpmn2");
            List<String> filesDRL = findFiles(Paths.get(path), "drl");
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
