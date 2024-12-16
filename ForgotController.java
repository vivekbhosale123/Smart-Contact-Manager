package org.vdb.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.vdb.dao.UserRepository;
import org.vdb.entity.User;
import org.vdb.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	Random random = new Random(1000);

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// Email id form Hanlder
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	
	// email handler
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email")String email,HttpSession session)
	{
		System.out.println("EMAIL is "+email);
		
		//generating otp of 4 digit
		
		
		int otp= random.nextInt(999999);
		
		System.out.println(otp);
		
		// write code for send otp to email
		String subject="OTP from SCM";
		String message=""
				+ "<div style='border:2px solid black; padding:20px'>"
				+ "<h1>"
				+ "OTP is"
				+ "<b>"+otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		String to=email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag)
		{
			session.setAttribute("otp",otp);
			session.setAttribute("email",email);
			return "verify_otp";
		}
		else
		{
			session.setAttribute("message","Check your email Id !!");
			
			return "forgot_email_form";
		}
	}
	
	// verify-otp handler
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session)
	{
		int myotp=(int)session.getAttribute("otp");
		String email=(String)session.getAttribute("email");
		
		if(myotp==otp)
		{
			// password change form
			
			User user = this.userRepository.getUserByUserName(email);
			
			if(user==null)
			{
				// send error message
				session.setAttribute("message","user does not exist with this email !!");
				
				return "forgot_email_form";
			}
			else
			{
				// send change password form
				
			}
			
			return "password_change_form";
		}
		else
		{
			session.setAttribute("message","you have entered wrong otp");
			return "verify_otp";
		}
	}
	
	// change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword")String newpassword,HttpSession session)
	{
		String email=(String)session.getAttribute("email");
        User user = this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
        this.userRepository.save(user);
        
        return "redirect:/signin?change=password changed successfully...";
	}
}
