package com.dexwin.notes.note;

import com.dexwin.notes.note.NoteDto.NoteRequest;
import com.dexwin.notes.note.NoteDto.NoteResponse;
import com.dexwin.notes.note.NoteDto.PagedNotesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    private Long userId(UserDetails user) {
        return service.userIdFromEmail(user.getUsername());
    }

    @Operation(
            summary = "Create a new note",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = NoteRequest.class),
                            examples = @ExampleObject(
                                    value = "{ \"title\": \"Meeting notes\", \"content\": \"Discuss Q4 roadmap\", \"tags\": \"work,planning\" }"
                            )
                    )
            )
    )
    @ApiResponse(responseCode = "201", description = "Note created")
    @PostMapping
    public ResponseEntity<NoteResponse> create(@AuthenticationPrincipal UserDetails user,
                                               @Valid @org.springframework.web.bind.annotation.RequestBody NoteRequest req) {
        Long userId = userId(user);
        return new ResponseEntity<>(service.create(userId, req), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a single note by id")
    @GetMapping("/{id}")
    public NoteResponse getOne(@AuthenticationPrincipal UserDetails user,
                               @PathVariable Long id) {
        Long userId = userId(user);
        return service.getOne(userId, id);
    }

    @Operation(summary = "Update an existing note")
    @PutMapping("/{id}")
    public NoteResponse update(@AuthenticationPrincipal UserDetails user,
                               @PathVariable Long id,
                               @Valid @org.springframework.web.bind.annotation.RequestBody NoteRequest req) {
        Long userId = userId(user);
        return service.update(userId, id, req);
    }

    @Operation(summary = "Soft delete a note")
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> softDelete(@AuthenticationPrincipal UserDetails user,
                                           @PathVariable Long id) {
        Long userId = userId(user);
        service.softDelete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restore a soft-deleted note")
    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@AuthenticationPrincipal UserDetails user,
                                        @PathVariable Long id) {
        Long userId = userId(user);
        service.restore(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search notes with pagination and optional tag filter")
    @GetMapping
    public PagedNotesResponse list(@AuthenticationPrincipal UserDetails user,
                                   @RequestParam(required = false) String query,
                                   @RequestParam(required = false) String tags,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(defaultValue = "updatedAt") String sortBy,
                                   @RequestParam(defaultValue = "DESC") String direction) {
        Long userId = userId(user);
        return service.search(userId, query, tags, page, size, sortBy, direction);
    }
}
