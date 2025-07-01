package com.example.demo.models.dto;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyRequestDto {
    @Max(255)
    String email;
    @Max(15)
    String phoneNumber;
}
