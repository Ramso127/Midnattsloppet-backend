package com.pvt.groupOne;

import com.pvt.groupOne.Service.TokenService;
import com.pvt.groupOne.Service.UserService;
import com.pvt.groupOne.controller.MainController;
import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GroupOneApplication {

	public static void main(String[] args) {
		System.out.println("test");
		System.out.println("wille");
		System.out.println("tjalalla");

		SpringApplication.run(GroupOneApplication.class, args);


//		 ApplicationContext context = SpringApplication.run(GroupOneApplication.class,
//		 args);
//		 AccountRepository userRepository = context.getBean(AccountRepository.class);
//		 User user;
//		 TokenService tokenService = context.getBean(TokenService.class);
//		 System.out.println(user = userRepository.findByUsername("noaTest6"));
//		 MainController mainController = context.getBean(MainController.class);
//		 System.out.println(user.getUserName());
//		 UserService userService = context.getBean(UserService.class);
//		 PasswordResetToken token = tokenService.createPasswordResetToken(user);
//		 System.out.println(token);
		// AOFÅJUBFÅU
	}

}

// Didde testkommentar

// test test test test

// TEST NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA
// NOA NOA NOA NO

// hej
