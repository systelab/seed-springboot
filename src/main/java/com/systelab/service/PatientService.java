package com.systelab.service;

import com.systelab.model.Patient;

import java.util.ArrayList;
import java.util.List;


public class PatientService {

    public List<Patient> getAllPatients() {
        List<Patient>list=new ArrayList<Patient>();
        return list;
    }

    public Patient getPatient(Long patientId) throws PatientNotFoundException {
        if (patientId==1) throw new PatientNotFoundException("Patient not found");
        return new Patient();
    }

    public void create(Patient patient) {

    }

    public Patient update(Long id, Patient patient) throws PatientNotFoundException {
        return patient;
    }

    public void delete(Long id) throws PatientNotFoundException {

    }
}