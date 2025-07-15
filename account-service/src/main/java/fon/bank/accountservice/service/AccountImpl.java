package fon.bank.accountservice.service;

import fon.bank.accountservice.dao.AccountRepository;
import fon.bank.accountservice.dto.AccountDTO;
import fon.bank.accountservice.dto.TransactionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
public class AccountImpl{

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;

    @Autowired
    public AccountImpl(AccountRepository accountRepository, ModelMapper modelMapper, RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
    }

    public List<AccountDTO> findByClientId(Integer clientId){
//        List<Account> accounts = accountRepository.findAccountsByClient(clientId);
//        List<AccountDTO> accountDTOS = new ArrayList<>();
//
//        List<Transaction> sentTransactions = new ArrayList<>();
//        List<Transaction> receivedTransactions = new ArrayList<>();
//
//        for(Account account:accounts){
//            accountDTOS.add(modelMapper.map(account, AccountDTO.class));
            //ovde pozovi transaction servis i pokupi sve transakcije acconta(i sender i receiver). Necu da brisem ovde
            //transaction repository, da bi videla kako treba da se vraca
//        }
//
//        return accountDTOS;

        String token = extractTokenFromRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("X-Gateway-Auth", "valid");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        List<AccountDTO> accountDTOS =  accountRepository.findAll().stream()
                .filter(account -> account.getClient().equals(clientId))
                .map(account->modelMapper.map(account, AccountDTO.class))
                .toList();
        for(AccountDTO accountDTO : accountDTOS){
            String accountId = accountDTO.getId();

            ResponseEntity<List<TransactionDTO>> senderResponse =
                    restTemplate.exchange(
                            "http://transaction-service/transactions/sender/" + accountId,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<>() {}
                    );
            accountDTO.setSentTransactions(senderResponse.getBody());

            ResponseEntity<List<TransactionDTO>> receiverResponse =
                    restTemplate.exchange(
                            "http://transaction-service/transactions/receiver/" + accountId,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<>() {}
                    );
            accountDTO.setReceivedTransactions(receiverResponse.getBody());

        }
        return accountDTOS;
    }

    public String extractTokenFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

//    @Override
//    public List<AccountDTO> findAll() {
//        return accountRepository.findAll().stream().map(account->modelMapper.map(account, AccountDTO.class))
//                .collect(Collectors.toList());
//    }

    //potencijalno ce ti trebati save ili update accounta, ali polako


}
