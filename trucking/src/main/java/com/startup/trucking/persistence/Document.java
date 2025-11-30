package com.startup.trucking.persistence;

import jakarta.persistence.*;
import java.net.URI;
import java.time.OffsetDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    private String id;                    // e.g., DOC-123

    @Column(name = "load_id", nullable = false)
    private String loadId;

    @Column(nullable = false, length = 16)
    private String type;                  // BOL, POD

    @Column(nullable = false, length = 32)
    private String status;                // Uploaded, Verified, Rejected

    @Column(name = "file_ref", nullable = false, length = 2000)
    private String fileRef;               // store URI as String

    @Column(name = "uploaded_at", nullable = false)
    private OffsetDateTime uploadedAt;

    @Transient
    public URI downloadUri() { return fileRef != null ? URI.create(fileRef) : null; }

    // getters/setters
    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getLoadId() { return loadId; } public void setLoadId(String loadId) { this.loadId = loadId; }
    public String getType() { return type; } public void setType(String type) { this.type = type; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getFileRef() { return fileRef; } public void setFileRef(String fileRef) { this.fileRef = fileRef; }
    public OffsetDateTime getUploadedAt() { return uploadedAt; } public void setUploadedAt(OffsetDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
