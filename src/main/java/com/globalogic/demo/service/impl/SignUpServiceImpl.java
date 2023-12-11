package com.globalogic.demo.service.impl;

import com.globalogic.demo.config.exception.ValidatorException;
import com.globalogic.demo.dto.SingUpDto;
import com.globalogic.demo.dto.UserDto;
import com.globalogic.demo.entities.ErrorDetail;
import com.globalogic.demo.entities.User;
import com.globalogic.demo.repository.UserRepository;
import com.globalogic.demo.service.SignUpService;
import com.globalogic.demo.util.Constant;
import com.globalogic.demo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final UserRepository repository;

    private final JwtUtils jwtUtils;

    @Override
    public SingUpDto save(UserDto dto) {
        validatorUser(dto);
        User user = chargeUser(dto);
        User newUser = repository.save(user);
        SingUpDto signUpDto = SingUpDto.builder().build();
        signUpDto.invert(newUser);
        String token = jwtUtils.createToken(newUser.getEmail());
        signUpDto.setToken(token);
        return signUpDto;
    }

    private User chargeUser(UserDto dto) {
        UUID uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        User user = dto.convertToUser();
        user.setId(uuid);
        user.setActive(Boolean.TRUE);
        user.setCreated(now);
        user.setLastLogin(now);
        user.setPassword(encodePwd(dto.getPassword()));
        if (!CollectionUtils.isEmpty(user.getPhones())) {
            user.getPhones().forEach(phone -> {
                phone.setId(UUID.randomUUID());
                phone.setUser(user);
            });
        }
        return user;
    }

    private void validatorUser(UserDto dto) {
        User user = repository.findByEmail(dto.getEmail()).orElse(null);
        if (null != user) {
            throw new EntityExistsException(Constant.USER_EXIST);
        }
    }

    private String encodePwd(String string) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(Constant.STRENGTH);
        return passwordEncoder.encode(string);
    }

    @Override
    public void validatorSignUp(UserDto dto) throws ValidatorException {
        List<ErrorDetail> listError = new ArrayList<>();
        validateGeneric(dto.getEmail(), Constant.MAIL, listError);
        validateGeneric(dto.getPassword(), Constant.PASSWORD, listError);
        if (StringUtils.isNotBlank(dto.getPassword())
                && (dto.getPassword().length() < Constant.MIN_LENGTH_PASSWORD
                || dto.getPassword().length() > Constant.MAX_LENGTH_PASSWORD)) {
            listError.add(createErrorDetail(Constant.PASSWORD_LENGTH));
        }
        if (!CollectionUtils.isEmpty(listError)) {
            throw new ValidatorException(listError);
        }
    }

    private static void validateGeneric(String value, String type, List<ErrorDetail> listError) {
        if (StringUtils.isNotBlank(value)) {
            switch (type) {
                case Constant.PASSWORD:
                    if (notMatchesRegex(Constant.REGEX_PASSWORD_MAYUSC, value, 1)
                            || notMatchesRegex(Constant.REGEX_PASSWORD_NUM, value, 2)) {
                        listError.add(createErrorDetail(type.concat(Constant.REGEX)));
                    }
                    break;
                case Constant.MAIL:
                    if (!Pattern.matches(Constant.REGEX_MAIL, value)) {
                        listError.add(createErrorDetail(type.concat(Constant.REGEX)));
                    }
                    break;
                default:
                    break;
            }
        } else {
            listError.add(createErrorDetail(type));
        }
    }

    private static boolean notMatchesRegex(String regex, String value, int sizeValue) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);

        int count = 0;
        while (matcher.find()) {
            count++;
            if (count > sizeValue) {
                return true;
            }
        }
        return false;
    }

    private static ErrorDetail createErrorDetail(String tipError) {
        String detail = "";
        switch (tipError) {
            case Constant.MAIL:
                detail = Constant.MSG_REQUIRED_MAIL;
                break;
            case Constant.MAIL_REGEX:
                detail = Constant.MSG_REGEX_MAIL;
                break;
            case Constant.PASSWORD:
                detail = Constant.MSG_REQUIRED_PASSWORD;
                break;
            case Constant.PASSWORD_REGEX:
                detail = Constant.MSG_REGEX_PASSWORD;
                break;
            case Constant.PASSWORD_LENGTH:
                detail = Constant.MSG_REQUIRED_LENGTH;
                break;
            default:
                break;
        }
        return ErrorDetail.builder().detail(detail).build();
    }

}
