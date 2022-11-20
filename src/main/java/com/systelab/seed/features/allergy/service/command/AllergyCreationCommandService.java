package com.systelab.seed.features.allergy.service.command;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AllergyCreationCommandService {

    private final AllergyRepository allergyRepository;

    public Allergy createAllergy(Allergy allergy) {
        return this.allergyRepository.save(allergy);
    }

}
