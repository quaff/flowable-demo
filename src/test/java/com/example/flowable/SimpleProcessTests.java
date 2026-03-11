package com.example.flowable;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class SimpleProcessTests {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    void simpleProcessTest() {
        String processDefinitionKey = "simpleProcess";
        String startUserId = "test";
        Map<String, Object> variables = Map.of("startUserId", startUserId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        assertThat(processInstance.getProcessDefinitionKey()).isEqualTo(processDefinitionKey);

        Task task = taskService.createTaskQuery().singleResult();
        assertThat(task.getName()).isEqualTo("User Task");

        taskService.complete(task.getId());
        assertThat(runtimeService.createProcessInstanceQuery().count()).isEqualTo(0);
    }
}
