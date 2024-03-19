package fr.uge.revue.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class ResetPasswordToken {
    @Id
    @GeneratedValue
    private long id;
    private String token;
    private LocalDateTime expirationDate;

    public ResetPasswordToken() {
    }

    public ResetPasswordToken(String token, LocalDateTime expirationDate) {
        this.token = Objects.requireNonNull(token);
        this.expirationDate = Objects.requireNonNull(expirationDate);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = Objects.requireNonNull(token);
    }



    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = Objects.requireNonNull(expirationDate);
    }

    @Override
    public String toString() {
        return "ResetPasswordToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }

    public boolean isExpired() {
        return expirationDate.isBefore(LocalDateTime.now());
    }
}
