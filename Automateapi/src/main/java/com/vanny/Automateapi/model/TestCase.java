package com.vanny.Automateapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class TestCase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    private String name;
    private String project;
    private String owner;
    private String status;

    // Constructors, Getters, Setters
    public TestCase() {}
    
    public TestCase(String name, String project, String owner, String status) {
        this.name = name;
        this.project = project;
        this.owner = owner;
        this.status = status;
    }

    // Getters and Setters here
}
