package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.common.controller.validation.OnCreate;
import com.nivlalulu.nnpro.common.controller.validation.OnUpdate;
import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.security.ownership.IsOwnedByUser;
import com.nivlalulu.nnpro.service.IPartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/party")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Party management", description = "Operations related to managing parties.")
public class PartyControllerV1 {
    private final IPartyService partyService;

    @GetMapping("/all")
    public ApiResponse<List<PartyDto>> getParties() {
        return new ApiResponse<>(HttpStatus.OK.value(), "All parties", partyService.findAllParties());
    }

    @Operation(
            summary = "Retrieve an party",
            description = "Returns the party with the specified customer facing identifier."
    )
    @GetMapping("/{id}")
    @IsOwnedByUser(entityClass = Party.class)
    public PartyDto getParty(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        log.info("User '{}' requested party with id: {}", user.getUsername(), id);
        return partyService.findById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create a party",
            description = "Creates a new party.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Party to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PartyDto.class)))
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Party created",
                    content = @Content(schema = @Schema(implementation = PartyDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public PartyDto saveParty(
            @AuthenticationPrincipal User user,
            @Validated(OnCreate.class) @RequestBody PartyDto partyDto) {
        log.info("User '{}' requested to create party: {}", user.getUsername(), partyDto);
        return partyService.createParty(partyDto, user);
    }

    @Operation(
            summary = "Update a party",
            description = "Updates an existing party."
    )
    @IsOwnedByUser(entityClass = Party.class)
    @PutMapping("/{id}")
    public PartyDto updateParty(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody PartyDto partyDto) {
        log.info("User '{}' requested to update party with id: {}", user.getUsername(), id);
        return partyService.updateParty(partyDto, user);
    }

    @Operation(
            summary = "Delete a party",
            description = "Deletes an existing party."
    )
    @IsOwnedByUser(entityClass = Party.class)
    @DeleteMapping("/{id}")
    public PartyDto deleteParty(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        log.info("User '{}' requested to delete party with id: {}", user.getUsername(), id);
        return partyService.deleteParty(id);

    }
}
