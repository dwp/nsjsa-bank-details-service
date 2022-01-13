package uk.gov.dwp.jsa.bankdetails.service.exceptions;

import uk.gov.dwp.jsa.bankdetails.service.services.Constants;

public final class BankDetailsByIdNotFoundException extends RuntimeException {
    public static final String CODE = Constants.DEFAULT_ERROR_CODE;
    public static final String MESSAGE = "Could not find the bank-details by bank-details id";
}
