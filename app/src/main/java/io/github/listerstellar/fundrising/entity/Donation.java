package io.github.listerstellar.fundrising.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @Min(value = 1, message = "Amount must be >= 1 EUR")
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime donationDate;

    @Size(max = 1000, message = "Description can have maximum 10000 symbols")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Donation must have a donor")
    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(nullable = false)
    private User donor;

    @NotNull(message = "Donation must belong to the fundraising")
    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(nullable = false)
    private Fundraising fundraising;

    @PrePersist
    protected void onCreate() {
        donationDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return Objects.equals(getId(), donation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Donation{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
    }
}
