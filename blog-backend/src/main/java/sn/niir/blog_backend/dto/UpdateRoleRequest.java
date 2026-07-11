package sn.niir.blog_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sn.niir.blog_backend.models.Role;

@Getter
@Setter
public class UpdateRoleRequest {

    @NotNull(message = "Le rôle est requis")
    private Role role;
}