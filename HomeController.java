package org.vdb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.vdb.Helper.Message;
import org.vdb.dao.UserRepository;
import org.vdb.entity.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
    @Autowired
    private UserRepository userRepository;

    // home Handler
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home: smart contact manager");
        return "home";
    }

    // about handler
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About: smart contact manager");
        return "about";
    }

    // signup handler
    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("title", "Signup: smart contact manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    // registering handler for user
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult results,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model,HttpSession session) {
        try {
            if (!agreement) {
            	System.out.println("You have not agreed to the terms and conditions.");
                throw new Exception("You have not agreed to the terms and conditions.");
            }
            
            if(results.hasErrors())
            {
                System.out.println("Error"+results.toString());
            	model.addAttribute("user",user);
            	return "signup";
            }
            
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            User result = this.userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully registered!", "alert-success"));
            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
            return "signup";
        }
    }
   
    // handler for custom login
    @GetMapping("/signin")
    public String customLogin(Model model)
    {
    	model.addAttribute("title", "Login Page");
    	return "login";
    }
}
