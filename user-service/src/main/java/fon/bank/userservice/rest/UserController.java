package fon.bank.userservice.rest;

import fon.bank.userservice.service.UserImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserImpl userImplementation;

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(@RequestHeader("Authorization") String token) {
        System.out.println("USAO SAM OVDE");
        Object user = userImplementation.getCurrentUser(token);
        return ResponseEntity.ok(user);
    }
}
