package org.vdb.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.vdb.dao.ContactRepository;
import org.vdb.dao.UserRepository;
import org.vdb.entity.Contact;
import org.vdb.entity.User;

@RestController
public class SearchController {

  
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	// search handler
	@GetMapping("/search/{quary}")
	public ResponseEntity<?> search(@PathVariable("quary")String quary,Principal p)
	{
		System.out.println(quary);
		
		String name = p.getName();
		
		User user= this.userRepository.getUserByUserName(name);
		
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(quary,user);
		
		return ResponseEntity.ok(contacts);
	}
}
