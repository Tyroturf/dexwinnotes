package com.dexwin.notes.note;

import com.dexwin.notes.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findByOwnerAndDeletedAtIsNull(User owner, Pageable pageable);

    @Query("""
    select n from Note n
    where n.owner = :owner
      and n.deletedAt is null
      and (
          :query is null
          or lower(n.title) like concat('%', lower(:query), '%')
          or n.content like concat('%', :query, '%')
      )
    """)
    Page<Note> searchByOwnerAndQuery(
            @Param("owner") User owner,
            @Param("query") String query,
            Pageable pageable
    );

    @Query("""
    select n from Note n
    where n.owner = :owner
      and n.deletedAt is null
      and (:tag is null or lower(n.tags) like concat('%', lower(:tag), '%'))
      and (
          :query is null
          or lower(n.title) like concat('%', lower(:query), '%')
          or n.content like concat('%', :query, '%')
      )
    """)
    Page<Note> searchByOwnerQueryAndTag(
            @Param("owner") User owner,
            @Param("query") String query,
            @Param("tag") String tag,
            Pageable pageable
    );
}
