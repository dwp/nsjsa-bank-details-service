package uk.gov.dwp.jsa.bankdetails.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsAlreadyExistsException;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsByClaimIdNotFoundException;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsByIdNotFoundException;
import uk.gov.dwp.jsa.bankdetails.service.models.db.BankDetails;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsRequest;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsResponse;
import uk.gov.dwp.jsa.bankdetails.service.repositories.BankDetailsRepository;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankDetailsServiceTest {

    private static final UUID EXPECTED_BANKDETAILS_ID = UUID.randomUUID();
    private static final UUID GIVEN_CLAIM_ID = UUID.randomUUID();
    private static final UUID GIVEN_UNKNOWN_CLAIM_ID = UUID.randomUUID();
    private static final UUID GIVEN_BANKDETAILS_ID = UUID.randomUUID();
    private static final UUID GIVEN_UNKNOWN_BANKDETAILS_ID = UUID.randomUUID();
    private static final String EXPECTED_BANK_DETAILS_JSON = "MyJSON";

    private BankDetails buildExpectedBankDetails;

    private BankDetailsService sut;

    @Mock
    private BankDetailsRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private BankDetailsRequest bankDetailsRequest;

    private BankDetailsRequest bankDetailsResponse;


    @Before
    public void setUp() throws Exception {
        buildExpectedBankDetails = buildExpectedBankDetails();
        buildExpectedBankDetails.setId(EXPECTED_BANKDETAILS_ID);

        bankDetailsResponse = new BankDetailsResponse(buildExpectedBankDetails);
        sut = new BankDetailsService(repository, mapper);

        when(mapper.writeValueAsString(bankDetailsRequest)).thenReturn(EXPECTED_BANK_DETAILS_JSON);
        when(repository.save(any())).thenReturn(buildExpectedBankDetails);
        when(repository.findById(GIVEN_BANKDETAILS_ID)).thenReturn(Optional.of(buildExpectedBankDetails));
        when(repository.findByClaimId(GIVEN_CLAIM_ID.toString())).thenReturn(Optional.of(buildExpectedBankDetails));
    }

    @Test
    public void givenValidRequest_Save_ShouldReturnExpectedBankDetailsId() {
        UUID claimId = sut.save(GIVEN_CLAIM_ID, bankDetailsRequest);
        assertEquals(EXPECTED_BANKDETAILS_ID, claimId);
    }

    @Test
    public void givenValidRequest_Save_ShouldSaveTheExpectedDataToRepository() {
        sut.save(GIVEN_CLAIM_ID, bankDetailsRequest);

        ArgumentCaptor<BankDetails> captor = ArgumentCaptor.forClass(BankDetails.class);

        verify(repository, times(1)).save(captor.capture());

        assertThat(captor.getValue().getClaimId(), is(GIVEN_CLAIM_ID.toString()));
    }

    @Test
    public void givenValidRequest_Save_ShouldMarshallRequest() throws JsonProcessingException {
        sut.save(GIVEN_CLAIM_ID, bankDetailsRequest);

        ArgumentCaptor<BankDetailsRequest> captor = ArgumentCaptor.forClass(BankDetailsRequest.class);

        verify(mapper, times(1)).writeValueAsString(captor.capture());

        assertThat(captor.getValue(), is(bankDetailsRequest));
    }

    @Test
    public void givenValidBankDetailsId_getBankDetailsById_ShouldReturnExpectedBankDetails() {
        assertEquals(bankDetailsResponse, sut.getBankDetailsById(GIVEN_BANKDETAILS_ID));
    }

    @Test(expected = BankDetailsByIdNotFoundException.class)
    public void givenUnvalidBankDetailsId_getClaimantById_ShouldReturnNull() {
        assertNull(sut.getBankDetailsById(GIVEN_UNKNOWN_BANKDETAILS_ID));
    }

    @Test
    public void givenValidClaimId_getBankDetailsByClaimId_ShouldReturnExpectedBankDetailsList() {
        assertEquals(bankDetailsResponse, sut.getBankDetailsByClaimId(GIVEN_CLAIM_ID));
    }

    @Test(expected = BankDetailsByClaimIdNotFoundException.class)
    public void givenUnvalidClaimId_getBankDetailsByClaimId_ShouldReturnNull() {
        assertNull(sut.getBankDetailsByClaimId(GIVEN_UNKNOWN_CLAIM_ID));
    }

    @Test
    public void testGivenValidClaimantIdShouldDeleteTheExpectedData() {
        sut.delete(EXPECTED_BANKDETAILS_ID);

        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(repository, times(1)).deleteById(captor.capture());
        assertThat(captor.getValue(), is(EXPECTED_BANKDETAILS_ID));
    }

    @Test
    public void givenValidRequest_Update_ShouldReturnExpectedBankDetailsId() {
        UUID claimId = sut.update(GIVEN_BANKDETAILS_ID, bankDetailsRequest);
        assertEquals(EXPECTED_BANKDETAILS_ID, claimId);
    }

    @Test
    public void givenValidRequest_Update_ShouldUpdateTheExpectedDataToRepository() {
        sut.update(GIVEN_BANKDETAILS_ID, bankDetailsRequest);

        ArgumentCaptor<BankDetails> captor = ArgumentCaptor.forClass(BankDetails.class);

        verify(repository, times(1)).save(captor.capture());

        assertThat(captor.getValue().getClaimId(), is(GIVEN_CLAIM_ID.toString()));
    }

    @Test
    public void givenValidRequest_Updae_ShouldMarshallRequest() throws JsonProcessingException {
        sut.update(GIVEN_BANKDETAILS_ID, bankDetailsRequest);

        ArgumentCaptor<BankDetailsRequest> captor = ArgumentCaptor.forClass(BankDetailsRequest.class);

        verify(mapper, times(1)).writeValueAsString(captor.capture());

        assertThat(captor.getValue(), is(bankDetailsRequest));
    }


    @Test(expected = BankDetailsAlreadyExistsException.class)
    public void givenWrongRequest_Save_ShouldReturnExpectedBankDetailsId() {
        when(this.repository.save(any())).thenThrow(new DataIntegrityViolationException("erro"));
        sut.save(GIVEN_CLAIM_ID, bankDetailsRequest);
    }

    @Test(expected = BankDetailsAlreadyExistsException.class)
    public void givenWrongRequest_Update_ShouldReturnExpectedBankDetailsId() {
        when(this.repository.save(any())).thenThrow(new DataIntegrityViolationException("erro"));
        sut.update(GIVEN_BANKDETAILS_ID, bankDetailsRequest);
    }

    private BankDetails buildExpectedBankDetails() {
        return new BankDetails(
                GIVEN_CLAIM_ID.toString(),
                bankDetailsRequest,
                "fake-hash",
                "fake-source",
                "fake-version"
        );
    }


}
