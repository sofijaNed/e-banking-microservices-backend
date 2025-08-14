package fon.bank.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name="id")
    private Long id;


    @Column(name = "firstname", nullable = false)
    private String firstname;


    @Column(name = "lastname", nullable = false)
    private String lastname;


    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;


    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "username")
    private String userClient;

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
        Client client = (Client) o;
        return Objects.equals(getId(), client.getId()) && Objects.equals(getFirstname(), client.getFirstname()) && Objects.equals(getLastname(), client.getLastname()) && Objects.equals(getBirthdate(), client.getBirthdate()) && Objects.equals(getEmail(), client.getEmail()) && Objects.equals(getPhone(), client.getPhone()) && Objects.equals(getAddress(), client.getAddress()) && Objects.equals(getUserClient(), client.getUserClient());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstname(), getLastname(), getBirthdate(), getEmail(), getPhone(), getAddress(), getUserClient());
    }



}
