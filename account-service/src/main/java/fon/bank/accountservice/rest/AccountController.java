package fon.bank.accountservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fon.bank.accountservice.dto.AccountDTO;
import fon.bank.accountservice.service.AccountImpl;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountImpl accountService;

    @Autowired
    public AccountController(AccountImpl accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<AccountDTO>> findByClientId(@PathVariable("id") int id) throws Exception {
        return ResponseEntity.ok().body(accountService.findByClientId(id));
    }

    //mozda nekad
//    @PostMapping
//    public ResponseEntity<AccountDTO> save( @RequestBody AccountDTO accountDTO) throws Exception {
//        return new ResponseEntity<>(accountService.save(accountDTO), HttpStatus.CREATED);
//    }


}
