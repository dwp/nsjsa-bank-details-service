package uk.gov.dwp.jsa.bankdetails.service.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsRequest;
import uk.gov.dwp.jsa.security.encryption.BaseJsonEncryption;
import uk.gov.dwp.jsa.security.encryption.EncryptionStrategy;

import java.util.Arrays;
import java.util.List;

@Component
public class BankDetailsRequestJsonEncryption extends BaseJsonEncryption<BankDetailsRequest> {

    private static final List<String> LIST_OF_SECURED_FIELDS =
            Arrays.asList("/claimantId", "/accountHolder", "/sortCode", "/accountNumber", "/reference");

    public BankDetailsRequestJsonEncryption(final EncryptionStrategy pStrategy, final ObjectMapper pMapper) {
        super(pStrategy, pMapper);
    }

    @Override
    public Class<BankDetailsRequest> getGenericType() {
        return BankDetailsRequest.class;
    }

    @Override
    public List<String> getFieldsPathToSecure() {
        return LIST_OF_SECURED_FIELDS;
    }
}
