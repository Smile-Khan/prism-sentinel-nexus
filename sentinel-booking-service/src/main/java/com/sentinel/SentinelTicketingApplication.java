package com.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.sentinel.repository")
public class SentinelTicketingApplication {

	public static void main(String[] args) {
		/*
		 * We force the JVM default timezone to UTC at the very start of the process.
		 * This prevents the PostgreSQL JDBC driver from detecting "Asia/Calcutta"
		 * from your Windows System, which the Postgres Docker container rejects.
		 */
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(SentinelTicketingApplication.class, args);
	}
}