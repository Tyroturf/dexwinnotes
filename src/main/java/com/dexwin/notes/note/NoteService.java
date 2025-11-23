package com.dexwin.notes.note;

import com.dexwin.notes.note.NoteDto.NoteRequest;
import com.dexwin.notes.note.NoteDto.NoteResponse;
import com.dexwin.notes.note.NoteDto.PagedNotesResponse;
import com.dexwin.notes.user.User;
import com.dexwin.notes.user.UserRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository notes;
    private final UserRepository users;

    public NoteService(NoteRepository notes, UserRepository users) {
        this.notes = notes;
        this.users = users;
    }

    public Long userIdFromEmail(String email) {
        User user = users.findByEmail(email).orElseThrow();
        return user.getId();
    }

    @Transactional
    public NoteResponse create(Long userId, NoteRequest req) {
        User user = users.findById(userId).orElseThrow();
        Note note = new Note();
        note.setOwner(user);
        note.setTitle(req.getTitle());
        note.setContent(req.getContent());
        note.setTags(normalizeTags(req.getTags()));
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        Note saved = notes.save(note);
        return toResponse(saved);
    }

    @Transactional
    public NoteResponse update(Long userId, Long noteId, NoteRequest req) {
        Note note = notes.findById(noteId).orElseThrow();
        if (!note.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Not your note");
        }
        if (req.getVersion() != null && !req.getVersion().equals(note.getVersion())) {
            throw new OptimisticLockException("Version mismatch");
        }
        note.setTitle(req.getTitle());
        note.setContent(req.getContent());
        note.setTags(normalizeTags(req.getTags()));
        note.setUpdatedAt(Instant.now());
        Note saved = notes.save(note);
        return toResponse(saved);
    }

    @Transactional
    public void softDelete(Long userId, Long id) {
        Note note = notes.findById(id).orElseThrow();
        if (!note.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Not your note");
        }
        note.setDeletedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
    }

    @Transactional
    public void restore(Long userId, Long id) {
        Note note = notes.findById(id).orElseThrow();
        if (!note.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Not your note");
        }
        note.setDeletedAt(null);
        note.setUpdatedAt(Instant.now());
    }

    @Transactional(readOnly = true)
    public NoteResponse getOne(Long userId, Long id) {
        Note note = notes.findById(id).orElseThrow();
        if (!note.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Not your note");
        }
        return toResponse(note);
    }

    @Transactional(readOnly = true)
    public PagedNotesResponse search(Long userId, String q, String tags,
                                     int page, int size, String sortBy, String direction) {
        User user = users.findById(userId).orElseThrow();
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        String qTrim = (q == null || q.isBlank()) ? null : q.trim();
        String firstTag = firstTag(normalizeTags(tags));

        Page<Note> result;
        if (qTrim == null && firstTag == null) {
            result = notes.findByOwnerAndDeletedAtIsNull(user, pageable);
        } else {
            result = notes.searchByOwnerQueryAndTag(user, qTrim, firstTag, pageable);
        }

        PagedNotesResponse resp = new PagedNotesResponse();
        resp.setContent(result.getContent().stream().map(this::toResponse).collect(Collectors.toList()));
        resp.setPage(result.getNumber());
        resp.setSize(result.getSize());
        resp.setTotalElements(result.getTotalElements());
        resp.setTotalPages(result.getTotalPages());
        return resp;
    }

    private String normalizeTags(String tags) {
        if (tags == null || tags.isBlank()) return null;
        String[] parts = tags.split(",");
        return java.util.Arrays.stream(parts)
                .map(t -> t.trim().toLowerCase(Locale.ROOT))
                .filter(t -> !t.isBlank())
                .distinct()
                .collect(Collectors.joining(","));
    }

    private String firstTag(String normalizedTags) {
        if (normalizedTags == null || normalizedTags.isBlank()) return null;
        int idx = normalizedTags.indexOf(',');
        return idx == -1 ? normalizedTags : normalizedTags.substring(0, idx);
    }

    private NoteResponse toResponse(Note note) {
        NoteResponse resp = new NoteResponse();
        resp.setId(note.getId());
        resp.setTitle(note.getTitle());
        resp.setContent(note.getContent());
        resp.setTags(note.getTags());
        resp.setCreatedAt(note.getCreatedAt());
        resp.setUpdatedAt(note.getUpdatedAt());
        resp.setDeletedAt(note.getDeletedAt());
        resp.setVersion(note.getVersion());
        return resp;
    }
}
