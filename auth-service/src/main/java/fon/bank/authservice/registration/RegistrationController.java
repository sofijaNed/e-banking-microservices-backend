package fon.bank.authservice.registration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService service;

    @PostMapping("/request")
    public RegistrationTicketDTO request(@Valid @RequestBody RegistrationRequestDTO dto) {
        return service.request(dto);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody RegistrationVerifyDTO dto) {
        service.verify(dto);
        return ResponseEntity.noContent().build();
    }
}
