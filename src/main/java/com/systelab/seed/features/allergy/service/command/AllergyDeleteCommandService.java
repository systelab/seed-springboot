package com.systelab.seed.features.allergy.service.command;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import com.systelab.seed.features.allergy.service.AllergyNotFoundException;
import com.systelab.seed.features.allergy.service.query.AllergyQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AllergyDeleteCommandService {

    private final AllergyRepository allergyRepository;
    private final AllergyQueryService allergyQueryService;

    public Allergy removeAllergy(UUID id) {
        Allergy allergy=allergyQueryService.getAllergy(id);
        allergyRepository.delete(allergy);
        return allergy;
    }
}
