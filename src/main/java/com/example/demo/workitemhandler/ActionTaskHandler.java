package com.example.demo.workitemhandler;


import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.process.ProcessWorkItemHandlerException;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ActionTaskHandler implements WorkItemHandler {
    public ActionTaskHandler() {
        super();
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        String action = (String) workItem.getParameter("action");
        Integer retry = getIntegerValue(workItem.getParameter("retry"));
        Integer retryDelay = getIntegerValue(workItem.getParameter("retryDelay"));
        Map<String, Object> outputMap = new HashMap<>();
        String status = null, exceptionMessage = null;

        try {
            log.info("ActionTask action : " + action); // -> here makes call to do the action
            log.info("Workitem id: " + workItem.getId());
            log.info("WorkItem ProcessInstanceId: " + workItem.getProcessInstanceId());
            // if sending to ems, then we also need to send exception details
            if ("SEND_TO_EMS".equals(action)) {
                String response = (String) workItem.getParameter("response");
                log.info("Error response to send EMS:" + response);
                status = (String) workItem.getParameter("status");
                log.info("Status code to send EMS:" + status);
            }

            // simulate error on passport scan task.
            if ("SCAN_PASSPORT".equals(action)) {
                status = "500";
                outputMap.put("response", "Internal server error");
                outputMap.put("status", status);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exceptionMessage = e.getMessage();
            outputMap.put("response", e.getMessage());
            outputMap.put("status", "500");
        }

        if (!"200".equals(status) && retry != null && retry > 0) {
            log.info("status not 200, retrying. Retry count: " + retry);

            wait(retryDelay);
            throw new ProcessWorkItemHandlerException("gate_orchestrator.retry_process", "RETRY", new RuntimeException(exceptionMessage));
        }


        workItemManager.completeWorkItem(workItem.getId(), outputMap);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }

    public void wait(Integer val) {
        try {
            val = val == null ? 5 : val;
            log.info("Waiting for " + val + " seconds before retrying.");
            TimeUnit.SECONDS.sleep(val);
        } catch (InterruptedException e) {
            log.error("Thread interrupted", e);
        }
    }

    public Integer getIntegerValue(Object val) {
        if (val == null) {
            return 0;
        } else if (val instanceof String) {
            return Integer.parseInt((String) val);
        } else if (val instanceof Integer) {
            return (Integer) val;
        } else {
            throw new RuntimeException("Not string or integer");
        }
    }
}
