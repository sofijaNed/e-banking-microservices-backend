package fon.bank.transactionservice.feign;

import fon.bank.transactionservice.dto.AccountDTO;
import fon.bank.transactionservice.dto.TransferCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service", url = "http://account-service:8083")
public interface AccountClient {

    @GetMapping(value = "/accounts/byaccountnumber", params = "accountNumber")
    AccountDTO findByNumber(@RequestParam("accountNumber") String accountNumber);

    @PostMapping("/accounts/transfer")
    void transfer(@RequestBody TransferCommand cmd);

    @PostMapping("/accounts/transfer/my")
    void clientTransfer(@RequestBody TransferCommand cmd);
}
