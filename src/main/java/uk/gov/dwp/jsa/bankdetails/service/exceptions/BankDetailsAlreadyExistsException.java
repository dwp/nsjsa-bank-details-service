package uk.gov.dwp.jsa.bankdetails.service.exceptions;

import uk.gov.dwp.jsa.bankdetails.service.services.Constants;

public class BankDetailsAlreadyExistsException extends RuntimeException {
    static final String CODE = Constants.DEFAULT_ERROR_CODE;
    static final String MESSAGE = "Bank details already exists";
}
