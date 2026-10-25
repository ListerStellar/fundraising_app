package io.github.listerstellar.fundrising.repository;

import io.github.listerstellar.fundrising.entity.Fundraising;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FundraisingRepository extends JpaRepository<Fundraising, UUID> {
}
