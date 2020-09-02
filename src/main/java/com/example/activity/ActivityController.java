package com.example.activity;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ActivityController {
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public ActivityController(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @GetMapping("/start-process")
    public String startProcess() {

        ProcessInstance pi = runtimeService.startProcessInstanceByKey("my-process");
        System.out.println("process name : " + pi.getName() + " id :" + pi.getId());
        return "Process started. Number of currently running"
                + " process instances = "
                + runtimeService.createProcessInstanceQuery().count();
    }

    @GetMapping("/get-tasks/{processInstanceId}")
    public List<TaskRepresentation> getTasks(
            @PathVariable String processInstanceId) {

        List<Task> usertasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        return usertasks.stream()
                .map(task -> new TaskRepresentation(
                        task.getId(), task.getName(), task.getProcessInstanceId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/complete-task-A/{processInstanceId}")
    public void completeTaskA(@PathVariable String processInstanceId) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        taskService.complete(task.getId());
    }

    @GetMapping("/running-process")
    public String runningProcess() {
        return "Process started. Number of currently running"
                + "process instances = "
                + runtimeService.createProcessInstanceQuery().count();
    }
}
