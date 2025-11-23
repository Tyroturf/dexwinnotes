package com.dexwin.notes.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class NoteDto {

    public static class NoteRequest {
        @NotBlank
        @Size(max = 255)
        private String title;

        @NotBlank
        private String content;

        private String tags;

        private Long version;

        public String getTitle() { return title; }

        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }

        public void setContent(String content) { this.content = content; }

        public String getTags() { return tags; }

        public void setTags(String tags) { this.tags = tags; }

        public Long getVersion() { return version; }

        public void setVersion(Long version) { this.version = version; }
    }

    public static class NoteResponse {
        private Long id;
        private String title;
        private String content;
        private String tags;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant deletedAt;
        private Long version;

        public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

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
    }

    public static class PagedNotesResponse {
        private java.util.List<NoteResponse> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public java.util.List<NoteResponse> getContent() { return content; }

        public void setContent(java.util.List<NoteResponse> content) { this.content = content; }

        public int getPage() { return page; }

        public void setPage(int page) { this.page = page; }

        public int getSize() { return size; }

        public void setSize(int size) { this.size = size; }

        public long getTotalElements() { return totalElements; }

        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

        public int getTotalPages() { return totalPages; }

        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}
