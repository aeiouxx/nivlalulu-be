package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.UserDto;
import com.nivlalulu.nnpro.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserControllerV1 {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUser(@PathVariable Long id) {
        try {
            UserDto userDto = userService.findUserDtoById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), String.format("User id %s found", id), userDto);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        }
    }

}
