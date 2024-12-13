package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.PartyDto;
import com.nivlalulu.nnpro.model.ApiResponse;
import com.nivlalulu.nnpro.service.impl.PartyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/party")
@Validated
public class PartyControllerV1 {

    @Autowired
    private PartyService partyService;

    @PostMapping("/saveParty")
    public ApiResponse<PartyDto> saveParty(@Valid @RequestBody PartyDto partyDto) {
        try {
            PartyDto party = partyService.createParty(partyDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added party", party);

        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PostMapping("/updateParty")
    public ApiResponse<PartyDto> updateParty(@Valid @RequestBody PartyDto partyDto) {
        try {
            PartyDto party = partyService.updateParty(partyDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated party", party);

        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PostMapping("/{id}")
    public ApiResponse<PartyDto> deleteParty(@PathVariable UUID id) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted party", partyService.deleteParty(id));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }
}
