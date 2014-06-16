package standardNaast.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.dao.SaisonDAO;

//@FacesConverter("seasonConverter")
//@Component("seasonConverter")
@Named
public class SaisonConverter implements Converter {

	// @Autowired
	@Inject
	SaisonDAO saisonDAO;

	public SaisonConverter() {
		System.out.println("Initializing SaisonConverter");
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		return this.saisonDAO.getSeasonById(value);
		// return value;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return value.toString();
	}

}
