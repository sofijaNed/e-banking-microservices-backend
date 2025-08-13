package fon.bank.authservice.security.twofactorauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
    private String preAuthToken;
    private String otpCode;
}