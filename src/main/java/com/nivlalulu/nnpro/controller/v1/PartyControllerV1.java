package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.PartyDto;
import com.nivlalulu.nnpro.model.ApiResponse;
import com.nivlalulu.nnpro.service.impl.PartyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/party")
@Validated
public class PartyControllerV1 {

    @Autowired
    private PartyService partyService;

    @GetMapping("/all")
    public ApiResponse<List<PartyDto>> getParties() {
        return new ApiResponse<>(HttpStatus.OK.value(), "All parties", partyService.findAllParties());
    }

    @GetMapping("/{id}")
    public ApiResponse<PartyDto> getParty(@PathVariable UUID id) {
        try {
            PartyDto partyDto = partyService.findById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), String.format("Party id %s found", id), partyDto);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        }
    }

    @PostMapping("/saveParty")
    public ApiResponse<PartyDto> saveParty(@Valid @RequestBody PartyDto partyDto) {
        try {
            PartyDto party = partyService.createParty(partyDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added party", party);

        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/updateParty")
    public ApiResponse<PartyDto> updateParty(@Valid @RequestBody PartyDto partyDto) {
        try {
            PartyDto party = partyService.updateParty(partyDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated party", party);

        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<PartyDto> deleteParty(@PathVariable UUID id) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted party", partyService.deleteParty(id));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }
}
