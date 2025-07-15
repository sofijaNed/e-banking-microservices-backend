package fon.bank.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private AccountType type;

    @Column(name="balance")
    private Double balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "opened")
    private LocalDate opened;

    @Getter
    @Column(name="clientid")
    private Integer client;

}
