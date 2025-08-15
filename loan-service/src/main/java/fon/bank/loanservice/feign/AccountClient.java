package fon.bank.loanservice.feign;

import fon.bank.loanservice.dto.AccountDTO;
import fon.bank.loanservice.dto.TransferCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service", url = "http://account-service:8083")
public interface AccountClient {

    @GetMapping(value = "/accounts/byaccountnumber", params = "accountNumber")
    AccountDTO findByNumber(@RequestParam("accountNumber") String accountNumber);

    @GetMapping("/accounts/{id}")
    AccountDTO findById(@PathVariable("id") Long id);

    @PostMapping("/accounts/transfer")
    void transfer(@RequestBody TransferCommand cmd);

    @PostMapping("/accounts/transfer/my")
    void clientTransfer(@RequestBody TransferCommand cmd);
}
