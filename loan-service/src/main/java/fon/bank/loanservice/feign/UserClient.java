package fon.bank.loanservice.feign;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="user-service", url="http://user-service:8082")
public interface UserClient {
    @GetMapping("/users/clients/{username}")
    ClientSummary findClientByUsername(@PathVariable("username") String username);
    @Data class ClientSummary { Long id; String username; }
}