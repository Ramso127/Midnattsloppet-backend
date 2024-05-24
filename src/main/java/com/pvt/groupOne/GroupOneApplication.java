package com.pvt.groupOne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GroupOneApplication {

	public static void main(String[] args) {

		System.out.println("RUNNING PROGRAM");

		SpringApplication.run(GroupOneApplication.class, args);
	}

}
