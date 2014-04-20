package standardNaast.beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.TypePersonne;
import standardNaast.service.TypePersonneService;

@Named(value = "typePersonneBean")
@SessionScoped
public class TypePersonneBean implements Serializable {

	private static final long serialVersionUID = -406292340780597731L;

	private TypePersonne selectedTypePersonne;

	private List<TypePersonne> typePersonnes;

	@Inject
	private TypePersonneService typePersonneService;

	@PostConstruct
	public void init() {
		System.out.println("Init TypePersonneBean");
		List<TypePersonne> typePersonnes = this.typePersonneService
				.findAllTypePersonnes();
		// Collections.sort(typePersonnes);
		this.typePersonnes = typePersonnes;
	}

	public List<TypePersonne> getTypePersonnes() {
		return this.typePersonnes;
	}

	public void setTypePersonnes(List<TypePersonne> typePersonnes) {
		this.typePersonnes = typePersonnes;
	}

	public TypePersonne getSelectedTypePersonne() {
		return this.selectedTypePersonne;
	}

	public void setSelectedTypePersonne(final TypePersonne selectedTypePersonne) {
		this.selectedTypePersonne = selectedTypePersonne;
	}

}
