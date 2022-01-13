package uk.gov.dwp.jsa.bankdetails.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.dwp.jsa.adaptors.enums.UserType;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsAlreadyExistsException;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsByClaimIdNotFoundException;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsByIdNotFoundException;
import uk.gov.dwp.jsa.bankdetails.service.models.db.BankDetails;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsRequest;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsResponse;
import uk.gov.dwp.jsa.bankdetails.service.repositories.BankDetailsRepository;

import java.util.UUID;


@Service
public class BankDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankDetailsService.class);

    private final BankDetailsRepository repository;
    private final ObjectMapper mapper;

    @Autowired
    public BankDetailsService(final BankDetailsRepository repository, final ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public UUID save(final UUID claimId, final BankDetailsRequest bankDetailsRequest) {
        final BankDetails bankDetails = createBankDetailsEntityWith(claimId, bankDetailsRequest);
        BankDetails createdBankDetails;

        try {
            createdBankDetails = repository.save(bankDetails);
        } catch (DataIntegrityViolationException e) {
            LOGGER.error("Bank details already exist for claimId: {}", claimId, e);
            throw new BankDetailsAlreadyExistsException();
        }

        return createdBankDetails.getId();

    }

    public BankDetailsResponse getBankDetailsById(final UUID id) {
        return repository.findById(id)
                .map(BankDetailsResponse::new)
                .orElseThrow(BankDetailsByIdNotFoundException::new);
    }

    public BankDetailsResponse getBankDetailsByClaimId(final UUID bankDetailsId) {
        return repository.findByClaimId(bankDetailsId.toString())
                .map(BankDetailsResponse::new)
                .orElseThrow(BankDetailsByClaimIdNotFoundException::new);
    }

    public UUID update(final UUID id, final BankDetailsRequest bankDetailsRequest) {
        final BankDetails bankDetails = repository.findById(id).orElseThrow(BankDetailsByIdNotFoundException::new);
        updateBankDetailsEntityWith(bankDetails, bankDetailsRequest);

        try {
            repository.save(bankDetails);
        } catch (DataIntegrityViolationException e) {
            LOGGER.error("Bank details already exist for id: {}", id, e);
            throw new BankDetailsAlreadyExistsException();
        }

        return bankDetails.getId();

    }

    private void updateBankDetailsEntityWith(final BankDetails bankDetails,
                                             final BankDetailsRequest bankDetailsRequest) {
        bankDetails.setBankDetailsJson(bankDetailsRequest);
        bankDetails.setHash(DigestUtils.sha256Hex(createJsonFor(bankDetailsRequest)));
        bankDetails.setSource(UserType.AGENT.toString());
        bankDetails.setServiceVersion(bankDetailsRequest.getServiceVersion());
    }

    private BankDetails createBankDetailsEntityWith(final UUID claimId, final BankDetailsRequest bankDetailsRequest) {
        return new BankDetails(
                claimId.toString(),
                bankDetailsRequest,
                DigestUtils.sha256Hex(createJsonFor(bankDetailsRequest)),
                UserType.CITIZEN.toString(),
                bankDetailsRequest.getServiceVersion()
        );
    }

    private String createJsonFor(final BankDetailsRequest bankDetailsRequest) {
        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(bankDetailsRequest);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error creating JSON for claimantId: {}", bankDetailsRequest.getClaimantId(), e);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        return requestJson;
    }

    public void delete(final UUID id) {
        repository.deleteById(id);
    }
}
