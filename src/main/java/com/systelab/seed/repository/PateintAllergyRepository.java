package com.systelab.seed.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import com.systelab.seed.model.patient.PatientAllergy;

@Repository
public interface PateintAllergyRepository extends JpaRepository<PatientAllergy, UUID>, RevisionRepository<PatientAllergy, UUID, Integer> {

}
