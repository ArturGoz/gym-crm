package com.gca.workloadservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainer_workloads")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@CompoundIndex(def = "{'firstName': 1, 'lastName': 1}", name = "trainer_name_index")
public class TrainerWorkload {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed
    private String firstName;

    @Indexed
    private String lastName;

    private Boolean isActive;

    @Builder.Default
    private List<YearWorkload> years = new ArrayList<>();
}
