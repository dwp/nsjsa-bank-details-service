package uk.gov.dwp.jsa.bankdetails.service.controllers;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.adaptors.http.api.ApiSuccess;
import uk.gov.dwp.jsa.bankdetails.service.AppInfo;
import uk.gov.dwp.jsa.bankdetails.service.config.WithVersionUriComponentsBuilder;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsByClaimIdNotFoundException;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsByIdNotFoundException;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsRequest;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsResponse;
import uk.gov.dwp.jsa.bankdetails.service.services.BankDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankDetailsControllerTest {

    private static final String NSJSA_CITIZEN_BASE_URL = "/nsjsa";
    private static final UUID VALID_BD_ID = UUID.randomUUID();
    private static final UUID UNVALID_BD_ID = UUID.randomUUID();
    private static final UUID VALID_CLAIM_ID = UUID.randomUUID();
    private static final UUID UNVALID_CLAIM_ID = UUID.randomUUID();
    private static final URI VALID_BD_URL = URI.create(NSJSA_CITIZEN_BASE_URL + "/bank-details/" + VALID_BD_ID);

    private BankDetailsController sut;

    @Mock
    private BankDetailsService bankDetailsService;

    @Mock
    private BankDetailsRequest bankDetailsRequest;

    @Mock
    private BankDetailsResponse expectedResponse;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private AppInfo appInfo;

    @Before
    public void setUp() {

        when(appInfo.getVersion()).thenReturn(StringUtils.EMPTY);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteHost("localhost");
        request.setScheme("http");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        sut = new BankDetailsController(bankDetailsService, new WithVersionUriComponentsBuilder(appInfo));
        when(bankDetailsService.save(any(), any())).thenReturn(VALID_BD_ID);
        when(bankDetailsService.getBankDetailsById(VALID_BD_ID)).thenReturn(expectedResponse);
        when(bankDetailsService.getBankDetailsByClaimId(VALID_CLAIM_ID)).thenReturn(expectedResponse);
        when(httpServletRequest.getRequestURI()).thenReturn(VALID_BD_URL.toString());
        when(bankDetailsService.getBankDetailsById(UNVALID_BD_ID)).thenThrow(BankDetailsByIdNotFoundException.class);
    }

    @Test
    public void givenValidRequest_Controller_ShouldReturnExpectedResponse() {
        ResponseEntity<ApiResponse<UUID>> uriResponseEntity = sut.createBankDetails(VALID_CLAIM_ID, bankDetailsRequest);
        ApiSuccess<UUID> apiSuccess = uriResponseEntity.getBody().getSuccess().get(0);
        assertEquals(HttpStatus.CREATED, uriResponseEntity.getStatusCode());
        assertEquals(VALID_BD_URL, apiSuccess.getPath());
        assertEquals(VALID_BD_ID, apiSuccess.getData());

    }

    @Test
    public void givenValidRequest_ServiceSave_ShouldBeCalledOnce() {
        sut.createBankDetails(VALID_BD_ID, bankDetailsRequest);

        ArgumentCaptor<BankDetailsRequest> captor = ArgumentCaptor.forClass(BankDetailsRequest.class);

        verify(bankDetailsService, times(1)).save(any(), captor.capture());

        assertThat(captor.getValue(), is(bankDetailsRequest));
    }

    @Test
    public void givenValidBankDetailsId_getBankDetailsById_ShouldReturnTheBankDetailsInformation() {
        ResponseEntity<ApiResponse<BankDetailsResponse>> bankDetailsResponse = sut.getBankDetailsById(VALID_BD_ID,
                httpServletRequest);
        assertEquals(expectedResponse, bankDetailsResponse.getBody().getSuccess().get(0).getData());
        assertEquals(HttpStatus.OK, bankDetailsResponse.getStatusCode());
    }

    @Test(expected = BankDetailsByIdNotFoundException.class)
    public void givenUnvalidBankDetailsId_getBankDetailsById_ShouldReturn404() {
        when(bankDetailsService.getBankDetailsById(any())).thenThrow(BankDetailsByIdNotFoundException.class);
        sut.getBankDetailsById(UNVALID_BD_ID, httpServletRequest);
    }

    @Test
    public void givenValidClaimId_getBankDetailsByClaimId_ShouldReturnTheBankDetailsInformation() {
        ResponseEntity<ApiResponse<BankDetailsResponse>> response =
                sut.getBankDetailsByClaimId(VALID_CLAIM_ID, httpServletRequest);
        assertEquals(expectedResponse, response.getBody().getSuccess().get(0).getData());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = BankDetailsByClaimIdNotFoundException.class)
    public void givenUnvalidClaimId_getBankDetailsByClaimId_ShouldReturn404() {
        when(bankDetailsService.getBankDetailsByClaimId(UNVALID_CLAIM_ID))
                .thenThrow(BankDetailsByClaimIdNotFoundException.class);
        sut.getBankDetailsByClaimId(UNVALID_CLAIM_ID, httpServletRequest);
    }

    @Test
    public void testGivenValidIdShouldDelete() {
        ResponseEntity<ApiResponse<BankDetailsResponse>> bankDetailsResponse = sut.deleteBankDetails(VALID_BD_ID,
                httpServletRequest);
        assertEquals(VALID_BD_URL, bankDetailsResponse.getBody().getSuccess().get(0).getPath());
        assertEquals(HttpStatus.OK, bankDetailsResponse.getStatusCode());
    }

    @Test(expected = BankDetailsByIdNotFoundException.class)
    public void testGivenInValidIdShouldReturnNotFound() {
        ResponseEntity<ApiResponse<BankDetailsResponse>> bankDetailsResponse = sut.deleteBankDetails(UNVALID_BD_ID,
                httpServletRequest);
    }

    @Test
    public void givenValidRequest_ServiceUpdate_ShouldBeCalledOnce() {
        sut.updateBankDetails(VALID_BD_ID, bankDetailsRequest);

        ArgumentCaptor<BankDetailsRequest> captor = ArgumentCaptor.forClass(BankDetailsRequest.class);

        verify(bankDetailsService, times(1)).update(any(), captor.capture());

        assertThat(captor.getValue(), is(bankDetailsRequest));
    }
}
