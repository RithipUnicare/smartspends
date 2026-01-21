package com.smartspend.smartspends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartspendsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartspendsApplication.class, args);
	}

}
