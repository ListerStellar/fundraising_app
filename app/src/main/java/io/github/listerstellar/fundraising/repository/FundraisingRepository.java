package io.github.listerstellar.fundraising.repository;

import io.github.listerstellar.fundraising.entity.Fundraising;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FundraisingRepository extends JpaRepository<Fundraising, UUID> {
}
