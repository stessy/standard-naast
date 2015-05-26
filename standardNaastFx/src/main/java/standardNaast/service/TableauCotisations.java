// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TableauCotisations.java
package standardNaast.service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import standardNaast.model.MemberCotisationsModel;
import standardNaast.model.PersonModel;
import standardNaast.model.SeasonModel;
import standardNaast.utils.SeasonUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class TableauCotisations {

	private final PersonneService personneService = new PersonneServiceImpl();;

	private final SeasonService saisonService = new SeasonServiceImpl();

	private final CotisationsService cotisationService = new CotisationsService();

	public static void main(final String args[]) {
		final TableauCotisations tableauCotisations = new TableauCotisations();
		tableauCotisations.creationTableau();
	}

	public String creationTableau() {
		final List<SeasonModel> findAllSaison = this.saisonService.findAllSaison();
		Collections.sort(findAllSaison);
		Collections.reverse(findAllSaison);
		final SeasonModel currentSeason = findAllSaison.get(0);

		final Document pdfDocument = new Document(PageSize.A4.rotate(), -30F, -30F, 10F, 10F);
		FileOutputStream fo = null;
		File pdfFile = null;
		String pdfPath = null;
		try {
			final URL resource = TableauCotisations.class.getResource("image_cotisations.PNG");
			final Image image = Image.getInstance(resource);
			image.scalePercent(24F);
			FontFactory.register("comicbd.ttf");
			FontFactory.register("comic.ttf");
			final com.itextpdf.text.Font comic24 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 24F);
			pdfPath = "Tableau_Cotisations_" + currentSeason.getId() + ".pdf";
			pdfFile = new File(pdfPath);
			fo = new FileOutputStream(pdfFile);
			PdfWriter.getInstance(pdfDocument, fo);
			pdfDocument.open();
			final PdfPTable mainTable = new PdfPTable(108);
			PdfPCell cell = new PdfPCell();
			cell.setColspan(20);
			cell.setBorder(0);
			mainTable.addCell(cell);
			cell = new PdfPCell();
			cell.setColspan(68);
			cell.setImage(image);
			cell.setBorder(0);
			mainTable.addCell(cell);
			cell = new PdfPCell();
			cell.setColspan(20);
			cell.setBorder(0);
			mainTable.addCell(cell);
			final int year = GregorianCalendar.getInstance().get(Calendar.YEAR);
			cell = new PdfPCell(new Paragraph("Cotisations " + year, comic24));
			cell.setColspan(108);
			cell.setBorder(0);
			cell.setHorizontalAlignment(1);
			mainTable.addCell(cell);
			cell = new PdfPCell();
			cell.setColspan(108);
			cell.setBorder(0);
			cell.setFixedHeight(5F);
			cell.setHorizontalAlignment(1);
			mainTable.addCell(cell);
			TableauCotisations.setHeaders(mainTable);
			final List<PersonneCotisationRow> personneCotisationList = this.buildPersonCotisation();
			final int cotisationsSize = personneCotisationList.size();
			final int resteModulo = cotisationsSize % 4;
			for (final PersonneCotisationRow personneCotisation : personneCotisationList) {
				TableauCotisations.writeNumber(mainTable, String.valueOf(personneCotisation.getMemberNumber()));
				final String fullName = MessageFormat.format("{0} {1}", personneCotisation.getName(),
						personneCotisation.getFirstname());
				TableauCotisations.writeName(mainTable, fullName);
				TableauCotisations.writeCotisation(mainTable, personneCotisation.getPaied());
				TableauCotisations.writeBonus(mainTable, String.valueOf(personneCotisation.getBonus()));
			}

			for (int i = 0; i < resteModulo; i++) {
				TableauCotisations.writeNumber(mainTable, "");
				TableauCotisations.writeName(mainTable, "");
				TableauCotisations.writeCotisation(mainTable, "");
				TableauCotisations.writeBonus(mainTable, "");
			}

			pdfDocument.add(mainTable);
			pdfDocument.close();
			JOptionPane.showMessageDialog(null, "Le fichier des cotisations " + currentSeason.getId()
					+ " a été généré.");
		} catch (final Exception fnfe) {
			fnfe.printStackTrace();
		}
		return pdfPath;
	}

	public TableauCotisations() {
	}

	public static void writeNumber(final PdfPTable table, final String value) {
		final com.itextpdf.text.Font comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 12F, 0, new BaseColor(
				0, 128, 0));
		final PdfPCell cell = new PdfPCell(new Paragraph(value, comic12));
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(5);
		cell.setColspan(4);
		table.addCell(cell);
	}

	public static void writeName(final PdfPTable table, final String value) {
		final com.itextpdf.text.Font comic12 = FontFactory
				.getFont("ComicSansMS-Bold", "Cp1252", 8F, 0, BaseColor.BLACK);
		final PdfPCell cell = new PdfPCell(new Paragraph(value, comic12));
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(5);
		cell.setColspan(19);
		table.addCell(cell);
	}

	public static void writeCotisation(final PdfPTable table, final String value) {
		final com.itextpdf.text.Font comic12 = FontFactory
				.getFont("ComicSansMS-Bold", "Cp1252", 12F, 0, BaseColor.BLUE);
		final PdfPCell cell = new PdfPCell(new Paragraph(value, comic12));
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(5);
		cell.setColspan(2);
		table.addCell(cell);
	}

	public static void writeBonus(final PdfPTable table, final String value) {
		final com.itextpdf.text.Font comic12 = FontFactory
				.getFont("ComicSansMS-Bold", "Cp1252", 12F, 0, BaseColor.BLUE);
		final PdfPCell cell = new PdfPCell(new Paragraph(value, comic12));
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(5);
		cell.setColspan(2);
		table.addCell(cell);
	}

	public static void setHeaders(final PdfPTable table) {
		try {
			final String headerNames[] = { "N°", "Noms", "C", "P" };
			final int colspan[] = { 4, 19, 2, 2 };
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < headerNames.length; j++) {
					final com.itextpdf.text.Font comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 12F, 1,
							BaseColor.BLACK);
					final PdfPCell cell = new PdfPCell(new Paragraph(headerNames[j], comic12));
					cell.setColspan(colspan[j]);
					cell.setHorizontalAlignment(1);
					table.addCell(cell);
				}

			}

		} catch (final Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	private List<PersonneCotisationRow> buildPersonCotisation() {
		final List<SeasonModel> seasonList = this.getSeasons();
		final List<PersonModel> personList = this.getMembers();
		final List<PersonneCotisationRow> personCotisationList = new ArrayList<>();
		final LocalDate today = LocalDate.now();
		final SeasonModel latestSeason = seasonList.get(0);
		final LocalDate latestSeasonEndDate = latestSeason.getEndDate();
		final LocalDate latestSeasonStartDate = latestSeason.getStartDate();
		// Si date d'aujourd'hui et égale à l'année de la première
		// saison de la liste, qui correspond à la
		// dernière saison en cours, alors les compteurs sont remis à zéro.
		if (today.isAfter(latestSeasonStartDate) && today.isBefore(latestSeasonEndDate)) {
			for (final PersonModel member : personList) {
				final PersonneCotisationRow personneCotisation = new PersonneCotisationRow();
				personneCotisation.setFirstname(member.getFirstName());
				personneCotisation.setName(member.getName());
				personneCotisation.setMemberNumber(member.getMemberNumber());
				final List<MemberCotisationsModel> memberCotisationList = this.getCotisations(member);
				for (final MemberCotisationsModel memberCotisation : memberCotisationList) {
					if (memberCotisation.getSeason().equals(latestSeason)) {
						// La cotisation a été payée
						personneCotisation.setPaied("P");
					} else {
						personneCotisation.setPaied(StringUtils.EMPTY);
					}
				}
				personCotisationList.add(personneCotisation);
			}
		} else {
			for (final PersonModel member : personList) {
				int bonus = 0;
				final PersonneCotisationRow personneCotisation = new PersonneCotisationRow();
				personneCotisation.setFirstname(member.getFirstName());
				personneCotisation.setName(member.getName());
				personneCotisation.setMemberNumber(member.getMemberNumber());
				final List<MemberCotisationsModel> memberCotisationList = this.getCotisations(member);
				for (final MemberCotisationsModel memberCotisation : memberCotisationList) {
					final LocalDate paymentDate = memberCotisation.getDatePaiement();
					final Calendar paymentDateCalendar = Calendar.getInstance();
					paymentDateCalendar.setTime(paymentDate);
					final int paymentDateYear = paymentDateCalendar.get(Calendar.YEAR);
					for (final SeasonModel season : seasonList) {
						final LocalDate seasonDateStart = season.getStartDate();
						final Calendar seasonDateCalendar = Calendar.getInstance();
						seasonDateCalendar.setTime(seasonDateStart);
						final int seasontDateYear = seasonDateCalendar.get(Calendar.YEAR);
						if (seasontDateYear == paymentDateYear) {
							if (paymentDateYear == TableauCotisations.YEAR) {
								personneCotisation.setPaied("P");
							}
							bonus = bonus + 1;
							break;
						}
					}
				}
				personneCotisation.setBonus(bonus);
				personCotisationList.add(personneCotisation);
			}
		}
		return personCotisationList;
	}

	private List<MemberCotisationsModel> getCotisations(final PersonModel personModel) {
		return this.cotisationService.getMemberCotisations(personModel);

	}

	private List<SeasonModel> getSeasons() {
		final List<SeasonModel> seasonList = this.saisonService.findAllSaison();
		return SeasonUtils.getCotisationsEuropeanSeasons(seasonList);
	}

	private List<PersonModel> getMembers() {
		final List<PersonModel> personneList = this.personneService.findAllPerson(false);
		Collections.sort(personneList);
		return personneList;
	}

	static class PersonneCotisationRow {

		long memberNumber;

		String name;

		String firstname;

		String paied;

		int bonus;

		long getMemberNumber() {
			return this.memberNumber;
		}

		void setMemberNumber(final long memberNumber) {
			this.memberNumber = memberNumber;
		}

		String getName() {
			return this.name;
		}

		void setName(final String name) {
			this.name = name;
		}

		String getFirstname() {
			return this.firstname;
		}

		void setFirstname(final String firstname) {
			this.firstname = firstname;
		}

		String getPaied() {
			return this.paied;
		}

		void setPaied(final String paied) {
			this.paied = paied;
		}

		int getBonus() {
			return this.bonus;
		}

		void setBonus(final int bonus) {
			this.bonus = bonus;
		}

	}

}