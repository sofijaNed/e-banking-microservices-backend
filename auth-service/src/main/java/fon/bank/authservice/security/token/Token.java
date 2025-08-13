package fon.bank.authservice.security.token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import fon.bank.authservice.entity.User;

import java.io.Serializable;

@Entity
@Table(name="token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;


    @Column(name="token",unique = true)
    public String token;


    @Enumerated(EnumType.STRING)
    @Column(name="token_type")
    public TokenType tokenType = TokenType.BEARER;


    public boolean revoked;


    public boolean expired;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="username",referencedColumnName = "username")
    public User user;
}
