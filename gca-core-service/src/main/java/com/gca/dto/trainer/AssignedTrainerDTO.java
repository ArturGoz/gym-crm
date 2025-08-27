package com.gca.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AssignedTrainerDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String specialization;
}