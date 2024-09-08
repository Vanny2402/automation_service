package com.vanny.Automateapi.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vanny.Automateapi.model.TestCase;

@Controller
@RequestMapping("/api/testcases")
public class TestCaseController {
	
    private List<TestCase> testCases = new ArrayList<>();

    public TestCaseController() {
        testCases.add(new TestCase("Wing to wing","App1", "John", "Pending"));
        testCases.add(new TestCase("Wing WeiLuy","App2", "Doe", "Completed"));

    }

    @GetMapping
    public List<TestCase> getTestCases() {
        return testCases;
    }

    @PostMapping
    public List<TestCase> addTestCase(@RequestBody TestCase newTestCase) {
        testCases.add(newTestCase);
        return testCases;
    }
}
