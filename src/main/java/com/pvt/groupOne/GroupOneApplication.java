package com.pvt.groupOne;

import com.pvt.groupOne.Service.TokenService;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.PasswordTokenRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GroupOneApplication {

	public static void main(String[] args) {

		System.out.println("RUNNING PROGRAM");

		SpringApplication.run(GroupOneApplication.class, args);
//
//		 ApplicationContext context = SpringApplication.run(GroupOneApplication.class, args);
//		 TokenService tokenService = context.getBean(TokenService.class);
//		 AccountRepository accountRepository = context.getBean(AccountRepository.class);
//		 System.out.println(tokenService.validatePasswordResetToken("47c34ffd-9cfb-4475-8765-010c2301b828"));
//		 AccountRepository userRepository = context.getBean(AccountRepository.class);
//		 User user;
//		 PasswordTokenRepository passwordTokenRepository = context.getBean(PasswordTokenRepository.class);
//		 System.out.println(passwordTokenRepository.getIfDepleted("47c34ffd-9cfb-4475-8765-010c2301b828"));
//		 TokenService tokenService = context.getBean(TokenService.class);
//		 System.out.println(user = userRepository.findByUsername("noaTest6"));
//		 System.out.println(user.isVerified());
//		 VerificationTokenRepository verificationTokenRepository =
//		 context.getBean(VerificationTokenRepository.class);
//		 MainController mainController = context.getBean(MainController.class);
//		 System.out.println(user.getUserName());
//		 UserService userService = context.getBean(UserService.class);
//		 PasswordResetToken token = tokenService.createPasswordResetToken(user);
//		 System.out.println(token);
//		 EmailController emailController = context.getBean(EmailController.class);
//		 WebRouterController webRouterController =
//		 context.getBean(WebRouterController.class);
//		 System.out.println(webRouterController.serveVerifyEmail("d06e0314-94e8-493b-98b2-66229c24cd1e"));
//		 System.out.println(tokenService.validateVerificationMail("d06e0314-94e8-493b-98b2-66229c24cd1e"));
//		 System.out.println(tokenService.isVerifiedAlready(verificationTokenRepository.findUserByToken("d06e0314-94e8-493b-98b2-66229c24cd1e")));
//		 System.out.println(tokenService.createVerificationCode(user));

		// AOFÅJUBFÅU
	}

}

// Didde testkommentar

// test test test test

// TEST NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA NOA
// NOA NOA NOA NO

// hej
