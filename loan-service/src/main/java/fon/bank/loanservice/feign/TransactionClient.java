package fon.bank.loanservice.feign;

import fon.bank.loanservice.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "transaction-service",
        url = "http://transaction-service:8085",
        configuration = FeignAuthRelayConfig.class
)
public interface TransactionClient {
    @PostMapping(value = "/transactions/_log", consumes = "application/json")
    void log(@RequestBody TransactionDTO req);
}
