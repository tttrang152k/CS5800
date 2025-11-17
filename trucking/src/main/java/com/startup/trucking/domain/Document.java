package com.startup.trucking.domain;

import java.net.URI;
import java.time.OffsetDateTime;

public class Document {
    private final String id;
    private final String loadId;
    private final String type;   // BOL, POD, RC, Other
    private String status;       // Uploaded, Verified, Rejected
    private final URI fileRef;
    private final OffsetDateTime uploadedAt;

    public Document(String id, String loadId, String type, String status, URI fileRef) {
        this.id = id;
        this.loadId = loadId;
        this.type = type;
        this.status = status;
        this.fileRef = fileRef;
        this.uploadedAt = OffsetDateTime.now();
    }

    public java.net.URI downloadUri() { return fileRef; }

    // getters/setters
    public String getId() { return id; }
    public String getLoadId() { return loadId; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public OffsetDateTime getUploadedAt() { return uploadedAt; }
}
