package standardNaast.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.dao.SeasonDAO;

@Named("seasonConverter")
public class SeasonConverter implements Converter {

	@Inject
	SeasonDAO seasonDAO;

	public SeasonConverter() {
		System.out.println("Initializing SaisonConverter");
	}

	@Override
	public Object getAsObject(final FacesContext context,
			final UIComponent component, final String value) {
		return this.seasonDAO.getSeasonById(value);
	}

	@Override
	public String getAsString(final FacesContext context,
			final UIComponent component, final Object value) {
		return value.toString();
	}

}
