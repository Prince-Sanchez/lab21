package application.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepository extends MongoRepository<Patient, Integer> {
	
	Patient findByIdAndLastName(int id, String lastName);
	Patient findById(int id);
	
	Patient findByLastName(String lastName);

	Patient findByIdAndFirstNameAndLastName(int id, String firstName, String lastName);
}
