package fr.uge.revue.service;

import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.model.Role;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(Objects.requireNonNull(username))
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public boolean register(UserSignUpDTO user) {
        Objects.requireNonNull(user);
        var userAlreadyExist = userRepository.findByUsername(user.username())
                .or(() -> userRepository.findByEmail(user.email()))
                .isPresent();
        if (userAlreadyExist) {
            return false;
        }
        var encodedPassword = passwordEncoder.encode(user.password());
        var signUpUser = new User(user.username(), user.email(), encodedPassword, Role.USER);
        userRepository.save(signUpUser);
        return true;
    }

    public Optional<User> getUserById(long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(Objects.requireNonNull(email));
    }

    public void updateUserPassword(User user, String password) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
