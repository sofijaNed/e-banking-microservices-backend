package fon.bank.userservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import fon.bank.userservice.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByUserEmployee(String username);
}
