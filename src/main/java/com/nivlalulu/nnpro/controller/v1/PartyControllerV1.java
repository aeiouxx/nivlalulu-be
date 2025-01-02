package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.dto.v1.UserDto;
import com.nivlalulu.nnpro.service.IPartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/party")
@RequiredArgsConstructor
@Validated
public class PartyControllerV1 {
    private final IPartyService partyService;

    @GetMapping("/all")
    public ApiResponse<List<PartyDto>> getParties(@AuthenticationPrincipal UserDto userDto) {
        return new ApiResponse<>(HttpStatus.OK.value(), "All parties", partyService.findAllParties());
    }

    @GetMapping("/userParties")
    public ApiResponse<List<PartyDto>> getUserParties(@AuthenticationPrincipal UserDto userDto) {
        return new ApiResponse<>(HttpStatus.OK.value(), "User parties", partyService.findUserParties(userDto));
    }

    @GetMapping("/{id}")
    public ApiResponse<PartyDto> getParty(@PathVariable UUID id, @AuthenticationPrincipal UserDto userDto) {
        try {
            PartyDto partyDto = partyService.findById(id, userDto);
            return new ApiResponse<>(HttpStatus.OK.value(), String.format("Party id %s found", id), partyDto);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        }
    }

    @PostMapping("/saveParty")
    @PreAuthorize("#partyDto.userId == authentication.principal.id")
    public ApiResponse<PartyDto> saveParty(@Valid @RequestBody PartyDto partyDto, @AuthenticationPrincipal UserDto userDto) {
        try {
            PartyDto party = partyService.createParty(partyDto, userDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added party", party);

        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/updateParty")
    public ApiResponse<PartyDto> updateParty(@Valid @RequestBody PartyDto partyDto, @AuthenticationPrincipal UserDto userDto) {
        try {
            PartyDto party = partyService.updateParty(partyDto, userDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated party", party);

        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<PartyDto> deleteParty(@PathVariable UUID id, @AuthenticationPrincipal UserDto userDto) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted party", partyService.deleteParty(id, userDto));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }
}
