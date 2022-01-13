package uk.gov.dwp.jsa.bankdetails.service.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomErrorControllerTest {

    private static final String STATUS = "status";
    private CustomErrorController sut;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ErrorAttributes errorAttributes;


    @Before
    public void setUp() {
        sut = new CustomErrorController(errorAttributes);
    }

    @Test
    public void method_not_allowed_should_return_expected_http_status_and_message() {
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(Boolean.class))).thenReturn(
                new HashMap<String, Object>() {{
                    put(STATUS, HttpStatus.METHOD_NOT_ALLOWED.value());
                }}
        );
        ResponseEntity<ApiResponse<String>> responseEntity = sut.handleError(request);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), responseEntity.getBody().getError().getMessage());
        assertEquals(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()), responseEntity.getBody().getError().getCode());
    }

    @Test
    public void not_found_should_return_expected_http_status_and_message() {
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(Boolean.class))).thenReturn(
                new HashMap<String, Object>() {{
                    put(STATUS, HttpStatus.NOT_FOUND.value());
                }}
        );
        ResponseEntity<ApiResponse<String>> responseEntity = sut.handleError(request);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseEntity.getBody().getError().getMessage());
        assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), responseEntity.getBody().getError().getCode());
    }

    public void bad_request_should_return_expected_http_status_and_message() {
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(Boolean.class))).thenReturn(
                new HashMap<String, Object>() {{
                    put(STATUS, HttpStatus.BAD_REQUEST.value());
                }}
        );
        ResponseEntity<ApiResponse<String>> responseEntity = sut.handleError(request);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseEntity.getBody().getError().getMessage());
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST.value()), responseEntity.getBody().getError().getCode());
    }

    public void conflict_should_return_expected_http_status_and_message() {
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(Boolean.class))).thenReturn(
                new HashMap<String, Object>() {{
                    put(STATUS, HttpStatus.CONFLICT.value());
                }}
        );
        ResponseEntity<ApiResponse<String>> responseEntity = sut.handleError(request);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), responseEntity.getBody().getError().getMessage());
        assertEquals(String.valueOf(HttpStatus.CONFLICT.value()), responseEntity.getBody().getError().getCode());
    }
}
