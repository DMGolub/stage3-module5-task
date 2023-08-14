package com.mjc.school;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication(scanBasePackages = {"com.mjc.school"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}