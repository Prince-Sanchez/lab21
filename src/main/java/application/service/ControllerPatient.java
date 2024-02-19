package application.service;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import view.PatientView;

import java.sql.*;
import java.util.Optional;

@Controller
public class ControllerPatient {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    SequenceService sequence;

    /*
     * Request blank patient registration form.
     */
    @GetMapping("/patient/new")
    public String getNewPatientForm(Model model) {
        // return blank form for new patient registration
        model.addAttribute("patient", new PatientView());
        return "patient_register";
    }
/*
Patient Registration
 */
    @PostMapping("/patient/new")
    public String createPatient(PatientView p, Model model){
        Doctor doctor = doctorRepository.findByLastName(p.getPrimaryName()); //validate the last name
        if (doctor != null){
            int id = sequence.getNextSequence("PATIENT_SEQUENCE");
            /*
             * insert to patient table
             */
            Patient newPatient = new Patient();
            newPatient.setId(id);
            newPatient.setFirstName(p.getFirstName());
            newPatient.setLastName(p.getLastName());
            newPatient.setBirthdate(p.getBirthdate());
            newPatient.setSsn(p.getSsn());
            newPatient.setStreet(p.getStreet());
            newPatient.setCity(p.getCity());
            newPatient.setState(p.getState());
            newPatient.setZipcode(p.getZipcode());
            newPatient.setPrimaryName(p.getPrimaryName());

            p.setId(id);
            patientRepository.insert(newPatient);
            // display patient data and the generated patient ID,  and success message
            model.addAttribute("message", "Registration successful.");
            model.addAttribute("patient", p);
            return "patient_show";
        }else {
            model.addAttribute("message", "Doctor not found."); // error message if no doctor ID found
            model.addAttribute("patient", p);
            return "patient_register";
        }

    }
    @GetMapping("/patient/edit")
    public String getSearchForm(Model model) {
        model.addAttribute("patient", new PatientView());
        return "patient_get";
    }

    /*
     * Perform search for patient by patient id and name.
     */
    @PostMapping("/patient/show")
    public String showPatient(PatientView p, Model model) {

        Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());

        if (patient != null) {
            p.setFirstName(patient.getFirstName());
            p.setStreet(patient.getStreet());
            p.setCity(patient.getCity());
            p.setState(patient.getState());
            p.setZipcode(patient.getZipcode());
            p.setBirthdate(patient.getBirthdate());
            p.setPrimaryName(patient.getPrimaryName());
            // if found, return "patient_show"
            model.addAttribute("patient", p);
            return "patient_show";
        }else {
            // else return error message and "patient_get"
            model.addAttribute("message", "Patient not found.");
            model.addAttribute("patient", p);// error message if patient not found
            return "patient_get";
        }
    }

    /*
     *  Display patient profile for patient id.
     */
    @GetMapping("/patient/edit/{id}")
    public String getUpdateForm(@PathVariable int id, Model model) {

        Patient patient = patientRepository.findById(id);// search for patient by id
        PatientView p = new PatientView();
        if (patient != null) { // insert patient into p patient view
            p.setId(id);
            p.setFirstName(patient.getFirstName());
            p.setLastName(patient.getLastName());
            p.setStreet(patient.getStreet());
            p.setCity(patient.getCity());
            p.setState(patient.getState());
            p.setZipcode(patient.getZipcode());
            p.setBirthdate(patient.getBirthdate());
            p.setPrimaryName(patient.getPrimaryName());

            model.addAttribute("patient", p);
            return "patient_edit"; // return p model in patient_edit form
        }else {
            model.addAttribute("message", "Patient not found");
            model.addAttribute("patient", p);
            return "index";
        }
    }

    /*
     * Process changes from patient_edit form
     *  Primary doctor, street, city, state, zip can be changed
     *  ssn, patient id, name, birthdate, ssn are read only in template.
     */
    @PostMapping("/patient/edit")
    public String updatePatient(PatientView p, Model model){
        Patient patient = patientRepository.findById(p.getId());

        Doctor doctor = doctorRepository.findByLastName(p.getPrimaryName()); //validate the last name
        if (doctor != null) {
            patient.setFirstName(p.getFirstName());
            patient.setLastName(p.getLastName());
            patient.setStreet(p.getStreet());
            patient.setState(p.getState());
            patient.setCity(p.getCity());
            patient.setZipcode(p.getZipcode());
            patient.setPrimaryName(p.getPrimaryName());
            patient.setBirthdate(p.getBirthdate());
            patient.setId(p.getId());
            patientRepository.save(patient);
            model.addAttribute("message", "Update Successful");
            model.addAttribute("patient", p);
            return "patient_show";
            }
            else {
                model.addAttribute("message", "Doctor not found."); // error message if no doctor ID found
                model.addAttribute("patient", p);
                return "patient_edit"; // returns to edit
            }// End of validation
        }

}// END OF CONTROLLER