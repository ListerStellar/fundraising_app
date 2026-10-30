package io.github.listerstellar.fundraising.repository;

import io.github.listerstellar.fundraising.dto.TopDonorDto;
import io.github.listerstellar.fundraising.entity.Donation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

    List<Donation> findAllByCustomerId(UUID customerId);

    List<Donation> findAllByFundraisingId(UUID fundraisingId);

    List<Donation> findAllByCustomerIdAndFundraisingId(UUID customerId, UUID fundraisingId);

    /**
     * Aggregates total donated amounts per donor customer for a given fundraising campaign,
     * groups by customer attributes, and orders donors in descending order by total donation amount.
     * Evaluated entirely at database level using JPQL.
     *
     * @param fundraisingId target fundraising campaign UUID
     * @param pageable pagination info used to restrict to Top-N donors
     * @return list of TopDonorDto projections containing customer info and aggregated total donation
     */
    @Query("""
            SELECT new io.github.listerstellar.fundraising.dto.TopDonorDto(
                d.customer.id,
                d.customer.username,
                d.customer.firstName,
                d.customer.lastName,
                SUM(d.amount)
            )
            FROM Donation d
            WHERE d.fundraising.id = :fundraisingId
            GROUP BY d.customer.id, d.customer.username, d.customer.firstName, d.customer.lastName
            ORDER BY SUM(d.amount) DESC
            """)
    List<TopDonorDto> findTopDonorsByFundraisingId(@Param("fundraisingId") UUID fundraisingId, Pageable pageable);
}
