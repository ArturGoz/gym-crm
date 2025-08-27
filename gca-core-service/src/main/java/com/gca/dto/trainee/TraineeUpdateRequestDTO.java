package com.gca.dto.trainee;

import com.gca.dto.user.UserGetData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TraineeUpdateRequestDTO extends UserGetData {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, max = 50, message = "Username must be 1-50 characters")
    private String username;

    @PastOrPresent(message = "Date of birth must be in the past or present")
    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;
}
