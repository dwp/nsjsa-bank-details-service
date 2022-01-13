package uk.gov.dwp.jsa.bankdetails.service.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.jsa.adaptors.http.api.ApiError;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.adaptors.http.api.ApiSuccess;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ResponseBuilder<T> {

    private HttpStatus httpStatus;
    private ApiError apiError;
    private List<ApiSuccess<T>> apiSuccessData;

    public ResponseBuilder<T> withStatus(final HttpStatus httpStatus) {
        Objects.requireNonNull(httpStatus);
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseBuilder<T> withApiError(final ApiError apiError) {
        Objects.requireNonNull(apiError);
        this.apiError = apiError;
        return this;
    }

    public ResponseBuilder<T> withApiError(final String error, final String message) {
        Objects.requireNonNull(error);
        Objects.requireNonNull(message);
        return withApiError(new ApiError(error, message));
    }

    public ResponseBuilder<T> withSuccessData(final ApiSuccess<T> apiSuccess) {
        Objects.requireNonNull(apiSuccess);
        return withSuccessData(Collections.singletonList(apiSuccess));
    }

    public ResponseBuilder<T> withSuccessData(final URI path, final T data) {
        Objects.requireNonNull(path);
        return withSuccessData(Collections.singletonList(new ApiSuccess<>(path, data)));
    }

    public ResponseBuilder<T> withSuccessData(final List<ApiSuccess<T>> apiSuccessData) {
        Objects.requireNonNull(apiSuccessData);
        this.apiSuccessData = apiSuccessData;
        return this;
    }

    public ResponseEntity<ApiResponse<T>> build() {
        Objects.requireNonNull(httpStatus);

        final ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setError(apiError);
        apiResponse.setSuccess(apiSuccessData);

        return ResponseEntity
                .status(httpStatus)
                .body(apiResponse);

    }
}
