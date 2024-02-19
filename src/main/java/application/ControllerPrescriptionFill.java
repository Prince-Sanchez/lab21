package application;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import application.model.*;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;

import javax.xml.transform.Result;


@Controller
public class ControllerPrescriptionFill {
	@Autowired
	PrescriptionRepository prescriptionRepository;

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	DrugRepository drugRepository;

	@Autowired
	PharmacyRepository pharmacyRepository;


	@Autowired
	SequenceService sequence;

	/*
	 * Patient requests form to fill prescription.
	 */
	@GetMapping("/prescription/fill")
	public String getfillForm(Model model) {
		model.addAttribute("prescription", new PrescriptionView());
		return "prescription_fill";
	}

	@PostMapping("/prescription/fill")
	public String processFillForm(PrescriptionView p, Model model) {
		// Validating pharmacy name and address
		Pharmacy pharmacy = pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress());
		if (pharmacy != null) {
			System.out.println("Pharmacy found!");
		} else {
			model.addAttribute("message", "Pharmacy not found!");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		// Finding the patient information
		Patient patient = patientRepository.findByLastName(p.getPatientLastName());
		if (patient != null) {
			System.out.println("Patient found!");
		} else {
			model.addAttribute("message", "Patient not found!");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		// Finding the prescription
		Prescription prescription = prescriptionRepository.findByRxid(p.getRxid());
		if (prescription != null) {
			System.out.println("Prescription found!");
			// Access fill list
			ArrayList<Prescription.FillRequest> refills = prescription.getFills();
			if (prescription.getRefills() > 0) {
				prescription.setRefills(prescription.getRefills() - 1); //decrement if there are refills

				//Create new fill request
				Prescription.FillRequest fillRequest = new Prescription.FillRequest();
				fillRequest.setPharmacyID(p.getPharmacyID());
				fillRequest.setDateFilled(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); // Assuming dateFilled is a string in the format "yyyy-MM-dd"
				fillRequest.setCost(p.getCost());

				// Add new fill request to the list
				refills.add(fillRequest);
				// Save the updated prescription object
				prescriptionRepository.save(prescription);
			} else {
				model.addAttribute("message", "There are no refills for this prescription!");
				model.addAttribute("prescription", p);
				return "prescription_fill";
			}
		}else{
				model.addAttribute("message", "Prescription not found!");
				model.addAttribute("prescription", p);
				return "prescription_fill";
		}


		// Get doctor information
		Doctor doctor = doctorRepository.findById(p.getDoctorId());
		if (doctor != null) {
			System.out.println("Doctor found!");
		} else {
			model.addAttribute("message", "Doctor not found!");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}
		model.addAttribute("message", "Refill successful!");
		model.addAttribute("prescription", p);
		return "prescription_show";
	}
}