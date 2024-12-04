package org.example.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class RegistrationDto {

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^((\\+98)|0)\\d{10}$",
            message = "Your phone number is not valid!!")
    @Column(unique = true)
    private String phoneNumber;

    @NotNull(message = "National code is required")
    @Pattern(regexp = "^\\d{10}$",
            message = "National code contains 10 number!!")
    @Column(unique = true)
    private String nationalCode;

}
