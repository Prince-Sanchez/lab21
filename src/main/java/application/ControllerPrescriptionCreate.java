package application;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

import application.model.*;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;

import javax.print.Doc;

@Controller
public class ControllerPrescriptionCreate {

	@Autowired
	PrescriptionRepository prescriptionRepository;

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	DrugRepository drugRepository;


	@Autowired
	SequenceService sequence;

	/*
	 * Doctor requests blank form for new prescription.
	 */
	@GetMapping("/prescription/new")
	public String getPrescriptionForm(Model model) {
		model.addAttribute("prescription", new PrescriptionView());
		return "prescription_create";
	}

	// process data entered on prescription_create form
	@PostMapping("/prescription")
	public String createPrescription(PrescriptionView p, Model model) throws SQLException {
		//Validate Doctor
		Doctor doctor = doctorRepository.findById(p.getDoctorId());
		if(doctor == null){
			model.addAttribute("message", "Doctor not found.");
			model.addAttribute("prescription", p);
			return "prescription_create";
		}
		//Validate Patient
		Patient patient = patientRepository.findById(p.getPatientId());
		if(patient == null){
			model.addAttribute("message", "Patient not found.");
			model.addAttribute("doctor", p);
			return "prescription_create";
		}
		//Validate Drug
		Drug drug = drugRepository.findByName(p.getDrugName());
		if(drug == null){
			model.addAttribute("message","Drug not found.");
			model.addAttribute("drug", p);
		}

		// Get the next sequence for the prescription
		int rxid = sequence.getNextSequence("PRESCRIPTION_SEQUENCE");
		Prescription prescriptionC = new Prescription();
		prescriptionC.setRxid(rxid);
		prescriptionC.setPatientId(p.getPatientId());
		prescriptionC.setDoctorId(p.getDoctorId());
		prescriptionC.setDrugName(p.getDrugName());
		prescriptionC.setQuantity(p.getQuantity());

		prescriptionRepository.insert(prescriptionC);

		p.setRxid(rxid);
		model.addAttribute("message", "Prescription created successfully.");
		model.addAttribute("prescription", p);
		return "prescription_show";
	}
}
