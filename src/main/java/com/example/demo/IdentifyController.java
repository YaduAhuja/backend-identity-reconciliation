package com.example.demo;

import com.example.demo.models.dto.IdentifyRequestDto;
import com.example.demo.models.dto.IdentifyResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
        return new IdentifyResponseDto();
    }
}
