package sn.niir.blog_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sn.niir.blog_backend.models.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}