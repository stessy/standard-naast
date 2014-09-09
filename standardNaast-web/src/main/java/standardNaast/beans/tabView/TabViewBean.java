package standardNaast.beans.tabView;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.TabChangeEvent;

import standardNaast.beans.AbonnementsTableBean;
import standardNaast.beans.CotisationsTableBean;

@Named(value = "tabView")
@RequestScoped
public class TabViewBean {

	@Inject
	private CotisationsTableBean cotisationsTableBean;

	@Inject
	private AbonnementsTableBean abonnementsTableBean;

	public void onTabChange(final TabChangeEvent event) {
		if (event.getTab().getId().equals("cotisationsTab")) {
			this.cotisationsTableBean.fillDropdownYear();
		} else if (event.getTab().getId().equals("abonnementsTab")) {
			this.abonnementsTableBean.fillDropdownSeason();
		}

	}

}
