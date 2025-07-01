package com.example.demo;

import com.example.demo.models.Contact;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IdentifyService {
    private final ContactRepository contactRepository;

    @Transactional
    List<Contact> identify(Contact contact) {
//      Check if it is an insert or read
        Optional<Contact> dbContact;

        if (contact.getPhoneNumber() == null)
            dbContact = contactRepository.findByEmail(contact.getEmail());
        else if (contact.getEmail() == null)
            dbContact = contactRepository.findByPhoneNumber(contact.getPhoneNumber());
        else
            dbContact = contactRepository.findByPhoneNumberAndEmail(contact.getPhoneNumber(), contact.getEmail());

//      Read Case
//      Just returning all the values defined by primary contact
        if (dbContact.isPresent()) {
            int linkedId = dbContact.get().getLinkedId() == null ? dbContact.get().getId() : dbContact.get().getLinkedId();
            return contactRepository.findAllByLinkedId(linkedId);
        }

//      Fetch all the primary contacts linked to payload
        List<Contact> primaryContacts = contactRepository.findPrimaryContactIdByPhoneNumberOrEmail(contact.getPhoneNumber(), contact.getEmail());

//      Base write case where a new contact is added and this payload is a primary contact
        if (primaryContacts.isEmpty()) {
            contact.setLinkPrecedence(Contact.LinkPrecedence.PRIMARY);
            return List.of(contactRepository.save(contact));
        }

//      Only one primary contact is present and this payload will be added as a secondary contact
        if (primaryContacts.size() == 1) {
            contact.setLinkedId(primaryContacts.getFirst().getId());
            contact.setLinkPrecedence(Contact.LinkPrecedence.SECONDARY);
            contactRepository.save(contact);
            return contactRepository.findAllByLinkedId(primaryContacts.getFirst().getId());
        }

//      Multiple primary contacts are linked we will need to perform a merge
//      Select the contact which was created at earliest and update all other related contacts with its id
//      Update previous ids to match new linked id and return the dataset
        List<Integer> updateIds = primaryContacts.subList(1, primaryContacts.size()).stream().map(Contact::getId).toList();
        contactRepository.updateLinkedIdAndLinkPrecedenceByLinkedId(primaryContacts.getFirst().getId(), updateIds);
        return contactRepository.findAllByLinkedId(1);
    }
}
