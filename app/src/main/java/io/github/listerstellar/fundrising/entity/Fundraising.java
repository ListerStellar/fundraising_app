package io.github.listerstellar.fundrising.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fundraising {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Name can't be blank")
    @Size(min = 1, max = 256, message = "Name must have between 1 and 256 characters")
    @Column(length = 256)
    private String name;

    @NotNull
    @Min(value = 10, message = "Goal can't be less than 10 EUR")
    @Column(nullable = false)
    private BigDecimal goal;

    @Size(max = 10000, message = "Description can't be longer than 10000 symbols")
    @Column(length = 10000)
    private String description;

    @NotNull(message = "Fundraising must have a creator")
    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(nullable = false)
    private Customer creator;

    @ManyToMany(mappedBy = "favouriteFundraisings", fetch = FetchType.LAZY)
    private Set<Customer> favouriteCustomers;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Fundraising that = (Fundraising) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Fundraising{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", goal=" + goal +
                '}';
    }
}
