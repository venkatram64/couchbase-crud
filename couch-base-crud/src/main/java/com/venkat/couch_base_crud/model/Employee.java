package com.venkat.couch_base_crud.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Employee {//this is document class
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;

    @Field
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Field
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Field
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @QueryIndexed
    private String email;

    @Field
    @NotNull(message = "Address is required")
    private Address address;

    @Field
    @NotNull(message = "Phone list cannot be null")
    @Size(min = 1, message = "At least one phone is required")
    private List<Phone> phones;
}
