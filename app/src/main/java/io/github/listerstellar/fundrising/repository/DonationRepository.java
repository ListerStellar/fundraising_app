package io.github.listerstellar.fundrising.repository;

import io.github.listerstellar.fundrising.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DonationRepository extends JpaRepository<Donation, UUID> {
    List<Donation> findAllByCustomerId(UUID customerId);

    List<Donation> findAllByFundraisingId(UUID fundraisingId);
}

