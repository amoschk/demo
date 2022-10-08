package com.example.demo.workitemhandler;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;


public class WaitWorkItemHandler  implements WorkItemHandler {

    public WaitWorkItemHandler(){
        super();
    }
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        System.out.println("Executing work item");
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
    }
}
