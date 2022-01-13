package uk.gov.dwp.jsa.bankdetails.service.models.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import uk.gov.dwp.jsa.bankdetails.service.models.http.BankDetailsRequest;
import uk.gov.dwp.jsa.security.encryption.SecuredJsonBinaryType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@TypeDef(name = "jsonb", typeClass = SecuredJsonBinaryType.class)
public class BankDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;
    @CreationTimestamp
    private LocalDateTime createdTimestamp;
    private String claimId;
    @UpdateTimestamp
    private LocalDateTime updatedTimestamp;
    private String hash;
    private String source;
    private String serviceVersion;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private BankDetailsRequest bankDetailsJson;

    public BankDetails() {
        // disabling the creation of an empty bank details object
    }

    public BankDetails(
            final UUID pId,
            final BankDetailsRequest bankDetailsJson,
            final String serviceVersion
    ) {
        this(null, bankDetailsJson, null, null, serviceVersion);
        this.id = pId;
    }

    public BankDetails(
            final String claimId,
            final BankDetailsRequest bankDetailsJson,
            final String hash,
            final String source,
            final String serviceVersion
    ) {
        this.claimId = claimId;
        this.bankDetailsJson = bankDetailsJson;
        this.hash = hash;
        this.source = source;
        this.serviceVersion = serviceVersion;
    }


    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(final String claimId) {
        this.claimId = claimId;
    }

    public BankDetailsRequest getBankDetailsJson() {
        return bankDetailsJson;
    }

    public void setBankDetailsJson(final BankDetailsRequest bankDetailsJson) {
        this.bankDetailsJson = bankDetailsJson;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(final String hash) {
        this.hash = hash;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(final String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
}
