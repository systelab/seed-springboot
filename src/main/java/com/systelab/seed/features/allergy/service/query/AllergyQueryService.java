package com.systelab.seed.features.allergy.service.query;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import com.systelab.seed.features.allergy.service.AllergyNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AllergyQueryService {

    private final AllergyRepository allergyRepository;

    public Page<Allergy> getAllAllergies(Pageable pageable) {
        final PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "name");
        return this.allergyRepository.findAll(page);
    }

    public Allergy getAllergy(UUID allergyId) {
        return this.allergyRepository.findById(allergyId).orElseThrow(() -> new AllergyNotFoundException(allergyId));
    }
}
