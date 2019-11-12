package com.systelab.seed.allergy.service;

import com.systelab.seed.allergy.model.Allergy;
import com.systelab.seed.allergy.repository.AllergyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AllergyService {

    private final AllergyRepository allergyRepository;

    @Autowired
    public AllergyService(AllergyRepository allergyRepository) {
        this.allergyRepository = allergyRepository;
    }

    public Page<Allergy> getAllAllergies(Pageable pageable) {
        final PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "name");
        return this.allergyRepository.findAll(page);
    }

    public Allergy getAllergy(UUID allergyId) {
        return this.allergyRepository.findById(allergyId).orElseThrow(() -> new AllergyNotFoundException(allergyId));
    }

    public Allergy createAllergy(Allergy allergy) {
        return this.allergyRepository.save(allergy);
    }

    public Allergy updateAllergy(UUID id, Allergy allergy) {
        return this.allergyRepository.findById(id)
                .map(existing -> {
                    allergy.setId(id);
                    return this.allergyRepository.save(allergy);
                }).orElseThrow(() -> new AllergyNotFoundException(id));
    }

    public Allergy removeAllergy(UUID id) {
        return this.allergyRepository.findById(id)
                .map(existing -> {
                    allergyRepository.delete(existing);
                    return existing;
                }).orElseThrow(() -> new AllergyNotFoundException(id));
    }
}
