package org.apisproject.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.apisproject.entity.UserAuthentication;
import org.apisproject.model.RequestPayload;
import org.apisproject.model.ResponseData;
import org.apisproject.model.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.apisproject.entity.User;
import org.apisproject.service.UserService;

import static org.apisproject.utilities.Crypto.generateSHA256;

@RestController
@RequestMapping(path = "/api/v1/user")
public class UserController {

    private final UserService userService;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping
    public ResponseEntity<ResponseObject> saveUser(@RequestBody RequestPayload payload) {

        User user = new User();
        user.setName(payload.getName() + " " + payload.getOtherName());
        user.setGender(payload.getGender());
        user.setAge(payload.getAge());
        user.setLocation(payload.getLocation());

        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setUsername(payload.getUsername());
        userAuth.setPassword(passwordEncoder.encode(payload.getPassword()));

        userAuth.setUser(user);
        user.setUserAuthentication(userAuth);

        User savedUser = userService.saveUser(user, userAuth);


        ResponseObject response = new ResponseObject();
        response.setMessage("success");
        response.setStatus(0);

        ResponseData rd = new ResponseData();
        rd.setName(savedUser.getName());
        rd.setAge(savedUser.getAge());
        rd.setLocation(savedUser.getLocation());

        response.setData(rd);

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<ResponseObject> getAllOrSearchUsers(@RequestParam(required = false, name = "name") String name,
                                                              @RequestParam(required = false, name="location") String location,
                                                              @RequestParam(required = false, name = "gender") String gender,
                                                              @RequestParam(required = false, defaultValue = "0") int page,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {
        ResponseObject response = new ResponseObject();
        List<User> searchResults;

        // Search users based on the query parameters
        if (name != null || location != null || gender != null) {
            searchResults = userService.searchUsersByNameAndLocationAndGender(name, location, gender, page, size);
        } else {
            searchResults = userService.fetchAllUsers(page, size);
        }


        response.setMessage("success");
        response.setStatus(0);
        response.setData(searchResults);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.fetchUserById(id);
        ResponseObject response = new ResponseObject();
        HttpStatus status = HttpStatus.OK;

        // Prepare response based on user presence
        if (user.isEmpty()) {
            response.setMessage("failed");
            response.setStatus(1);
            response.setData(new LinkedList<>());
        } else {
            response.setMessage("success");
            response.setStatus(0);
            response.setData(user.get());
        }

        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateUser(@PathVariable Long id,
                                                     @RequestBody RequestPayload userUpdatePayload ) {
        User user = new User();
        user.setName(userUpdatePayload.getOtherName() == null ? userUpdatePayload.getName() : userUpdatePayload.getName() + " " + userUpdatePayload.getOtherName());
        user.setGender(userUpdatePayload.getGender());
        user.setAge(userUpdatePayload.getAge());
        user.setLocation(userUpdatePayload.getLocation());

        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setUsername(userUpdatePayload.getUsername());
        userAuth.setPassword(generateSHA256(userUpdatePayload.getPassword()));  // Use generateSHA256 directly

        Optional<User> updatedUserOptional = userService.updateUser(id, user, userAuth);
        ResponseObject response = new ResponseObject();

        // Prepare response based on update status
        if (updatedUserOptional.isPresent()) {
            response.setMessage("successful");
            response.setStatus(0);
            response.setData(updatedUserOptional.get());
        } else {
            response.setMessage("failed");
            response.setStatus(1);
            response.setData(new LinkedList<>());
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        boolean deletionStatus = userService.deleteUser(id);
        ResponseObject response = new ResponseObject();

        if (deletionStatus) {
            response.setMessage("success");
            response.setStatus(0);
            response.setData(new LinkedList<>());
            return ResponseEntity.ok(response);
        } else {
            response.setMessage("failed");
            response.setStatus(1);
            response.setData(new LinkedList<>());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
