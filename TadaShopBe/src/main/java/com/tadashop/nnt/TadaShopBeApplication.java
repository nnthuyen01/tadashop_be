package com.tadashop.nnt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.tadashop.nnt.dto.UserReq;

import com.tadashop.nnt.service.AuthenticationService;
import com.tadashop.nnt.utils.constant.Role;

@SpringBootApplication
public class TadaShopBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TadaShopBeApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(AuthenticationService service) {
		return args -> {
			if (!service.adminExists()) {
				var admin = UserReq.builder().firstname("Admin").lastname("Admin").email("admin@mail.com")
						.phone("0902235692").username("admin").password("password").role(Role.ADMIN)

						.build();
				service.saveAdmin(admin);
			}

		};
	}
}
