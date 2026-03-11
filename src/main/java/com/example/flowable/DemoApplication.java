package com.example.flowable;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(RuntimeService runtimeService, TaskService taskService, HistoryService historyService) {
		return args -> {
			String processDefinitionKey = "simpleProcess";
			String businessKey = UUID.randomUUID().toString();
			String startUserId = "01012";
			Map<String, Object> variables = Map.of("startUserId", startUserId);
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
			System.out.println("process variables: " + processInstance.getProcessVariables());
			Task task = taskService.createTaskQuery().taskAssignee(startUserId).orderByTaskCreateTime().desc().list().getFirst();
			System.out.println("task assignee: " + task.getAssignee());
			taskService.complete(task.getId());
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
			System.out.println("historic process instance: " + historicProcessInstance.getProcessDefinitionName());
		};
	}
}
