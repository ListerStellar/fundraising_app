package io.github.listerstellar.fundrising.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Username can't be blank")
    @Size(max = 100, message = "Username can have maximum 100 symbols")
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @NotNull(message = "Email can't be blank")
    @Size(max = 254, message = "Email can have maximum 254 symbols")
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Size(min = 1, max = 100, message = "First name can have minimum 1 and maximum 100 symbols")
    @Column(length = 100)
    private String firstName;

    @Size(min = 1, max = 100, message = "Second name can have minimum 1 and maximum 100 symbols")
    @Column(length = 100)
    private String lastName;

    @Size(min = 3, max = 20, message = "Phone number can have minimum 3 and maximum 20 digits")
    @Column(length = 20)
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "favourite_user_fundraising",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "fundraising_id")
    )
    private Set<Fundraising> favouriteFundraisings;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
