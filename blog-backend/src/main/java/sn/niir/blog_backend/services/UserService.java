package sn.niir.blog_backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.niir.blog_backend.dto.UpdateRoleRequest;
import sn.niir.blog_backend.dto.UserResponse;
import sn.niir.blog_backend.exceptions.ResourceNotFoundException;
import sn.niir.blog_backend.models.User;
import sn.niir.blog_backend.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse getUserById(String id) {
        return UserResponse.fromEntity(findUserOrThrow(id));
    }

    public UserResponse updateRole(String id, UpdateRoleRequest request) {
        User user = findUserOrThrow(id);
        user.setRole(request.getRole());
        return UserResponse.fromEntity(userRepository.save(user));
    }

    public void deleteUser(String id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    private User findUserOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }
}