package com.khorunaliyev.kettu.component;

import com.khorunaliyev.kettu.dto.request.user.UserUpdate;
import com.khorunaliyev.kettu.entity.auth.User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserDiffChecker {

    public boolean isUserSame(User user, UserUpdate userUpdate) {
        if(user.getBio()!=null && user.getBackgroundImage()==null){
            return false;
        }
        if (user.getName().trim().equals(userUpdate.getName().trim())) {
            return true;
        }
        if (user.getImage().trim().equals(userUpdate.getImage().trim())) {
            return true;
        }
        return user.getBackgroundImage().trim().equals(userUpdate.getBackgroundImage().trim());
    }
}
