package com.gca.automation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TraineeRegistrationRequest {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String address;
}
