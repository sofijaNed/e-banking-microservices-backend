package fon.bank.authservice.feign;

import fon.bank.authservice.dto.ClientLookupRequestDTO;
import fon.bank.authservice.dto.ClientLookupResponseDTO;
import fon.bank.authservice.dto.EmailDTO;
import fon.bank.authservice.dto.LinkUserRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="user-service", url="http://user-service:8082", configuration = FeignConfig.class)
public interface ClientServiceClient {

    @GetMapping("/clients/lookup")
    ClientLookupResponseDTO lookup(@SpringQueryMap ClientLookupRequestDTO req);

    @PostMapping("/clients/{id}/link-user")
    void linkUser(@PathVariable("id") Long clientId, @RequestBody LinkUserRequestDTO body);

    @GetMapping("/internal/users/clients/{username}")
    EmailDTO clientEmail(@PathVariable("username") String username);

    @GetMapping("/internal/users/employees/{username}")
    EmailDTO employeeEmail(@PathVariable("username") String username);
}
