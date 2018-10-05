package com.systelab.seed.service;

import feign.RequestLine;
import feign.hystrix.HystrixFeign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

@FeignClient(name = "identity")
interface IdentityClient {

    @RequestLine("GET /identity/v1/medical-record-number")
    public String getMedicalRecordNumber();
}

@Service
public class MedicalRecordNumberService {

    public String getMedicalRecordNumber() {
        IdentityClient client = HystrixFeign.builder().target(IdentityClient.class, "http://localhost:9090",MedicalRecordNumberService::defaultMedicalRecordNumber);
        return client.getMedicalRecordNumber();
    }

    private static String defaultMedicalRecordNumber() {
        return "UNDEFINED";
    }

}