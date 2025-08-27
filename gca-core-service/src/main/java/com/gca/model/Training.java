package com.gca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "trainings")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "training_name", nullable = false, length = 100)
    private String name;

    @Column(name = "training_date", nullable = false)
    private LocalDate date;

    @Column(name = "training_duration", nullable = false)
    private Long duration;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false)
    private Trainee trainee;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "trainer_id", referencedColumnName = "id", nullable = false)
    private Trainer trainer;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = false)
    private TrainingType type;
}


