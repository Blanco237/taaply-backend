package com.taaply.todo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserPrincipal implements Principal {

    private UUID userId;
    private String email;

    @Override
    public String getName() {
        return email;
    }
}
