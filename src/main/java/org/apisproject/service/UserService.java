package org.apisproject.service;

import java.util.List;
import java.util.Optional;

import org.apisproject.entity.UserAuthentication;
import org.apisproject.repository.UserAuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.apisproject.entity.User;
import org.apisproject.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
   private final UserAuthenticationRepository userAuthenticationRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserAuthenticationRepository userAuthenticationRepository) {
        this.userRepository = userRepository;
        this.userAuthenticationRepository = userAuthenticationRepository;

    }


    public User saveUser(User user, UserAuthentication userAuth) {
        try {

            User savedUser = userRepository.save(user);

            // Link and save user authentication
            userAuth.setUser(savedUser);
            userAuthenticationRepository.save(userAuth);

            return savedUser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user: " + e.getMessage());
        }
    }


    // Get all users with pagination
    public List<User> fetchAllUsers(int page, int size) {
        try {
            Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
            return userPage.getContent();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all users: " + e.getMessage());
        }
    }

    // Get a user by ID
    public Optional<User> fetchUserById(Long id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user by ID: " + e.getMessage());
        }
    }

    // Search users by name, location, and gender with pagination
    public List<User> searchUsersByNameAndLocationAndGender(String name, String location, String gender, int page, int size) {
        return userRepository.findByNameContainingAndLocationContainingAndGenderContaining(name, location, gender, PageRequest.of(page, size));
    }

    // Update a user and its authentication
    public Optional<User> updateUser(Long id, User updatedUser, UserAuthentication updatedAuth) {
        try {
            Optional<User> existingUserOptional = userRepository.findById(id);

            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();


                existingUser.setName(updatedUser.getName());
                existingUser.setAge(updatedUser.getAge());
                existingUser.setGender(updatedUser.getGender());
                existingUser.setLocation(updatedUser.getLocation());

                Optional<UserAuthentication> existingAuthOptional = userAuthenticationRepository.findByUserId(id);

                if (existingAuthOptional.isPresent()) {
                    UserAuthentication existingAuth = existingAuthOptional.get();

                    // Update authentication details
                    existingAuth.setUsername(updatedAuth.getUsername());
                    existingAuth.setPassword(updatedAuth.getPassword());

                    userAuthenticationRepository.save(existingAuth);
                }

                // Save updated user details
                User savedEntity = userRepository.save(existingUser);
                return Optional.of(savedEntity);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user and authentication: " + e.getMessage());
        }
    }


    public boolean deleteUser(Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }
}
