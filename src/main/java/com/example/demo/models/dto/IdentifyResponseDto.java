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
    ContactDto contact;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactDto {
        int primaryContactId;
        String[] emails;
        String[] phoneNumbers;
        int[] secondaryContactIds;
    }
}
