package com.khorunaliyev.kettu.component;

import com.khorunaliyev.kettu.dto.request.user.UserUpdate;
import com.khorunaliyev.kettu.entity.auth.AppUser;
import org.springframework.stereotype.Component;

@Component
public class UserDiffChecker {

    public boolean isUserSame(AppUser appUser, UserUpdate userUpdate) {
        if(appUser.getBio()!=null && appUser.getBackgroundImage()==null){
            return false;
        }
        if (appUser.getName().trim().equals(userUpdate.getName().trim())) {
            return true;
        }
        if (appUser.getImage().trim().equals(userUpdate.getImage().trim())) {
            return true;
        }
        return appUser.getBackgroundImage().trim().equals(userUpdate.getBackgroundImage().trim());
    }
}
