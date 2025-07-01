package com.example.demo;

import com.example.demo.models.Contact;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends ListCrudRepository<Contact, Integer> {
    @Query("""
       SELECT
            ID,
            PHONE_NUMBER,
            EMAIL
       FROM
            BACKEND.CONTACT
       WHERE
            LINKED_ID = :linkedId OR
            ID = :linkedId
       ORDER BY
            CREATED_AT
       """)
    List<Contact> findAllByLinkedId(int linkedId);

    @Query("""
        SELECT
            ID
        FROM
            BACKEND.CONTACT
        WHERE
            ID IN(
                SELECT
                    COALESCE(LINKED_ID, ID)
                FROM
                    BACKEND.CONTACT
                WHERE
                    PHONE_NUMBER = :phoneNumber OR
                    EMAIL = :email
            )
        ORDER BY
            CREATED_AT
        """
    )
    List<Contact> findPrimaryContactIdByPhoneNumberOrEmail(String phoneNumber, String email);

    @Query("""
           UPDATE BACKEND.CONTACT
                SET
                    LINKED_ID = :newLinkedId,
                    LINK_PRECEDENCE = 'secondary'
                WHERE
                    LINKED_ID IN (:oldLinkedIds) OR
                    ID IN (:oldLinkedIds)
           """)
    @Modifying
    int updateLinkedIdAndLinkPrecedenceByLinkedId(int newLinkedId, List<Integer> oldLinkedIds);

    Optional<Contact> findByPhoneNumber(String phoneNumber);
    Optional<Contact> findByEmail(String email);
    Optional<Contact> findByPhoneNumberAndEmail(String phoneNumber, String email);
}
