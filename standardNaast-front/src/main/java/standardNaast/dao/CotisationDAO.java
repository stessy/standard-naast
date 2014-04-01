package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Cotisation;

public interface CotisationDAO {

	List<Cotisation> getAllCotisations();

	List<Cotisation> getAllCotisationsPerYear(String year);

}
