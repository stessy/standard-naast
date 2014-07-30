package standardNaast.beans.tabView;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.TabChangeEvent;

import standardNaast.beans.CotisationsTableBean;

@Named(value = "tabView")
@RequestScoped
public class TabViewBean {

	@Inject
	private CotisationsTableBean cotisationsTableBean;

	public void onTabChange(TabChangeEvent event) {
		if (event.getTab().getId().equals("cotisationsTab")) {
			this.cotisationsTableBean.fillDropdownYear();
		}

	}

}
