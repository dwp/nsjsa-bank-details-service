package uk.gov.dwp.jsa.bankdetails.service.accepatance_tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.adaptors.http.api.ApiSuccess;
import uk.gov.dwp.jsa.bankdetails.service.AppInfo;
import uk.gov.dwp.jsa.bankdetails.service.config.WithVersionUriComponentsBuilder;
import uk.gov.dwp.jsa.bankdetails.service.controllers.BankDetailsController;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsAlreadyExistsException;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsByClaimIdNotFoundException;
import uk.gov.dwp.jsa.bankdetails.service.exceptions.BankDetailsByIdNotFoundException;
import uk.gov.dwp.jsa.bankdetails.service.models.db.BankDetails;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsRequest;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsResponse;
import uk.gov.dwp.jsa.bankdetails.service.services.BankDetailsService;
import uk.gov.dwp.jsa.security.WithMockUser;
import uk.gov.dwp.jsa.security.roles.Role;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BankDetailsController.class)
public class BankDetailsControllerAcceptanceTest {

    private static final String DATA_MODEL_VERSION = "v1";
    private static final String CLAIM_BASE_PATH = "/nsjsa";

    private static final UUID VALID_BANKDETAILS_ID = UUID.randomUUID();
    private static final UUID UNVALID_BANKDETAILS_ID = UUID.randomUUID();
    private static final URI VALID_BANKDETAILS_BY_ID_URL = URI.create(CLAIM_BASE_PATH + "/bank-details/" + VALID_BANKDETAILS_ID);
    private static final URI UNVALID_BANKDETAILS_BY_ID_URL = URI.create(CLAIM_BASE_PATH + "/bank-details/" + UNVALID_BANKDETAILS_ID);

    private static final UUID VALID_CLAIM_ID = UUID.randomUUID();
    private static final UUID UNVALID_CLAIM_ID = UUID.randomUUID();
    private static final URI VALID_BANKDETAILS_BY_CLAIM_ID_URL = URI.create(CLAIM_BASE_PATH + "/claim/" + VALID_CLAIM_ID + "/bank-details/");
    private static final URI UNVALID_BANKDETAILS_BY_CLAIM_ID_URL = URI.create(CLAIM_BASE_PATH + "/claim/" + UNVALID_CLAIM_ID + "/bank-details/");

    private static final URI SAVE_BANK_DETAILS_URL = URI.create(CLAIM_BASE_PATH + "/claim/" + VALID_CLAIM_ID + "/bank-details/");

    private static final BankDetailsRequest BANK_DETAILS_REQUEST = createBankDetailsRequest();

    private static final BankDetails BANK_DETAILS = new BankDetails(VALID_BANKDETAILS_ID, BANK_DETAILS_REQUEST, DATA_MODEL_VERSION);

    private static final BankDetailsResponse SERVICE_BANK_DETAILS_RESPONSE = new BankDetailsResponse(BANK_DETAILS);

    private static final ApiSuccess<BankDetailsResponse> BANK_DETAILS_API_SUCCESS_BY_ID = new ApiSuccess<>(VALID_BANKDETAILS_BY_ID_URL, SERVICE_BANK_DETAILS_RESPONSE);
    private static final ApiResponse BANK_DETAILS_API_RESPONSE_BY_ID = new ApiResponse(Collections.singletonList(BANK_DETAILS_API_SUCCESS_BY_ID));

    private static final ApiSuccess<BankDetailsResponse> BANK_DETAILS_API_SUCCESS_BY_CLAIM_ID = new ApiSuccess<>(VALID_BANKDETAILS_BY_CLAIM_ID_URL, SERVICE_BANK_DETAILS_RESPONSE);
    private static final ApiResponse BANK_DETAILS_API_RESPONSE_BY_CLAIM_ID = new ApiResponse(Collections.singletonList(BANK_DETAILS_API_SUCCESS_BY_CLAIM_ID));

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private BankDetailsService service;

    @MockBean(name = "appInfo")
    private AppInfo appInfo;

    @MockBean
    private WithVersionUriComponentsBuilder uriBuilder;

    @Autowired
    private MockMvc mockMvc;

    private static BankDetailsRequest createBankDetailsRequest() {
        BankDetailsRequest bankDetailsRequest = new BankDetailsRequest();
        bankDetailsRequest.setAccountHolder("Account Holder");
        bankDetailsRequest.setAccountNumber("Account Number");
        bankDetailsRequest.setReference("reference");
        bankDetailsRequest.setSortCode("sort-code");
        return bankDetailsRequest;
    }

