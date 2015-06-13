package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Benevolat;
import standardNaast.entities.Personne;

public interface BenevolatDAO {

	List<Benevolat> getMemberBenevolats(Personne member);

}
