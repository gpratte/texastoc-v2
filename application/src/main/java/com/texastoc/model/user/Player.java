package com.texastoc.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    public String getName() {
        String name = null;

        if (firstName != null) {
            name = firstName;
            if (lastName != null) {
                name += " " + lastName;
            }
        } else if (lastName != null) {
            name = lastName;
        }

        return name == null ? "Unknown" : name;
    }
}
