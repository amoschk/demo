package com.example.demo.workitemhandler;

import org.kie.api.runtime.process.ProcessWorkItemHandlerException;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import java.util.HashMap;
import java.util.Map;

public class PassportScanWorkItemHandler implements WorkItemHandler {
    private String processId;
    private ProcessWorkItemHandlerException.HandlingStrategy strategy;

    public PassportScanWorkItemHandler(){
        super();
    }

    public PassportScanWorkItemHandler(String processId, ProcessWorkItemHandlerException.HandlingStrategy strategy){
        super();
        this.processId = processId;
        this.strategy = strategy;
    }

    public PassportScanWorkItemHandler(String processId, String strategy){
        super();
        this.processId = processId;
        this.strategy = ProcessWorkItemHandlerException.HandlingStrategy.valueOf(strategy);
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        String actionLocal = (String) workItem.getParameter("action");
        if(actionLocal == null){
            actionLocal = "";
        }
        if("ABORT".equals(actionLocal)){
            System.out.println("ABORTING WORK ITEM");
            this.abortWorkItem(workItem, workItemManager);
        }
        System.out.println("PassportScanWorkHandler action: " + actionLocal);
        if(!"COMPLETE".equals(actionLocal)){
            // TODO this part should run the actual passport scanning, resulting in action = COMPLETE if scan is successful
        }

        if("COMPLETE".equals(actionLocal)){
            workItemManager.completeWorkItem(workItem.getId(), null);
        } else {
            System.out.println("NOT COMPLETE SO WE ARE GOING TO EXCEPTION with processid: " + processId + " and strategy: " + strategy);
            throw new ProcessWorkItemHandlerException(processId, strategy, new RuntimeException("Passport Scan Failed"));
        }


    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }
}
