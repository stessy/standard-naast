package standardNaast.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import standardNaast.dao.SaisonDAO;

//@FacesConverter("seasonConverter")
@Component("seasonConverter")
public class SaisonConverter implements Converter {

	@Autowired
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
