package standardNaast.resources;

import com.github.rodionmoiseev.c10n.annotations.Fr;

/**
 * Created by stessy on 16/03/2015.
 */
public interface C10NResources {
	@Fr("Numéro de membre")
	String memberNumber();

	@Fr("Nom")
	String nom();

	@Fr("Prénom")
	String prenom();

	@Fr("Adresse")
	String adresse();

	@Fr("Code postal")
	String postalCode();

	@Fr("Ville")
	String city();

	@Fr("N° de téléphone")
	String phoneNumber();

}
