package uk.gov.dwp.jsa.bankdetails.service.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.bankdetails.service.config.WithVersionUriComponentsBuilder;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsRequest;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsResponse;
import uk.gov.dwp.jsa.bankdetails.service.services.BankDetailsService;
import uk.gov.dwp.jsa.bankdetails.service.services.ResponseBuilder;
import uk.gov.dwp.jsa.security.roles.AnyRole;
import uk.gov.dwp.jsa.security.roles.WC;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.UUID;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromController;
import static uk.gov.dwp.jsa.bankdetails.service.config.WithVersionUriComponentsBuilder.VERSION_SPEL;

@RestController
@RequestMapping("/nsjsa/" + VERSION_SPEL)
public class BankDetailsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankDetailsController.class);

    private final BankDetailsService bankDetailsService;
    private final WithVersionUriComponentsBuilder uriBuilder;

    @Autowired
    public BankDetailsController(
            final BankDetailsService bankDetailsService,
            final WithVersionUriComponentsBuilder pUriBuilder
    ) {
        this.bankDetailsService = bankDetailsService;
        this.uriBuilder = pUriBuilder;
    }

    @AnyRole
    @GetMapping("/bank-details/{id}")
    public ResponseEntity<ApiResponse<BankDetailsResponse>> getBankDetailsById(
            @PathVariable final UUID id,
            final HttpServletRequest request
    ) {
        LOGGER.debug("Getting bank details for id: {}", id);
        return buildSuccessfulResponse(
                request.getRequestURI(),
                bankDetailsService.getBankDetailsById(id),
                HttpStatus.OK
        );
    }

    @AnyRole
    @GetMapping("/claim/{claimId}/bank-details")
    public ResponseEntity<ApiResponse<BankDetailsResponse>> getBankDetailsByClaimId(
            @PathVariable final UUID claimId,
            final HttpServletRequest request
    ) {
        LOGGER.debug("Getting bank details for claimId: {}", claimId);
        return buildSuccessfulResponse(
                request.getRequestURI(),
                bankDetailsService.getBankDetailsByClaimId(claimId),
                HttpStatus.OK
        );
    }

    @PreAuthorize("!hasAnyAuthority('SCA')")
    @PostMapping("/claim/{claimId}/bank-details")
    public ResponseEntity<ApiResponse<UUID>> createBankDetails(
            @PathVariable("claimId") final UUID claimId,
            @RequestBody @Validated final BankDetailsRequest bankDetailsRequest
    ) {
        LOGGER.debug("Creating bank details for claimId: {}", claimId);
        final UUID savedBankDetailsId = bankDetailsService.save(claimId, bankDetailsRequest);
        return buildSuccessfulResponse(
                buildResourceUriFor(savedBankDetailsId).toString(),
                savedBankDetailsId,
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasAnyAuthority('CCM', 'CCA', 'WC')")
    @PatchMapping("/bank-details/{id}")
    public ResponseEntity<ApiResponse<UUID>> updateBankDetails(
            @PathVariable("id") final UUID id,
            @RequestBody @Validated final BankDetailsRequest bankDetailsRequest
    ) {
        LOGGER.debug("Updating bank details for id: {}", id);
        final UUID savedBankDetailsId = bankDetailsService.update(id, bankDetailsRequest);
        return buildSuccessfulResponse(
                buildResourceUriFor(savedBankDetailsId).toString(),
                savedBankDetailsId,
                HttpStatus.OK
        );
    }

    @WC
    @DeleteMapping("/bank-details/{id}")
    public ResponseEntity deleteBankDetails(@PathVariable final UUID id,
                                            final HttpServletRequest request) {
        LOGGER.debug("Deleting bank details for id: {}", id);
        bankDetailsService.getBankDetailsById(id);
        bankDetailsService.delete(id);
        return buildSuccessfulResponse(
                request.getRequestURI(),
                null,
                HttpStatus.OK
        );
    }

    private <T> ResponseEntity<ApiResponse<T>> buildSuccessfulResponse(
            final String path,
            final T objectToReturn,
            final HttpStatus status
    ) {
        return new ResponseBuilder<T>()
                .withStatus(status)
                .withSuccessData(URI.create(path), objectToReturn)
                .build();
    }

    private URI buildResourceUriFor(final UUID id) {
        return fromController(uriBuilder, getClass())
                .path("/bank-details/{bankDetailsId}")
                .buildAndExpand(id)
                .toUri();
    }
}
