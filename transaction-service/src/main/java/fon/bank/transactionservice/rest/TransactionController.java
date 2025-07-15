package fon.bank.transactionservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fon.bank.transactionservice.dto.TransactionDTO;
import fon.bank.transactionservice.entity.TransactionPK;
import fon.bank.transactionservice.service.TransactionImpl;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionImpl transactionService;

    @Autowired
    public TransactionController(TransactionImpl transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> findById(@PathVariable("id") TransactionPK id) throws Exception {
        return ResponseEntity.ok().body(transactionService.findById(id));
    }

    @GetMapping("/sender/{id}")
    public ResponseEntity<List<TransactionDTO>> findBySenderId(@PathVariable("id") String id) throws Exception {
        return ResponseEntity.ok().body(transactionService.findBySenderId(id));
    }

    @GetMapping("/receiver/{id}")
    public ResponseEntity<List<TransactionDTO>> findByReceiverId(@PathVariable("id") String id) throws Exception {
        return ResponseEntity.ok().body(transactionService.findByReceiverId(id));
    }

    @PostMapping("/savePliz")
    public ResponseEntity<TransactionDTO> save( @RequestBody TransactionDTO transactionDTO) throws Exception {
        System.out.println("Ovde sam");
        return new ResponseEntity<>(transactionService.save(transactionDTO), HttpStatus.CREATED);
    }
}
