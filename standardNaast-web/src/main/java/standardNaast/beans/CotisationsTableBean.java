package standardNaast.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import standardNaast.entities.Cotisation;
import standardNaast.model.CotisationViewModel;
import standardNaast.model.PersonnesCotisationsModel;
import standardNaast.service.CotisationsService;

@Named(value = "cotisationsTable")
@ViewScoped
public class CotisationsTableBean implements Serializable {

	private static final long serialVersionUID = -4747235749146019196L;

	@Inject
	private CotisationsService cotisationService;

	private List<PersonnesCotisationsModel> paidCotisationsModel;

	private CotisationViewModel cotisationViewModel;

	private List<PersonnesCotisationsModel> unpaidCotisationsModel;

	private List<PersonnesCotisationsModel> unpaidSelectedCotisationsModel;

	private List<PersonnesCotisationsModel> paidSelectedCotisationsModel;

	private List<PersonnesCotisationsModel> filteredUnpaidCotisation;

	private List<Cotisation> cotisations;

	private Map<Long, Cotisation> cotisationsMap;

	private String selectedCotisation;

	private boolean paidCotisationButtonDisabled = true;

	private int totalPaid;

	private int totalUnpaid;

	private long totalAmountPaid;

	public CotisationsTableBean() {
		System.out.println("Instantiating CotisationsTableBean");
	}

	// Business methods

	public void onRowSelect(final SelectEvent event) {
		this.paidCotisationButtonDisabled = false;
	}

	public void onPaidRowSelect(final SelectEvent event) {
		this.paidCotisationButtonDisabled = false;
	}

	public void updateCotisations() {
		this.cotisationViewModel = this.cotisationService.getPaiedCotisationsPerYear(this.getSelectedCotisation());
		this.setPaidCotisationsModel(this.cotisationViewModel.getPaidCotisationsModel());
		this.setUnpaidCotisationsModel(this.cotisationViewModel.getUnpaidCotisationsModel());
		this.setTotalAmountPaid(this.cotisationViewModel.getTotalAmountPaid());
		this.setTotalPaid(this.cotisationViewModel.getTotalPaidCotisations());
		this.setTotalUnpaid(this.cotisationViewModel.getTotalUnpaidCotisations());
	}

	public void printSelected() {

	}

	public void setPaidForSelected() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
		if (CollectionUtils.isEmpty(this.unpaidSelectedCotisationsModel)) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", bundle.getString("COTISATION.UNPAID.NOT.SELECTED")));
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", bundle.getString("COTISATION.UNPAID.NOT.SELECTED")));
		} else {
			this.cotisationService.setCotisationsAsPaid(this.unpaidSelectedCotisationsModel, this.cotisationsMap.get(Long.valueOf(this.selectedCotisation)));
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle.getString("COTISATION.UNPAID.SAVED")));
			this.updateCotisations();
		}
	}

	public void fillDropdownYear() {
		this.cotisations = this.cotisationService.findAllCotisations();
		this.cotisationsMap = new HashMap<>();
		for (Cotisation cotisation : this.cotisations) {
			this.cotisationsMap.put(cotisation.getAnneeCotisation(), cotisation);
		}
	}

	// Getters and setters

	public List<PersonnesCotisationsModel> getPaidCotisationsModel() {
		return this.paidCotisationsModel;
	}

	public void setPaidCotisationsModel(
			final List<PersonnesCotisationsModel> cotisations) {
		this.paidCotisationsModel = cotisations;
	}

	public String getSelectedCotisation() {
		return this.selectedCotisation;
	}

	public void setSelectedCotisation(final String selectedCotisation) {
		this.selectedCotisation = selectedCotisation;
	}

	public List<Cotisation> getCotisations() {
		return this.cotisations;
	}

	public void setCotisations(List<Cotisation> cotisations) {
		this.cotisations = cotisations;
	}

	public boolean isPaidCotisationButtonDisabled() {
		return this.paidCotisationButtonDisabled;
	}

	public void setPaidCotisationButtonDisabled(
			boolean paidCotisationButtonDisabled) {
		this.paidCotisationButtonDisabled = paidCotisationButtonDisabled;
	}

	public List<PersonnesCotisationsModel> getUnpaidCotisationsModel() {
		return this.unpaidCotisationsModel;
	}

	public void setUnpaidCotisationsModel(
			List<PersonnesCotisationsModel> unpaidCotisationsModel) {
		this.unpaidCotisationsModel = unpaidCotisationsModel;
	}

	public int getTotalPaid() {
		return this.totalPaid;
	}

	public void setTotalPaid(int totalPaid) {
		this.totalPaid = totalPaid;
	}

	public int getTotalUnpaid() {
		return this.totalUnpaid;
	}

	public void setTotalUnpaid(int totalUnpaid) {
		this.totalUnpaid = totalUnpaid;
	}

	public long getTotalAmountPaid() {
		return this.totalAmountPaid;
	}

	public void setTotalAmountPaid(long totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}

	public List<PersonnesCotisationsModel> getUnpaidSelectedCotisationsModel() {
		return this.unpaidSelectedCotisationsModel;
	}

	public void setUnpaidSelectedCotisationsModel(
			final List<PersonnesCotisationsModel> unpaidSelectedCotisationsModel) {
		this.unpaidSelectedCotisationsModel = unpaidSelectedCotisationsModel;
	}

	public List<PersonnesCotisationsModel> getPaidSelectedCotisationsModel() {
		return this.paidSelectedCotisationsModel;
	}

	public void setPaidSelectedCotisationsModel(
			final List<PersonnesCotisationsModel> paidSelectedCotisationsModel) {
		this.paidSelectedCotisationsModel = paidSelectedCotisationsModel;
	}

	public List<PersonnesCotisationsModel> getFilteredUnpaidCotisation() {
		return this.filteredUnpaidCotisation;
	}

	public void setFilteredUnpaidCotisation(
			List<PersonnesCotisationsModel> filteredUnpaidCotisation) {
		this.filteredUnpaidCotisation = filteredUnpaidCotisation;
	}

}
