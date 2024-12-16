package org.vdb.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vdb.entity.Contact;
import org.vdb.entity.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer> {

	// pagination...
	
	@Query("from Contact as c where c.user.id=:userid")
	// currentPage-page
	// contact per page=5 
	public Page<Contact> findContactByUser(@Param("userid")int userId,Pageable pageable);

	// search
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
