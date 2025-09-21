package fon.bank.accountservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-service",
        path = "/users",
        configuration = FeignAuthConfig.class
)
public interface UserClient {

    @GetMapping("/clients/{username}")
    ClientSummary findClientByUsername(@PathVariable String username);


}
