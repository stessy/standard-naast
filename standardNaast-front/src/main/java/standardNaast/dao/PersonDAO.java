package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Personne;

public interface PersonDAO {

	void addPerson(Personne person);

	Personne updatePerson(Personne personne);

	Personne getPerson(long id);

	List<Personne> getAllPersons();

}
