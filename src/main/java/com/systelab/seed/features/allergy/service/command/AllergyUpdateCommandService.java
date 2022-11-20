package com.systelab.seed.features.allergy.service.command;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import com.systelab.seed.features.allergy.service.AllergyNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AllergyUpdateCommandService {

    private final AllergyRepository allergyRepository;

    public Allergy updateAllergy(UUID id, Allergy allergy) {
        return this.allergyRepository.findById(id)
                .map(existing -> {
                    allergy.setId(id);
                    return this.allergyRepository.save(allergy);
                }).orElseThrow(() -> new AllergyNotFoundException(id));
    }
}
