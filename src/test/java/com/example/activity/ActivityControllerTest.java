package com.example.activity;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class ActivityControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(ActivityControllerTest.class);
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    RuntimeService runtimeService;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .build();

        for (ProcessInstance instance : runtimeService.createProcessInstanceQuery()
                .list()) {
            runtimeService.deleteProcessInstance(instance.getId(), "Reset Processes");
        }
    }

    @Test
    public void givenProcess_whenStartProcess_thenIncreaseInProcessInstanceCount() throws Exception {

        String responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals("Process started. Number of currently running process instances = 1", responseBody);

        responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals("Process started. Number of currently running process instances = 2", responseBody);

        responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals("Process started. Number of currently running process instances = 3", responseBody);
    }

    @Test
    public void givenProcess_whenCompleteTaskA_thenReceivedNextTask() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
                .andReturn()
                .getResponse();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .orderByProcessInstanceId()
                .desc()
                .list()
                .get(0);

        logger.info("process instance = " + pi.getId());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/complete-task-A/" + pi.getId()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        assertEquals(0, list.size());
    }

}
