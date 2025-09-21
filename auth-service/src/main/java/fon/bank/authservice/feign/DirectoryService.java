package fon.bank.authservice.feign;

import fon.bank.authservice.entity.Role;
import fon.bank.authservice.entity.User;
import fon.bank.authservice.feign.ClientServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final ClientServiceClient userDir;

    public String resolveEmailForUser(User user) {
        String email;
        if (user.getRole() == Role.ROLE_CLIENT) {
            email = safeEmail(userDir.clientEmail(user.getUsername()).getEmail());
        } else {

            email = safeEmail(userDir.employeeEmail(user.getUsername()).getEmail());
        }
        if (!StringUtils.hasText(email)) {
            throw new UsernameNotFoundException("Email nije postavljen.");
        }
        return email.toLowerCase();
    }

    private String safeEmail(String s) {
        return s == null ? null : s.trim();
    }
}
