package standardNaast.view.member.overview;

import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.model.MemberCotisationsModel;
import standardNaast.utils.DateUtils;

public class MemberCotisationsController {

	private final ObservableList<MemberCotisationsModel> memberCotisationsList = FXCollections.observableArrayList();

	@FXML
	private TableView<MemberCotisationsModel> memberCotisationsTable;

	@FXML
	private TableColumn<MemberCotisationsModel, LocalDate> annee;

	@FXML
	private TableColumn<MemberCotisationsModel, LocalDate> datePaiement;

	@FXML
	private TableColumn<MemberCotisationsModel, Long> montant;

	@FXML
	public void initialize() {
		this.memberCotisationsTable.setPlaceholder(new Label("Aucune cotisation pour le membre"));
	}

	public void onSelectedMember(final Personne person) {
		final List<PersonneCotisation> personnesCotisations = person.getPersonnesCotisations();
		this.memberCotisationsList.clear();
		for (final PersonneCotisation personneCotisation : personnesCotisations) {
			final MemberCotisationsModel model = new MemberCotisationsModel();
			// model.setAnnee(personneCotisation.getCotisation().getAnneeCotisation());
			model.setDatePaiement(DateUtils.toLocalDate(personneCotisation.getDatePaiement()));
			model.setMontant(personneCotisation.getCotisation().getMontantCotisation().longValue());
			this.memberCotisationsList.add(model);
		}
		this.memberCotisationsTable.setItems(this.memberCotisationsList);
		this.bindProperties();
	}

	private void bindProperties() {
		this.annee.setCellValueFactory(cellData -> cellData.getValue().anneeProperty());
		this.montant.setCellValueFactory(cellData -> cellData.getValue().montantProperty().asObject());
		this.datePaiement.setCellValueFactory(cellData -> cellData.getValue().datePaimentProperty());
	}
}
