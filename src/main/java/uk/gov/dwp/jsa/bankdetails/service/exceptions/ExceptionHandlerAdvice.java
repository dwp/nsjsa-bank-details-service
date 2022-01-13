package uk.gov.dwp.jsa.bankdetails.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.bankdetails.service.services.ResponseBuilder;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BankDetailsByIdNotFoundException.class)
    public final @ResponseBody
    ResponseEntity<ApiResponse<String>> handleBankDetailsByIdNotFoundException(
            final Exception ex,
            final WebRequest request
    ) {
        return new ResponseBuilder<String>()
                .withStatus(HttpStatus.NOT_FOUND)
                .withApiError(
                        BankDetailsByIdNotFoundException.CODE,
                        BankDetailsByIdNotFoundException.MESSAGE
                ).build();
    }

    @ExceptionHandler(BankDetailsByClaimIdNotFoundException.class)
    public final @ResponseBody
    ResponseEntity<ApiResponse<String>> handleBankDetailsByClaimIdNotFoundException(
            final Exception ex,
            final WebRequest request
    ) {
        return new ResponseBuilder<String>()
                .withStatus(HttpStatus.NOT_FOUND)
                .withApiError(
                        BankDetailsByClaimIdNotFoundException.CODE,
                        BankDetailsByClaimIdNotFoundException.MESSAGE
                ).build();
    }

    @ExceptionHandler(BankDetailsAlreadyExistsException.class)
    public final @ResponseBody
    ResponseEntity<ApiResponse<String>> handlePSQLException(
            final Exception ex,
            final WebRequest request
    ) {
        return new ResponseBuilder<String>()
                .withStatus(HttpStatus.CONFLICT)
                .withApiError(
                        BankDetailsAlreadyExistsException.CODE,
                        BankDetailsAlreadyExistsException.MESSAGE
                ).build();
    }

}
