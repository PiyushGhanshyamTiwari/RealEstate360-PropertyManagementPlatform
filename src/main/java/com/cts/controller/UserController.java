package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.entity.User;
import com.cts.repository.UserRepository;
import com.cts.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name= "User Controller", description="Operations related to all the users.")
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/register")
	@Operation(summary = "Add User using required information",
               description = "Returns added User info, if successfully added. ")
	public ResponseEntity<?> registerUser(@RequestBody RegistrationInputDTO registerInputDTO){
		RegistrationOutputDTO response = userService.registerUser(registerInputDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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

    @PutMapping("/{userId}")
    @Operation(summary = "Update User Details",
            description = "Allows user to update his/her information")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userId,
            @RequestBody RegistrationInputDTO registerInputDTO) {

        RegistrationOutputDTO response =
                userService.updateUser(userId, registerInputDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{userId}/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update User Status",
            description = "Allows updating status of user account")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Integer userId,
            @PathVariable String status) {
        userService.updateUserStatus(userId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
