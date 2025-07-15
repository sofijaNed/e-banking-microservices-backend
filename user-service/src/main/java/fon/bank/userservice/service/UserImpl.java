package fon.bank.userservice.service;

import fon.bank.userservice.dao.ClientRepository;
import fon.bank.userservice.dao.EmployeeRepository;
import fon.bank.userservice.dto.ClientDTO;
import fon.bank.userservice.dto.EmployeeDTO;
import fon.bank.userservice.dto.UserTokenInfoDTO;
import fon.bank.userservice.entity.Client;
import fon.bank.userservice.entity.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserImpl {

    private final RestTemplate restTemplate;

    private final ClientRepository clientRepository;

    private final EmployeeRepository employeeRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public UserImpl(RestTemplate restTemplate, ClientRepository clientRepository, EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.restTemplate = restTemplate;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    public Object getCurrentUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<UserTokenInfoDTO> response = restTemplate.exchange(
                "http://auth-service/auth/validate",
                HttpMethod.GET,
                request,
                UserTokenInfoDTO.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            UserTokenInfoDTO tokenInfo = response.getBody();
            String username = tokenInfo.getUsername();

            if (tokenInfo.getUserType().equals("ROLE_CLIENT")) {
                Client client = clientRepository.findByUserClient(username);
                return modelMapper.map(client, ClientDTO.class);
            } else {
                Employee emp = employeeRepository.findByUserEmployee(username);
                return modelMapper.map(emp, EmployeeDTO.class);
            }
        }

        return null;
    }
}
