package fon.bank.userservice.rest;

import fon.bank.userservice.dao.ClientRepository;
import fon.bank.userservice.dao.EmployeeRepository;
import fon.bank.userservice.dto.ClientLookupRequestDTO;
import fon.bank.userservice.dto.ClientLookupResponseDTO;
import fon.bank.userservice.dto.LinkUserRequestDTO;
import fon.bank.userservice.entity.Client;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientRegistrationController {

    private final ClientRepository repo;
    private final EmployeeRepository employeeRepo;

    @GetMapping("/lookup")
    @PreAuthorize("permitAll()")
    public ClientLookupResponseDTO lookup(@Valid ClientLookupRequestDTO q) {
        Client c = repo.findByJmbg(q.getJmbg())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Klijent ne postoji"));

        if (!c.getFirstname().equalsIgnoreCase(q.getFirstName())
                || !c.getLastname().equalsIgnoreCase(q.getLastName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ime/prezime se ne poklapaju");
        }
        if (q.getIdCardNo()!=null && c.getIdCardNo()!=null
                && !c.getIdCardNo().equalsIgnoreCase(q.getIdCardNo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lična karta se ne poklapa");
        }

        return new ClientLookupResponseDTO(c.getId(), c.getEmail(), c.getUserClient());
    }

    @PostMapping("/{id}/link-user")
    @PreAuthorize("hasAuthority('SVC_AUTH')")
//    @PreAuthorize("hasAuthority('SVC_AUTH')") // samo auth-service
    public ResponseEntity<Void> linkUser(@PathVariable Long id, @Valid @RequestBody LinkUserRequestDTO body) {
        Client c = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Klijent ne postoji"));
        if (c.getUserClient()!=null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Klijent već ima nalog");
        }
        c.setUserClient(body.getUsername());
        repo.save(c);
        return ResponseEntity.noContent().build();
    }

}
