package com.chatboot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS", schema = "ADMIN")
public class Usuario {
    @Id
    private String username;
    private String password;
    private String name;
    private boolean locked;
    private boolean disabled;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;
}
