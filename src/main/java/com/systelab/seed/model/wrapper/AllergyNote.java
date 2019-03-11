package com.systelab.seed.model.wrapper;

import com.systelab.seed.model.allergy.Allergy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllergyNote {
    private Allergy allergy;
    private String note;
}
