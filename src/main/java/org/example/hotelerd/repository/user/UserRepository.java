package org.example.hotelerd.repository.user;

import java.util.Optional;
import org.example.hotelerd.repository.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findById(Integer id);
}
