package com.agonyforge.core.model;

import com.agonyforge.core.controller.interpret.PrimaryConnectionState;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Connection {
    public static final String DEFAULT_SECONDARY_STATE = "DEFAULT";

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String sessionUsername;
    private String sessionId;
    private String httpSessionId;
    private String remoteAddress;
    private String oauthUsername;
    private String name;
    private Date disconnected;

    @Enumerated(EnumType.STRING)
    private PrimaryConnectionState primaryState;

    private String secondaryState;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSessionUsername() {
        return sessionUsername;
    }

    public void setSessionUsername(String sessionUsername) {
        this.sessionUsername = sessionUsername;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getHttpSessionId() {
        return httpSessionId;
    }

    public void setHttpSessionId(String httpSessionId) {
        this.httpSessionId = httpSessionId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getOauthUsername() {
        return oauthUsername;
    }

    public void setOauthUsername(String oauthUsername) {
        this.oauthUsername = oauthUsername;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDisconnected() {
        return disconnected;
    }

    public void setDisconnected(Date disconnected) {
        this.disconnected = disconnected;
    }

    public PrimaryConnectionState getPrimaryState() {
        return primaryState;
    }

    public void setPrimaryState(PrimaryConnectionState primaryConnectionState) {
        this.primaryState = primaryConnectionState;
    }

    public String getSecondaryState() {
        return secondaryState == null ? DEFAULT_SECONDARY_STATE : secondaryState;
    }

    public void setSecondaryState(String secondaryState) {
        this.secondaryState = secondaryState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Connection)) return false;
        Connection that = (Connection) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
