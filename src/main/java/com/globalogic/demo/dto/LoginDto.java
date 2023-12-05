package com.globalogic.demo.dto;

import com.globalogic.demo.entities.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class LoginDto implements Serializable{

    private UUID id;

    private String created;

    private String lastLogin;

    private String token;

    private boolean isActive;

    private String name;

    private String email;

    private String password;

    private List<PhoneDto> phones;

    public void invert(User user) {
        BeanUtils.copyProperties(user, this);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a");
        setCreated(user.getCreated().format(formatter));
        setLastLogin(user.getLastLogin().format(formatter));
    }

}
