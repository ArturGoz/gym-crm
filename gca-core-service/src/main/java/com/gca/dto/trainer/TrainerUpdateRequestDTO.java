package com.gca.dto.trainer;

import com.gca.dto.user.UserGetData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateRequestDTO extends UserGetData {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, max = 50, message = "Username must be 1-50 characters")
    private String username;

    @NotBlank(message = "Specialization cannot be blank")
    private String specialization;
}
