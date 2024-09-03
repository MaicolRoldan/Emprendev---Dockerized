package com.emprendev.controller;

import com.emprendev.entity.User;
import com.emprendev.services.UserServices;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping(path = "emprendev/v1/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserServices userService;

    @GetMapping //Get all Users
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/listOrderAccount")
    public List<User> getAllAccountState(){
        return userService.getAllOrderByAccountState();
    }

    @GetMapping("/{id}") //Get user by id
    public Optional<User> getById(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/email/{email}") // Check if email exists
    public ResponseEntity<?> checkEmailExists(@PathVariable("email") String email) {
        boolean emailExists = userService.checkEmailExists(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", emailExists));
    }

    @PostMapping //Save user
    public void saveUser(@RequestBody User user) { // Corrige el nombre del método
        userService.saveOrUpdate(user);
    }

    @PostMapping("/login") //user login and session management
    public ResponseEntity<?> loginUser(@RequestBody @NotNull LoginRequest loginRequest, HttpSession session) {
        try {
            Optional<User> user = userService.validateUser(loginRequest.getEmail(), loginRequest.getPassword());
            if (user.isPresent() && user.get().getAccountState() == 1) {

                // Store only user ID in session
                session.setAttribute("userId", user.get().getId());

                Map<String, Object> response = new HashMap<>();
                response.put("userId", session.getAttribute("userId"));
                response.put("success", true);
                response.put("role", user.get().getRole());
                response.put("accountState", user.get().getAccountState());
                System.out.println(response);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(Collections.singletonMap("message", "Invalid credentials"));
            }
        } catch (Exception e) {
            // Implement more specific error handling based on exception type
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Internal server error"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Session closed successfully");
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessionStatus")
    public ResponseEntity<?> sessionStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        if (session.getAttribute("userId") != null) {
            Optional<User> user = userService.getUser(userId);
            if (user.isPresent()) {
                response.put("sessionActive", true);
                response.put("userId", user.get().getId());
                response.put("firstName", user.get().getFirstName());
                response.put("secondName", user.get().getSecondName());
                response.put("lastName", user.get().getLastName());
                response.put("lastName2", user.get().getLastName2());
                response.put("docType", user.get().getDocType());
                response.put("docNum", user.get().getDocNum());
                response.put("birthDate", user.get().getBirthDate());
                response.put("role", user.get().getRole());
                response.put("phoneNum", user.get().getPhoneNum());
                response.put("address", user.get().getAddress());
                response.put("Email", user.get().getEmail());
                response.put("password", user.get().getPassword());
                response.put("imgProfile", user.get().getImgProfile());
                response.put("accountState", user.get().getAccountState());
                response.put("creationDate", user.get().getCreationDate());

            } else {
                // Handle case where user might be removed from database
                session.invalidate();
                response.put("sessionActive", false);
                response.put("message", "Invalid session");
                logger.warn("Session invalidated due to missing user in the database");
            }
        } else {
            response.put("sessionActive", false);
            response.put("message", "No active session");
            logger.info("No active session found");
        }

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}") // Update user by id
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        Optional<User> optionalExistingUser = userService.getUser(id);

        if (optionalExistingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = optionalExistingUser.get();

        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getSecondName() != null) {
            existingUser.setSecondName(user.getSecondName());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        if (user.getLastName2() != null) {
            existingUser.setLastName2(user.getLastName2());
        }
        if (user.getDocType() != null) {
            existingUser.setDocType(user.getDocType());
        }
        if (user.getDocNum() != null) {
            existingUser.setDocNum(user.getDocNum());
        }
        if (user.getBirthDate() != null) {
            existingUser.setBirthDate(user.getBirthDate());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getPhoneNum() != null) {
            existingUser.setPhoneNum(user.getPhoneNum());
        }
        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }
        if (user.getImgProfile() != null) {
            existingUser.setImgProfile(user.getImgProfile());
        }
        if (user.getAccountState() != null) {
            existingUser.setAccountState(user.getAccountState());
        }
        if (user.getCreationDate() != null) {
            existingUser.setCreationDate(user.getCreationDate());
        }

        userService.saveOrUpdate(existingUser);
        return ResponseEntity.ok(existingUser);
    }

    @DeleteMapping("/{id}") //Delete user by id
    public void deleteById(@PathVariable("id") Long id) {
        userService.delete(id);
    }
}
