package com.example.demo;

import com.example.demo.models.Contact;
import com.example.demo.models.dto.IdentifyRequestDto;
import com.example.demo.models.dto.IdentifyResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(
        value = "/identify",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class IdentifyController {
    private final IdentifyService identifyService;

    @PostMapping
    IdentifyResponseDto identifyContact(@Valid @RequestBody IdentifyRequestDto request) {
        if (request.getEmail() == null && request.getPhoneNumber() == null)
            return IdentifyResponseDto.DEFAULT;
        Contact contact = new Contact();
        contact.setEmail(request.getEmail());
        contact.setPhoneNumber(request.getPhoneNumber());
        List<Contact> contacts = identifyService.identify(contact);
        return formatServiceResult(contacts);
    }


    private IdentifyResponseDto formatServiceResult(List<Contact> contacts) {
        IdentifyResponseDto.ContactDto contactDto = new IdentifyResponseDto.ContactDto();
        String[] emails = new String[contacts.size()];
        String[] phoneNumbers = new String[contacts.size()];
        int[] secondaryContactIds = new int[contacts.size() - 1];

        contactDto.setPrimaryContactId(contacts.getFirst().getId());
        contactDto.setEmails(emails);
        contactDto.setPhoneNumbers(phoneNumbers);
        contactDto.setSecondaryContactIds(secondaryContactIds);

        emails[0] = contacts.getFirst().getEmail();
        phoneNumbers[0] = contacts.getFirst().getPhoneNumber();

        for (int i = 1; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            emails[i] = contact.getEmail();
            phoneNumbers[i] = contact.getPhoneNumber();
            secondaryContactIds[i-1] = contact.getId();
        }

        return new IdentifyResponseDto(contactDto);
    }
}
