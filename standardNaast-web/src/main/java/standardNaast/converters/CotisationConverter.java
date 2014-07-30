package standardNaast.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Cotisation;
import standardNaast.service.CotisationsService;

@Named
public class CotisationConverter implements Converter {

	@Inject
	CotisationsService cotisationService;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		return this.cotisationService.getCotisationPerYear(Long.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return String.valueOf(((Cotisation) value).getAnneeCotisation());
	}
}
