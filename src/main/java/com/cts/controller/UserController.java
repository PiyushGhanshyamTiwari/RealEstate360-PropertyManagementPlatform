package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.entity.User;
import com.cts.repository.UserRepository;
import com.cts.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name= "User Controller", description="Operations related to all the users.")
public class UserController {
	private final UserRepository userRepository;
	private final UserService userService;
	
	@PostMapping("/register")
	@Operation(summary = "Add User using required information",
               description = "Returns added User info, if successfully added. ")
	public ResponseEntity<?> addUser(@RequestBody User user){
		user = userService.addUser(user);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}
	
	@GetMapping("/all")
	@Operation(summary = "Provide User/Owner list which have been registered",
               description = "This will display a list of all the registered Users.")
	public ResponseEntity<?> getAllUsers(){
		List<User> user= userService.getAllUsers();
		return new ResponseEntity<>(user, HttpStatus.OK);
		
	}
	
	@PostMapping("/login")
	@Operation(summary = "Responsible for user login",
               description = "This will display the required Data")
	public ResponseEntity<?> userLogin(@RequestBody LoginDTO loginDTO){
		LoginResponseDTO response = userService.userLogin(loginDTO);
		  if(response!=null)
		       return new ResponseEntity<>(response, HttpStatus.OK);
		   else
			   return new ResponseEntity<>("Login Failed", HttpStatus.BAD_REQUEST);
	}

}
