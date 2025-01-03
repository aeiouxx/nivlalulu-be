package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.common.controller.validation.OnCreate;
import com.nivlalulu.nnpro.common.controller.validation.OnUpdate;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.security.ownership.IsOwnedByUser;
import com.nivlalulu.nnpro.service.impl.InvoiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/items")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Invoice item management", description = "Operations related to managing invoice items.")
public class InvoiceItemController {
    private final InvoiceItemService productService;

    @PostMapping
    @Operation(
            summary = "Create an invoice item",
            description = "Creates a new invoice item.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Invoice item to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InvoiceItemDto.class)))
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Invoice item created",
                    content = @Content(schema = @Schema(implementation = InvoiceItemDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceItemDto createInvoiceItem(@AuthenticationPrincipal User user,
                                            @Validated(OnCreate.class) @RequestBody InvoiceItemDto invoiceItemDto) {
        log.info("User '{}' requested to create invoice item: {}", user.getUsername(), invoiceItemDto);
        return productService.createInvoiceItem(invoiceItemDto);
    }

    @Operation(
            summary = "Update an invoice item",
            description = "Updates an existing invoice item."
    )
    @IsOwnedByUser(entityClass = InvoiceItem.class)
    @PutMapping("/{id}")
    public InvoiceItemDto updateInvoiceItem(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody InvoiceItemDto invoiceItemDto) {
        log.info("User '{}' requested to update invoice item with id: {}", user.getUsername(), id);
        return productService.updateInvoiceItem(invoiceItemDto);
    }

    @Operation(
            summary = "Delete an invoice item",
            description = "Deletes an existing invoice item."
    )
    @IsOwnedByUser(entityClass = InvoiceItem.class)
    @DeleteMapping("/{id}")
    public InvoiceItemDto deleteInvoiceItem(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        log.info("User '{}' requested to delete invoice item with id: {}", user.getUsername(), id);
        return productService.deleteInvoiceItem(id);

    }
}
