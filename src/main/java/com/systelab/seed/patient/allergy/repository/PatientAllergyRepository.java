package com.systelab.seed.patient.allergy.repository;

import com.systelab.seed.patient.allergy.model.PatientAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientAllergyRepository extends JpaRepository<PatientAllergy, UUID>, RevisionRepository<PatientAllergy, UUID, Integer> {

    Optional<PatientAllergy> findByPatientIdAndAllergyId(UUID id1, UUID id2);

}
