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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserImpl {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserImpl(ClientRepository clientRepository, EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    public List<ClientDTO> findAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> modelMapper.map(client, ClientDTO.class))
                .toList();
    }

    public ClientDTO findByClientUsername(String username) throws Exception {
        Client entity = clientRepository.findByUserClient(username);
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + username);
        }
        return modelMapper.map(entity, ClientDTO.class);
    }

    public List<EmployeeDTO> findAllEmployees() {
        return employeeRepository.findAll().stream().map(employee->modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    public EmployeeDTO findByEmployeeUsername(String username) throws Exception {
        return modelMapper.map(employeeRepository.findByUserEmployee(username), EmployeeDTO.class);
    }
}
