package fon.bank.userservice.rest;

import fon.bank.userservice.dto.ClientDTO;
import fon.bank.userservice.dto.EmployeeDTO;
import fon.bank.userservice.service.UserImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserImpl userImplementation;

    @GetMapping("/clients")
    public List<ClientDTO> findAllClients(){
        return userImplementation.findAllClients();
    }

    @GetMapping("/employees")
    public List<EmployeeDTO> findAllEmployees(){
        return userImplementation.findAllEmployees();
    }

    @GetMapping("/clients/{username}")
    public ResponseEntity<ClientDTO> findClientByUsername(@PathVariable String username) throws Exception {
        return ResponseEntity.ok().body(userImplementation.findByClientUsername(username));
    }

    @GetMapping("/employees/{username}")
    public ResponseEntity<EmployeeDTO> findEmployeesByUsername(@PathVariable String username) throws Exception {
        return ResponseEntity.ok().body(userImplementation.findByEmployeeUsername(username));
    }
}
