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

	private CotisationViewModel cotisationViewModel;

	private List<PersonnesCotisationsModel> paidCotisationsCardSent;

	private List<PersonnesCotisationsModel> paidCotisationsCardNotSent;

	private List<PersonnesCotisationsModel> unpaidCotisations;

	private List<PersonnesCotisationsModel> unpaidSelectedCotisations;

	private List<PersonnesCotisationsModel> paidSelectedCotisationsCardNotSent;

	private List<PersonnesCotisationsModel> filteredUnpaidCotisation;

	private List<Cotisation> cotisations;

	private Map<Long, Cotisation> cotisationsMap;

	private String selectedCotisation;

	private int totalPaid;

	private int totalUnpaid;

	private long totalAmountPaid;

	public CotisationsTableBean() {
		System.out.println("Instantiating CotisationsTableBean");
	}

	// Business methods

	public void updateCotisations() {
		this.cotisationViewModel = this.cotisationService
				.getPaiedCotisationsPerYear(this.getSelectedCotisation());
		this.setPaidCotisationsCardSent(this.cotisationViewModel
				.getPaidCotisationsMemberCardSent());
		this.setPaidCotisationsCardNotSent(this.cotisationViewModel
				.getPaidCotisationsMemberCardNotSent());
		this.setUnpaidCotisations(this.cotisationViewModel
				.getUnpaidCotisationsModel());
		this.setTotalAmountPaid(this.cotisationViewModel.getTotalAmountPaid());
		this.setTotalPaid(this.cotisationViewModel.getTotalPaidCotisations());
		this.setTotalUnpaid(this.cotisationViewModel
				.getTotalUnpaidCotisations());
	}

	public void setPaidForSelected() {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		final ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
		if (CollectionUtils.isEmpty(this.unpaidSelectedCotisations)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", bundle
							.getString("COTISATION.UNPAID.NOT.SELECTED")));
			RequestContext.getCurrentInstance().showMessageInDialog(
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", bundle
							.getString("COTISATION.UNPAID.NOT.SELECTED")));
		} else {
			this.cotisationService.setCotisationsAsPaid(
					this.unpaidSelectedCotisations, this.cotisationsMap
					.get(Long.valueOf(this.selectedCotisation)));
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle
							.getString("COTISATION.UNPAID.SAVED")));
			this.updateCotisations();
		}
	}

	public void setSentForSelected() {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		final ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
		if (CollectionUtils.isEmpty(this.paidSelectedCotisationsCardNotSent)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", bundle
							.getString("COTISATION.UNPAID.NOT.SELECTED")));
			RequestContext.getCurrentInstance().showMessageInDialog(
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", bundle
							.getString("COTISATION.UNPAID.NOT.SELECTED")));
		} else {
			this.cotisationService.setMemberCardAsSent(
					this.paidSelectedCotisationsCardNotSent,
					this.cotisationsMap.get(Long
							.valueOf(this.selectedCotisation)));
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", bundle
							.getString("COTISATION.UNPAID.SAVED")));
			this.updateCotisations();
		}
	}

	public void fillDropdownYear() {
		this.cotisations = this.cotisationService.findAllCotisations();
		this.cotisationsMap = new HashMap<>();
		for (final Cotisation cotisation : this.cotisations) {
			this.cotisationsMap
			.put(cotisation.getAnneeCotisation(), cotisation);
		}
	}

	// Getters and setters

	public List<PersonnesCotisationsModel> getPaidCotisationsCardSent() {
		return this.paidCotisationsCardSent;
	}

	public void setPaidCotisationsCardSent(
			final List<PersonnesCotisationsModel> paidCotisationsCardSent) {
		this.paidCotisationsCardSent = paidCotisationsCardSent;
	}

	public List<PersonnesCotisationsModel> getPaidCotisationsCardNotSent() {
		return this.paidCotisationsCardNotSent;
	}

	public void setPaidCotisationsCardNotSent(
			final List<PersonnesCotisationsModel> paidCotisationsCardNotSent) {
		this.paidCotisationsCardNotSent = paidCotisationsCardNotSent;
	}

	public List<PersonnesCotisationsModel> getUnpaidCotisations() {
		return this.unpaidCotisations;
	}

	public void setUnpaidCotisations(
			final List<PersonnesCotisationsModel> unpaidCotisations) {
		this.unpaidCotisations = unpaidCotisations;
	}

	public List<PersonnesCotisationsModel> getPaidSelectedCotisationsCardNotSent() {
		return this.paidSelectedCotisationsCardNotSent;
	}

	public void setPaidSelectedCotisationsCardNotSent(
			final List<PersonnesCotisationsModel> paidSelectedCotisationsCardNotSent) {
		this.paidSelectedCotisationsCardNotSent = paidSelectedCotisationsCardNotSent;
	}

	public List<PersonnesCotisationsModel> getFilteredUnpaidCotisation() {
		return this.filteredUnpaidCotisation;
	}

	public void setFilteredUnpaidCotisation(
			final List<PersonnesCotisationsModel> filteredUnpaidCotisation) {
		this.filteredUnpaidCotisation = filteredUnpaidCotisation;
	}

	public int getTotalPaid() {
		return this.totalPaid;
	}

	public void setTotalPaid(final int totalPaid) {
		this.totalPaid = totalPaid;
	}

	public long getTotalAmountPaid() {
		return this.totalAmountPaid;
	}

	public void setTotalAmountPaid(final long totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}

	public int getTotalUnpaid() {
		return this.totalUnpaid;
	}

	public void setTotalUnpaid(final int totalUnpaid) {
		this.totalUnpaid = totalUnpaid;
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

	public void setCotisations(final List<Cotisation> cotisations) {
		this.cotisations = cotisations;
	}

	public List<PersonnesCotisationsModel> getUnpaidSelectedCotisations() {
		return this.unpaidSelectedCotisations;
	}

	public void setUnpaidSelectedCotisations(
			final List<PersonnesCotisationsModel> unpaidSelectedCotisations) {
		this.unpaidSelectedCotisations = unpaidSelectedCotisations;
	}

}
