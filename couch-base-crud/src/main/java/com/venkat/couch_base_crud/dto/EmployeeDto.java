package com.venkat.couch_base_crud.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private String id; // Not validated since it's generated

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @NotNull(message = "Address is required")
    @Valid // Ensures AddressDTO validation is triggered
    private AddressDto address;

    @NotNull(message = "Phone list cannot be null")
    @Size(min = 1, message = "At least one phone is required")
    @Valid // Ensures PhoneDTO validation is triggered
    private List<PhoneDto> phones;
}
