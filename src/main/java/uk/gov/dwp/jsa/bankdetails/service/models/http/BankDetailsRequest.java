package uk.gov.dwp.jsa.bankdetails.service.models.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;

import javax.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankDetailsRequest extends BankDetails {
    @NotBlank
    @Override
    public String getAccountHolder() {
        return super.getAccountHolder();
    }

    @NotBlank
    @Override
    public String getAccountNumber() {
        return super.getAccountNumber();
    }

    @NotBlank
    @Override
    public String getSortCode() {
        return super.getSortCode();
    }
}
