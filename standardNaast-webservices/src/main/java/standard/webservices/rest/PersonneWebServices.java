package standard.webservices.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import standardNaast.dto.PersonDto;
import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;

@Path("/")
@Produces("application/json")
@RequestScoped
public class PersonneWebServices {

	@EJB
	PersonneService personneService;

	@GET
	@Path("/members/{allPersons}")
	public List<PersonDto> getMembers(
			@PathParam("allPersons") final boolean allPersons) {
		final List<Personne> persons = this.personneService
				.findAllPerson(allPersons);
		final List<PersonDto> personsDto = new ArrayList<>();
		for (final Personne personne : persons) {
			final PersonDto person = new PersonDto();
			person.setAddress(personne.getAddress());
			person.setFirstName(personne.getFirstname());
			person.setMemberNumber(personne.getMemberNumber());
			person.setName(personne.getName());
			person.setPersonId(personne.getPersonneId());
			person.setPhoneNumber(personne.getPhone());
			person.setTown(personne.getPostalCode() + " " + personne.getCity());
			personsDto.add(person);
		}
		return personsDto;
	}
}
