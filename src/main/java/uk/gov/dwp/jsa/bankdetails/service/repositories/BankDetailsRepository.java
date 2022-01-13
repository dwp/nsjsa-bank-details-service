package uk.gov.dwp.jsa.bankdetails.service.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.dwp.jsa.bankdetails.service.models.db.BankDetails;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankDetailsRepository extends CrudRepository<BankDetails, UUID> {
    Optional<BankDetails> findByClaimId(final String claimId);
}
