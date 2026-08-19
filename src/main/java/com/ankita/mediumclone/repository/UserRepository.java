package com.ankita.mediumclone.repository;
import java.util.Optional;
import com.ankita.mediumclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail ( String Email);
}
