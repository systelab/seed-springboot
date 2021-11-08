package com.systelab.seed.features.allergy.repository;

import com.systelab.seed.features.allergy.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, UUID>, RevisionRepository<Allergy, UUID, Integer> {

    Optional<Allergy> findById(@Param("id") UUID id);
}