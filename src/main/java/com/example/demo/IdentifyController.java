package com.example.demo;

import com.example.demo.models.dto.IdentifyRequestDto;
import com.example.demo.models.dto.IdentifyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identify")
@RequiredArgsConstructor
public class IdentifyController {
    private final IdentifyService identifyService;

    @PostMapping
    IdentifyResponseDto identifyContact(@Validated @RequestBody IdentifyRequestDto request) {
        if (request.getEmail() == null && request.getPhoneNumber() == null)
            return new IdentifyResponseDto(new IdentifyResponseDto.ContactDto());
        return null;
    }
}
