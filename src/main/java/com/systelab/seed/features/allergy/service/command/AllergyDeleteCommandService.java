package com.systelab.seed.features.allergy.service.command;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import com.systelab.seed.features.allergy.service.AllergyNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AllergyDeleteCommandService {

    private final AllergyRepository allergyRepository;

    public Allergy removeAllergy(UUID id) {
        return this.allergyRepository.findById(id)
                .map(existing -> {
                    allergyRepository.delete(existing);
                    return existing;
                }).orElseThrow(() -> new AllergyNotFoundException(id));
    }
}
