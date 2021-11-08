package com.systelab.seed.features.patient.repository;

import com.systelab.seed.features.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID>, RevisionRepository<Patient, UUID, Integer> {

    Optional<Patient> findById(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("update Patient p set p.active = FALSE where p.modificationTime < ?1")
    int setActiveForUpdatedBefore(ZonedDateTime somedate);

}