package fon.bank.authservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.io.Serializable;
import java.time.LocalDate;
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
    @Column(name="id")
    private Integer id;


    @Column(name = "firstname")
    private String firstname;


    @Column(name = "lastname")
    private String lastname;


    @Column(name = "birthdate")
    private LocalDate birthdate;


    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @JoinColumn(name="username",referencedColumnName = "username")
    @ManyToOne(optional = false)
    @JsonIgnore
    private User userClient;

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
