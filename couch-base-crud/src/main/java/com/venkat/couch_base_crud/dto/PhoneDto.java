package com.venkat.couch_base_crud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDto {
    @NotBlank(message = "Phone type is required")
    @Pattern(regexp = "^(home|work|mobile)$",
            message = "Phone type must be 'home', 'work', or 'mobile'")
    private String type;

    /*
        International numbers: +1 123-456-7890
        Local numbers: 0123456789, 123 456 7890
        Formatted numbers: 123-456-7890, (123) 456-7890 (if parentheses are added to the regex)
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\s-]{10,}$",
            message = "Invalid phone number format")
    private String number;
}
