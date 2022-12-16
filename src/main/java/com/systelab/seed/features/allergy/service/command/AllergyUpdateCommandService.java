package com.systelab.seed.features.allergy.service.command;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import com.systelab.seed.features.allergy.service.query.AllergyQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AllergyUpdateCommandService {

    private final AllergyRepository allergyRepository;
    private final AllergyQueryService allergyQueryService;

    public Allergy updateAllergy(UUID id, Allergy allergy) {
        Allergy existing=allergyQueryService.getAllergy(id);
        allergy.setId(existing.getId());
        return this.allergyRepository.save(allergy);
    }
}
