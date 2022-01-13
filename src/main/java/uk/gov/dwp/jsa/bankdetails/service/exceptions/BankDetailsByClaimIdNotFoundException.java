package uk.gov.dwp.jsa.bankdetails.service.exceptions;

import uk.gov.dwp.jsa.bankdetails.service.services.Constants;

public final class BankDetailsByClaimIdNotFoundException extends RuntimeException {
    static final String CODE = Constants.DEFAULT_ERROR_CODE;
    static final String MESSAGE = "Could not find the bank-details by claim id";
}
