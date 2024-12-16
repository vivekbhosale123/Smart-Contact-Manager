package org.vdb.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.vdb.Helper.Message;
import org.vdb.dao.ContactRepository;
import org.vdb.dao.UserRepository;
import org.vdb.entity.Contact;
import org.vdb.entity.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactRepository contactRepository;

    // Method to add common data (User info)
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String Username = principal.getName();
        System.out.println("User name: " + Username);

        // Get the user using the username (Email)
        User userByUserName = userRepository.getUserByUserName(Username);
        System.out.println("User: " + userByUserName);

        model.addAttribute("user", userByUserName);
    }

    // Dashboard home page
    @GetMapping("/index")
    public String dashBoard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "user/user_dashboard";
    }

    // Display the form for adding a new contact (GET request)
    @GetMapping("/add_contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact Form");
        model.addAttribute("contact", new Contact());
        return "user/add_contact"; // Return the view for the contact form
    }

    // Process the form submission for adding a contact (POST request)
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
    		Principal principal,
    		HttpSession session
    		) {
    	
    	try {
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        
        contact.setUser(user);
        
        user.getContacts().add(contact);
        
        this.userRepository.save(user);
        
        contact.setImage("profile.png");
    	
    	System.out.println("DATA "+contact);
    	
    	System.out.println("Data added to database");
    	
    	// message success....
    	
    	session.setAttribute("message",new Message("Your contact is added !! add more....","success"));
    	
    	}catch (Exception e) {
    		
			System.out.println(e.getMessage());
			e.printStackTrace();
			// message error ....
	    	session.setAttribute("message",new Message("Something went wrong !!Try Again....","danger"));

		}
    	
        return "redirect:/user/add_contact"; // Redirect after form submission
    }

    // show contact handler
    // per page=5[n];
    // current page=0[page]
    @GetMapping("/show_contacts/{page}")
    public String showContact(@PathVariable("page")Integer page,Model model,Principal principal)
    {
    	model.addAttribute("title","View Contacts");
    	// contact list bhejani hai 
//    	     // this is ulternative Principal principal
//    	String userName = principal.getName();
//    	
//    	User user = this.userRepository.getUserByUserName(userName);
//    	
//    	List<Contact> contacts = user.getContacts();
    	
    	String userName = principal.getName();
    	
    	User user = this.userRepository.getUserByUserName(userName);
    	// currentPage-page
    	// contact per page=5 
    	Pageable pageable = PageRequest.of(page,5);
    	
    	Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);
    	
    	model.addAttribute("contacts",contacts);
    	model.addAttribute("currentPage",page);
    	model.addAttribute("totalPages", contacts.getTotalPages());
    	
    	
    	return "user/show_contacts";
    }
    
    // showing particular contact details
    @GetMapping("/{cid}/contact")
    public String showContactDetails(@PathVariable("cid")Integer cid,Model model,Principal principal)
    {
    	System.out.println("CID"+cid);
    	
    	Optional<Contact> contactOptional = this.contactRepository.findById(cid);
    	
    	Contact contact = contactOptional.get();
    	
    	// check security
    	String userName = principal.getName();
    	
    	User user = this.userRepository.getUserByUserName(userName);
    	
    	if(user.getId()==contact.getUser().getId())
    	{
    		model.addAttribute("contact",contact);
    		model.addAttribute("title",contact.getName());
    	}
    	
//    	model.addAttribute("contact",contact);
    	
    	return "user/contact_details";
    }
    
    // delete contact handler
    
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid")Integer cid,HttpSession session,
    		Principal principal)
    {
        Optional<Contact> contactOptional= this.contactRepository.findById(cid);
    	Contact contact = contactOptional.get();
    	
    	
    	
    	//remove
    	// image
    	//contact.getImage()
    	//check....
    	
    	User user = this.userRepository.getUserByUserName(principal.getName());
    	
    	user.getContacts().remove(contact);
    	
    	this.userRepository.save(user);
    	
    	System.out.println("deleted");
    	
    	session.setAttribute("message",new Message("Contact Deleted Successfully", "success"));
    	
    	return "redirect:/user/show_contacts/0";
    }
    
    //Open update form
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model model) {
        model.addAttribute("title", "Update contacts");

        Optional<Contact> contactOptional = this.contactRepository.findById(cid);
        if (contactOptional.isPresent()) {
            model.addAttribute("contact", contactOptional.get());
        }
        return "user/update_form";
    }
    
    // update contact handler
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact,
    		Model model,
    		Principal principal,
    		HttpSession session)
    {
    	try {
    		//old contact Details
    		
    		Contact oldContact=this.contactRepository.findById(contact.getCid()).get();
    		
    		User user=this.userRepository.getUserByUserName(principal.getName());
    		
    		contact.setUser(user);
    		
    		this.contactRepository.save(contact);
    		
    		session.setAttribute("message",new Message("Your contact is updated","success"));
    		
    	}
    	catch (Exception e) {
		   e.printStackTrace();
		}
    	return "redirect:/user/"+contact.getCid()+"/contact";
    }
    
    // Your profile
    @GetMapping("/profile")
    public String yourProfile(Model model,@ModelAttribute User user)
    {
    	model.addAttribute("title","Profile Page");
    	
    	user.setImageUrl("profile.png");
    	
    	return "user/profile";
    }
    

	// open setting handler
	@GetMapping("/settings")
	public String openSettings()
	{
		return "user/settings";
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpassword")String oldpassword,
			@RequestParam("newpassword")String newpassword,
			Principal principal,
			HttpSession session)
	{
		System.out.println("OLD PASSWORD"+ oldpassword);
		System.out.println("NEW PASSWORD"+ newpassword);
		
		String username = principal.getName();
		User CurrentUserName = this.userRepository.getUserByUserName(username);
		
		System.out.println(CurrentUserName.getPassword());
		
		if(bCryptPasswordEncoder.matches(oldpassword,CurrentUserName.getPassword()))
		{
			// change the password
			CurrentUserName.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
			this.userRepository.save(CurrentUserName);
    		session.setAttribute("message",new Message("Your Password is successfully Changed....","success"));

		}
		else
		{
			// error
    		session.setAttribute("message",new Message("Please Enter Your Correct old Password ","danger"));
    		return "redirect:/user/settings";
		}
		
		
		return "redirect:/user/index";
	}
}

























//@RequestParam("image") MultipartFile file,
//// Process and upload the file if provided
//if (!file.isEmpty()) {
//    // Get the filename and save it in the 'image' field
//    String fileName = file.getOriginalFilename();
//    contact.setImage(fileName); // Store the filename
//
//    // Directory where the image will be saved
//    File directory = new ClassPathResource("static/image").getFile();
//    Path path = Paths.get(directory.getAbsolutePath(), fileName);
//
//    // Save the file to the directory
//    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//    System.out.println("Image uploaded: " + fileName);
//}

