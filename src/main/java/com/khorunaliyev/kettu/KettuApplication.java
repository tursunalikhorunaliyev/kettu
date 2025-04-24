package com.khorunaliyev.kettu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
public class KettuApplication {

	public static void main(String[] args) {
		SpringApplication.run(KettuApplication.class, args);
	}

}
