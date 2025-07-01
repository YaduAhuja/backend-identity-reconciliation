package com.example.demo;

import com.example.demo.models.Contact;
import com.example.demo.models.Contact.LinkPrecedence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = IdentifyService.class)
class IdentifyServiceTest {

    @MockitoBean
    private ContactRepository contactRepository;

    @Autowired
    private IdentifyService identifyService;

    private Contact contact(String email, String phone, Integer id, Integer linkedId, LinkPrecedence precedence) {
        Contact c = new Contact();
        c.setId(id);
        c.setEmail(email);
        c.setPhoneNumber(phone);
        c.setLinkedId(linkedId);
        c.setLinkPrecedence(precedence);
        return c;
    }

    @Test
    void testReadByEmail_only() {
        Contact input = contact("a@example.com", null, 10, null, null);
        Contact existing = contact("a@example.com", "999", 1, null, LinkPrecedence.PRIMARY);
        when(contactRepository.findByEmail("a@example.com")).thenReturn(Optional.of(existing));
        when(contactRepository.findAllByLinkedId(1)).thenReturn(List.of(existing));

        List<Contact> result = identifyService.identify(input);

        assertEquals(1, result.size());
        verify(contactRepository).findByEmail("a@example.com");
        verify(contactRepository).findAllByLinkedId(1);
    }

    @Test
    void testReadByPhone_only() {
        Contact input = contact(null, "999", 10, null, null);
        Contact existing = contact("a@example.com", "999", 1, 2, LinkPrecedence.SECONDARY);
        when(contactRepository.findByPhoneNumber("999")).thenReturn(Optional.of(existing));
        when(contactRepository.findAllByLinkedId(2)).thenReturn(List.of(existing));

        List<Contact> result = identifyService.identify(input);

        assertEquals(1, result.size());
        verify(contactRepository).findByPhoneNumber("999");
        verify(contactRepository).findAllByLinkedId(2);
    }

    @Test
    void testReadByPhoneAndEmail() {
        Contact input = contact("a@example.com", "999", 10, null, null);
        Contact existing = contact("a@example.com", "999", 3, null, LinkPrecedence.PRIMARY);
        when(contactRepository.findByPhoneNumberAndEmail("999", "a@example.com")).thenReturn(Optional.of(existing));
        when(contactRepository.findAllByLinkedId(3)).thenReturn(List.of(existing));

        List<Contact> result = identifyService.identify(input);

        assertEquals(1, result.size());
        verify(contactRepository).findByPhoneNumberAndEmail("999", "a@example.com");
        verify(contactRepository).findAllByLinkedId(3);
    }

    @Test
    void testNewPrimaryContact() {
        Contact input = contact("a@example.com", "999", 10, null, null);
        when(contactRepository.findByPhoneNumberAndEmail("999", "a@example.com")).thenReturn(Optional.empty());
        when(contactRepository.findPrimaryContactIdByPhoneNumberOrEmail("999", "a@example.com"))
                .thenReturn(List.of());
        Contact saved = contact("a@example.com", "999", 1, null, LinkPrecedence.PRIMARY);
        when(contactRepository.save(any())).thenReturn(saved);

        List<Contact> result = identifyService.identify(input);

        assertEquals(1, result.size());
        assertEquals(LinkPrecedence.PRIMARY, input.getLinkPrecedence());
        verify(contactRepository).save(input);
    }

    @Test
    void testNewSecondaryContact_singlePrimary() {
        Contact input = contact("b@example.com", "111", 10, null, null);
        Contact primary = contact("a@example.com", "999", 1, null, LinkPrecedence.PRIMARY);

        when(contactRepository.findByPhoneNumberAndEmail("111", "b@example.com")).thenReturn(Optional.empty());
        when(contactRepository.findPrimaryContactIdByPhoneNumberOrEmail("111", "b@example.com"))
                .thenReturn(List.of(primary));
        when(contactRepository.findAllByLinkedId(1)).thenReturn(List.of(primary));

        List<Contact> result = identifyService.identify(input);

        assertEquals(1, input.getLinkedId());
        assertEquals(LinkPrecedence.SECONDARY, input.getLinkPrecedence());
        verify(contactRepository).save(input);
    }

    @Test
    void testMergeMultiplePrimaries() {
        Contact input = contact("c@example.com", "222", 10, null, null);
        Contact primary1 = contact("x@example.com", "111", 1, null, LinkPrecedence.PRIMARY);
        Contact primary2 = contact("y@example.com", "222", 2, null, LinkPrecedence.PRIMARY);

        when(contactRepository.findByPhoneNumberAndEmail("222", "c@example.com")).thenReturn(Optional.empty());
        when(contactRepository.findPrimaryContactIdByPhoneNumberOrEmail("222", "c@example.com"))
                .thenReturn(List.of(primary1, primary2));
        when(contactRepository.findAllByLinkedId(1)).thenReturn(List.of(primary1, primary2));

        List<Contact> result = identifyService.identify(input);

        verify(contactRepository).updateLinkedIdAndLinkPrecedenceByLinkedId(1, List.of(2));
        assertEquals(2, result.size());
    }
}
