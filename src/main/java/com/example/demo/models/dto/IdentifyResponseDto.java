package com.example.demo.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyResponseDto {
    public static final IdentifyResponseDto DEFAULT = new IdentifyResponseDto(ContactDto.DEFAULT);
    ContactDto contact;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactDto {
        static final ContactDto DEFAULT = new ContactDto(0, new String[0], new String[0], new int[0]);
        int primaryContactId;
        String[] emails;
        String[] phoneNumbers;
        int[] secondaryContactIds;
    }
}
