package com.dexwin.notes.note;

import com.dexwin.notes.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 255)
    private String tags;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public User getOwner() { return owner; }

    public void setOwner(User owner) { this.owner = owner; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }

    public void setTags(String tags) { this.tags = tags; }

    public Instant getCreatedAt() { return createdAt; }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getDeletedAt() { return deletedAt; }

    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }

    public Long getVersion() { return version; }

    public void setVersion(Long version) { this.version = version; }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