    @Before
    public void setUp() {
        when(appInfo.getVersion()).thenReturn(StringUtils.EMPTY);
        when(uriBuilder.cloneBuilder()).thenReturn(new WithVersionUriComponentsBuilder(appInfo));
        when(service.save(any(), any())).thenReturn(VALID_BANKDETAILS_ID);
        when(service.update(any(), any())).thenReturn(VALID_BANKDETAILS_ID);
        when(service.getBankDetailsById(VALID_BANKDETAILS_ID)).thenReturn(SERVICE_BANK_DETAILS_RESPONSE);
        when(service.getBankDetailsByClaimId(VALID_CLAIM_ID)).thenReturn(SERVICE_BANK_DETAILS_RESPONSE);
    }

    @WithMockUser
    @Test
    public void GivenValidAndPopulatedRequest_ShouldSaveAndReturnExpectedURL() throws Exception {
        mockMvc.perform(post(SAVE_BANK_DETAILS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(BANK_DETAILS_REQUEST)))
                .andExpect(content().string(containsString(VALID_BANKDETAILS_BY_ID_URL.toString())))
                .andExpect(status().isCreated());
    }

    @WithMockUser(role = Role.CCA)
    @Test
    public void GivenNoRequest_ShouldNotSaveAndReturnBadRequest() throws Exception {
        mockMvc.perform(post(SAVE_BANK_DETAILS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(role = Role.CCA)
    @Test
    public void GivenEmptyRequest_ShouldNotSaveAndReturnBadRequest() throws Exception {
        mockMvc.perform(post(SAVE_BANK_DETAILS_URL)
                .with(csrf())
                .content(mapper.writeValueAsString(new BankDetailsRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(role = Role.CCA)
    @Test
    public void GivenAlreadyExistentBankDetailsRequest_ShouldNotSaveAndReturnConflict() throws Exception {
        when(service.save(any(), any())).thenThrow(BankDetailsAlreadyExistsException.class);
        mockMvc.perform(post(SAVE_BANK_DETAILS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(BANK_DETAILS_REQUEST)))
                .andExpect(status().isConflict());
    }

    @WithMockUser(role = Role.CCA)
    @Test
    public void GivenInvalidRequestJson_SaveShouldReturnBadRequest() throws Exception {
        when(service.save(any(), any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(SAVE_BANK_DETAILS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @WithMockUser(role = Role.SCA)
    @Test
    public void GivenValidBankDetailsId_ShouldReturnBankDetails() throws Exception {
        mockMvc.perform(get(VALID_BANKDETAILS_BY_ID_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(BANK_DETAILS_API_RESPONSE_BY_ID)))
                .andExpect(status().isOk());
    }

    @WithMockUser(role = Role.WC)
    @Test
    public void GivenUnvalidBankDetailsId_ShouldReturnNotFound() throws Exception {
        when(service.getBankDetailsById(UNVALID_BANKDETAILS_ID)).thenThrow(BankDetailsByIdNotFoundException.class);

        mockMvc.perform(get(UNVALID_BANKDETAILS_BY_ID_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(role = Role.CCM)
    @Test
    public void GivenValidClaimId_ShouldReturnBankDetailsList() throws Exception {

        mockMvc.perform(get(VALID_BANKDETAILS_BY_CLAIM_ID_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(BANK_DETAILS_API_RESPONSE_BY_CLAIM_ID)))
                .andExpect(status().isOk());
    }

    @WithMockUser(role = Role.CCA)
    @Test
    public void GivenUnvalidClaimId_ShouldReturnNotFound() throws Exception {
        when(service.getBankDetailsByClaimId(UNVALID_CLAIM_ID)).thenThrow(BankDetailsByClaimIdNotFoundException.class);

        mockMvc.perform(get(UNVALID_BANKDETAILS_BY_CLAIM_ID_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(role = Role.WC)
    @Test
    public void testGivenValidIdShouldDeleteAndReturnExpectedURL() throws Exception {
        mockMvc.perform(delete(VALID_BANKDETAILS_BY_ID_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.containsString(VALID_BANKDETAILS_BY_ID_URL.toString())))
                .andExpect(status().isOk());
    }

    @WithMockUser(role = Role.WC)
    @Test
    public void testGivenInValidIdShouldReturnNotFoundAndReturnExpectedURL() throws Exception {
        when(service.getBankDetailsById(UNVALID_BANKDETAILS_ID)).thenThrow(BankDetailsByIdNotFoundException.class);

        mockMvc.perform(delete(UNVALID_BANKDETAILS_BY_ID_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(role = Role.WC)
    @Test
    public void GivenValidAndPopulatedRequest_ShouldUpdateAndReturnExpectedURL() throws Exception {
        mockMvc.perform(patch(VALID_BANKDETAILS_BY_ID_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(BANK_DETAILS_REQUEST)))
                .andExpect(content().string(containsString(VALID_BANKDETAILS_BY_ID_URL.toString())))
                .andExpect(status().isOk());
    }

    private <T> String toJson(T objectToTransform) throws JsonProcessingException {
        return mapper.writeValueAsString(objectToTransform);
    }

}
