package com.thotran.sochitieu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Enable scheduled tasks
public class SoChiTieuApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoChiTieuApplication.class, args);
	}

}

