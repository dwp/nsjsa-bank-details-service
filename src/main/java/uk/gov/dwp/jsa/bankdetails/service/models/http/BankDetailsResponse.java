package uk.gov.dwp.jsa.bankdetails.service.models.http;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.BeanUtils;
import uk.gov.dwp.jsa.bankdetails.service.models.db.BankDetails;

import java.util.Objects;

public class BankDetailsResponse extends BankDetailsRequest {

    public BankDetailsResponse(final BankDetails bankDetails) {
        Objects.requireNonNull(bankDetails);
        Objects.requireNonNull(bankDetails.getBankDetailsJson());
        BeanUtils.copyProperties(bankDetails.getBankDetailsJson(), this);
        this.setId(bankDetails.getId());
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
