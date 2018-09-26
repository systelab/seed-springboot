package com.systelab.seed.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MedicalRecordNumberService {
    @HystrixCommand(fallbackMethod = "defaultMedicalRecordNumber")
    public String getMedicalRecordNumber() {
        return new RestTemplate()
                .getForObject("http://identity:9090/identity/v1/medical-record-number",
                        String.class);
    }

    private String defaultMedicalRecordNumber() {
        return "UNDEFINED";
    }

}