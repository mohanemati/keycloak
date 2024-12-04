package org.example.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class OtpDto {

    @NotNull(message = "otp is required")
    @Pattern(regexp = "^\\d{4}$",
            message = "otp is not valid!!")
    private String otp;


    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^((\\+98)|0)\\d{10}$",
            message = "Your phone number is not valid!!")
    @Column(unique = true)
    private String phoneNumber;
}
