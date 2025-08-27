package com.gca.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrainerCreateDTO {
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 50, message = "First name must be 1-50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be 1-50 characters")
    private String lastName;

    @NotBlank(message = "Specialization cannot be blank")
    private String specialization;
}
