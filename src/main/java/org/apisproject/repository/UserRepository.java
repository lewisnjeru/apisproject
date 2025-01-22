
package org.apisproject.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.apisproject.entity.User;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        @Query("SELECT p FROM User p WHERE " +
                "(:name IS NULL OR p.name LIKE %:name%) AND " +
                "(:location IS NULL OR p.location LIKE %:location%) AND " +
                "(:gender IS NULL OR p.gender LIKE %:gender%)")
        List<User> findByNameContainingAndLocationContainingAndGenderContaining(@Param("name") String name,
                                      @Param("location") String location,
                                      @Param("gender") String gender,
                                      Pageable pageable);



@Query("SELECT u FROM User u JOIN u.userAuthentication ua WHERE ua.username = :username")
org.springframework.security.core.userdetails.User findByUsername(@Param("username") String username);
}



