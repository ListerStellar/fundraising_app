package io.github.listerstellar.fundrising.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
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

    @Column(length = 256)
    private String name;

    @Column(nullable = false)
    private BigDecimal goal;

    @Column(length = 10000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(nullable = false)
    private Customer creator;

    @ManyToMany(mappedBy = "favouriteFundraisings", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Customer> favouredByCustomers = new HashSet<>();

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
