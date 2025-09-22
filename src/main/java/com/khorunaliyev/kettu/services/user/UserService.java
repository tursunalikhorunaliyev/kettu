package com.khorunaliyev.kettu.services.user;

import com.khorunaliyev.kettu.component.UserDiffChecker;
import com.khorunaliyev.kettu.config.security.JWTGenerator;
import com.khorunaliyev.kettu.dto.projection.PlaceInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.PlaceDTO;
import com.khorunaliyev.kettu.dto.reponse.place.PlacePhotoDTO;
import com.khorunaliyev.kettu.dto.request.user.UserUpdate;
import com.khorunaliyev.kettu.entity.auth.User;
import com.khorunaliyev.kettu.repository.auth.UserRepository;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final UserDiffChecker userDiffChecker;


    public ResponseEntity<Response> getMe(Authentication authentication){
        return ResponseEntity.ok(new Response("User data", userRepository.findDetachedByEmail(authentication.getName())));
    }

    public ResponseEntity<Response> update(Authentication authentication,UserUpdate userUpdate){
        final User user = userRepository.findById(userUpdate.getUserId()).orElseThrow(() -> new UsernameNotFoundException("ser not found"));
        final User userWithEmail = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("ser not found"));
        if(!user.getId().equals(userWithEmail.getId())){
            return new ResponseEntity<>(new Response("Failed", "Conflict with user id") ,HttpStatus.BAD_REQUEST);
        }
        if(userDiffChecker.isUserSame(user, userUpdate)) return new ResponseEntity<>(new Response("No difference", null), HttpStatus.NO_CONTENT);
        if(userUpdate.getName()!=null){
            user.setName(userUpdate.getName().trim());
        }
        if(userUpdate.getImage()!=null){
            user.setImage(userUpdate.getImage().trim());
        }
        if(userUpdate.getBackgroundImage()!=null){
            user.setBackgroundImage(userUpdate.getBackgroundImage().trim());
        }
        if(userUpdate.getBio()!=null){
            user.setBio(userUpdate.getBio());
        }
        userRepository.save(user);
        return ResponseEntity.ok(new Response("User updated", user));
    }

    public ResponseEntity<Response> getPlaces(Authentication authentication){
        final User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(new Response("User places",placeRepository.findByCreatedBy_Id(user.getId())));
    }


}
