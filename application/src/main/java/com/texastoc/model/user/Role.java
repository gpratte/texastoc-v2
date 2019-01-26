package com.texastoc.model.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Role {
    private long id;
    private String name;
    private String description;

}
