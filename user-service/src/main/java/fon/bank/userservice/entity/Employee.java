package fon.bank.userservice.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstname", nullable = false)
    private String firstname;


    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "position")
    private String position;

    @Column(name = "username")
    private String userEmployee;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="updated_by")
    private String updatedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getId(), employee.getId()) && Objects.equals(getFirstname(), employee.getFirstname()) && Objects.equals(getLastname(), employee.getLastname()) && Objects.equals(getUserEmployee(), employee.getUserEmployee());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstname(), getLastname(), getUserEmployee());
    }
}
