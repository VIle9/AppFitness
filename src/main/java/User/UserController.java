package User;

import com.fit.AppFitness.DTO.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO.UserProfileResponse> getProfile(){
        return ResponseEntity.ok(userService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO.UserProfileResponse> updateProfile(
            @RequestBody UserDTO.UpdateProfileRequest request){
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @PutMapping("/goals")
    public ResponseEntity<UserDTO.UserProfileResponse> updateGoals(
            @RequestBody UserDTO.UpdateProfileRequest request){
        return ResponseEntity.ok(userService.updateGoals(request));
    }

}
