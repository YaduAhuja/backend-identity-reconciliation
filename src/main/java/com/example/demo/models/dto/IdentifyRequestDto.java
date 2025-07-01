package com.example.demo.models.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyRequestDto {
    @Size(min = 5, max = 255, message = "Email length should be between 5 and 255")
    @Email(message = "Email should be valid")
    String email;

    @Size(min = 4, max = 17, message = "Phone number length should be between 4 and 17")
    @Pattern(regexp = "\\+?\\d+", message = "Phone number should contain digits with an optional + in start")
    String phoneNumber;
}
