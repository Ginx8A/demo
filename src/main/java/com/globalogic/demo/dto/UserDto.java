package com.globalogic.demo.dto;

import com.globalogic.demo.entities.Phone;
import com.globalogic.demo.entities.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class UserDto implements Serializable {

    private String name;

    private String email;

    private String password;

    private transient List<Phone> phones;

    public User convertToUser() {
        User user = User.builder().build();
        BeanUtils.copyProperties(this, user);
        return user;
    }

}
