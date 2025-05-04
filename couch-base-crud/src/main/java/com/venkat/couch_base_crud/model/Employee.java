package com.venkat.couch_base_crud.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;
    @Field
    @NotBlank(message = "First name is required")
    private String firstName;

    @Field
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Field
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Field
    @NotNull(message = "Address is required")
    @Size(min = 1, message = "At least one address is required")
    private String[] address;
}
