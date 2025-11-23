package com.dexwin.notes.note;

import com.dexwin.notes.user.User;
import com.dexwin.notes.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NoteSearchIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        // Clean up just in case
        noteRepository.deleteAll();
        userRepository.deleteAll();

        // Create one test user
        owner = new User();
        owner.setEmail("search@test.com");
        owner.setPassword("dummy"); // not used in this test
        owner.setRole("USER");
        owner = userRepository.save(owner);

        // Seed multiple notes for that user
        // Some match "roadmap" + tag "work", some do not
        for (int i = 1; i <= 12; i++) {
            Note n = new Note();
            n.setOwner(owner);
            n.setTitle("Q4 roadmap item " + i);
            n.setContent("Discuss Q4 roadmap and deliverables " + i);
            // Alternate tags: work, personal
            n.setTags(i % 2 == 0 ? "work,planning" : "personal");
            noteRepository.save(n);
        }

        // A few unrelated notes that should NOT match filter
        for (int i = 1; i <= 3; i++) {
            Note n = new Note();
            n.setOwner(owner);
            n.setTitle("Random note " + i);
            n.setContent("Nothing to do with roadmap");
            n.setTags("other");
            noteRepository.save(n);
        }
    }

    @Test
    void searchByOwnerQueryAndTag_respectsPaginationAndFilters() {
        // page 0, 5 items per page, sorted by updatedAt desc
        Pageable pageable = PageRequest.of(
                0,
                5,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        String query = "roadmap";
        String tag = "work";

        Page<Note> page = noteRepository.searchByOwnerQueryAndTag(
                owner,
                query,
                tag,
                pageable
        );

        // Assert pagination
        assertThat(page.getSize()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);

        // We inserted 12 notes with title containing "roadmap";
        // only half of them have the "work" tag => 6 matching notes.
        assertThat(page.getTotalElements()).isEqualTo(6);
        assertThat(page.getTotalPages()).isEqualTo(2); // 6 / 5 => 2 pages

        // Assert filter: every returned note has "work" in its tags
        // and contains the word "roadmap" in title or content
        assertThat(page.getContent()).allSatisfy(note -> {
            String tags = note.getTags().toLowerCase();
            String title = note.getTitle().toLowerCase();
            String content = note.getContent().toLowerCase();

            assertThat(tags).contains("work");
            assertThat(title.contains("roadmap") || content.contains("roadmap"))
                    .isTrue();
        });
    }
}
