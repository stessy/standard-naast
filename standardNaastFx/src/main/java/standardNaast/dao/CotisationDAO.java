package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Cotisation;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.entities.Season;

public interface CotisationDAO {

	List<Cotisation> getAllCotisations();

	List<Cotisation> getAllCotisationsPerYear(String year);

	List<PersonneCotisation> getMemberCotisation(Personne member, Season season);

	PersonneCotisation addMemberCotisation(PersonneCotisation cotisation);

	List<PersonneCotisation> getMemberCotisations(Personne member);

}
