// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TableauCotisations.java
package standardNaast.service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.entities.Season;
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

	private PersonneServiceImpl personneService;

	private SeasonService saisonService;

	private static final int YEAR = GregorianCalendar.getInstance().get(
			Calendar.YEAR);

	public static void main(final String args[]) {
		TableauCotisations tableauCotisations = new TableauCotisations();
		tableauCotisations.creationTableau();
	}

	public String creationTableau() {
		Document pdfDocument = new Document(PageSize.A4.rotate(), -30F, -30F,
				10F, 10F);
		FileOutputStream fo = null;
		File pdfFile = null;
		String pdfPath = null;
		try {
			URL resource = TableauCotisations.class
					.getResource("image_cotisations.PNG");
			Image image = Image.getInstance(resource);
			image.scalePercent(24F);
			FontFactory.register("comicbd.ttf");
			FontFactory.register("comic.ttf");
			com.itextpdf.text.Font comic24 = FontFactory.getFont(
					"ComicSansMS-Bold", "Cp1252", 24F);
			pdfPath = "Tableau_Cotisations_" + TableauCotisations.YEAR + ".pdf";
			pdfFile = new File(pdfPath);
			fo = new FileOutputStream(pdfFile);
			PdfWriter.getInstance(pdfDocument, fo);
			pdfDocument.open();
			PdfPTable mainTable = new PdfPTable(108);
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
			int year = GregorianCalendar.getInstance().get(Calendar.YEAR);
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
			List<PersonneCotisationRow> personneCotisationList = this
					.buildPersonCotisation();
			int cotisationsSize = personneCotisationList.size();
			int resteModulo = cotisationsSize % 4;
			for (PersonneCotisationRow personneCotisation : personneCotisationList) {
				TableauCotisations.writeNumber(mainTable,
						String.valueOf(personneCotisation.getMemberNumber()));
				String fullName = MessageFormat.format("{0} {1}",
						personneCotisation.getName(),
						personneCotisation.getFirstname());
				TableauCotisations.writeName(mainTable, fullName);
				TableauCotisations.writeCotisation(mainTable,
						personneCotisation.getPaied());
				TableauCotisations.writeBonus(mainTable,
						String.valueOf(personneCotisation.getBonus()));
			}

			for (int i = 0; i < resteModulo; i++) {
				TableauCotisations.writeNumber(mainTable, "");
				TableauCotisations.writeName(mainTable, "");
				TableauCotisations.writeCotisation(mainTable, "");
				TableauCotisations.writeBonus(mainTable, "");
			}

			pdfDocument.add(mainTable);
			pdfDocument.close();
			JOptionPane.showMessageDialog(null, "Le fichier des cotisations "
					+ TableauCotisations.YEAR + " a été généré.");
		} catch (Exception fnfe) {
			fnfe.printStackTrace();
		}
		return pdfPath;
	}

	public TableauCotisations() {
	}

	public static void writeNumber(final PdfPTable table, final String value) {
		com.itextpdf.text.Font comic12 = FontFactory.getFont(
				"ComicSansMS-Bold", "Cp1252", 12F, 0, new BaseColor(0, 128, 0));
		PdfPCell cell = new PdfPCell(new Paragraph(value, comic12));
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(5);
		cell.setColspan(4);
		table.addCell(cell);
	}

	public static void writeName(final PdfPTable table, final String value) {
		com.itextpdf.text.Font comic12 = FontFactory.getFont(
				"ComicSansMS-Bold", "Cp1252", 8F, 0, BaseColor.BLACK);
		PdfPCell cell = new PdfPCell(new Paragraph(value, comic12));
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(5);
		cell.setColspan(19);
		table.addCell(cell);
	}

	public static void writeCotisation(final PdfPTable table, final String value) {
		com.itextpdf.text.Font comic12 = FontFactory.getFont(
				"ComicSansMS-Bold", "Cp1252", 12F, 0, BaseColor.BLUE);
		PdfPCell cell = new PdfPCell(new Paragraph(value, comic12));
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(5);
		cell.setColspan(2);
		table.addCell(cell);
	}

	public static void writeBonus(final PdfPTable table, final String value) {
		com.itextpdf.text.Font comic12 = FontFactory.getFont(
				"ComicSansMS-Bold", "Cp1252", 12F, 0, BaseColor.BLUE);
		PdfPCell cell = new PdfPCell(new Paragraph(value, comic12));
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(5);
		cell.setColspan(2);
		table.addCell(cell);
	}

	public static void setHeaders(final PdfPTable table) {
		try {
			String headerNames[] = { "N°", "Noms", "C", "P" };
			int colspan[] = { 4, 19, 2, 2 };
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < headerNames.length; j++) {
					com.itextpdf.text.Font comic12 = FontFactory.getFont(
							"ComicSansMS-Bold", "Cp1252", 12F, 1,
							BaseColor.BLACK);
					PdfPCell cell = new PdfPCell(new Paragraph(headerNames[j],
							comic12));
					cell.setColspan(colspan[j]);
					cell.setHorizontalAlignment(1);
					table.addCell(cell);
				}

			}

		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	private List<PersonneCotisationRow> buildPersonCotisation() {
		Date advantageDate = new DateTime(TableauCotisations.YEAR, 4, 1, 0, 0)
				.toDate();
		List<Season> seasonList = this.getSeasons();
		List<Personne> personList = this.getMembers();
		List<PersonneCotisationRow> personCotisationList = new ArrayList<>();
		Date today = new Date();
		Calendar todayCalendar = GregorianCalendar.getInstance();
		todayCalendar.setTime(today);
		int todayYear = todayCalendar.get(Calendar.YEAR);
		Calendar seasonCalendar = GregorianCalendar.getInstance();
		Season latestSeason = seasonList.get(0);
		seasonCalendar.setTime(latestSeason.getDateEnd());
		int seasonYear = seasonCalendar.get(Calendar.YEAR);
		// Si l'année de la date d'aujourd'hui et égale à l'année de la première
		// saison de la liste, qui correspond à la
		// dernière saison en cours, alors les compteurs sont remis à zéro.
		if (todayYear == seasonYear) {
			for (Personne member : personList) {
				PersonneCotisationRow personneCotisation = new PersonneCotisationRow();
				personneCotisation.setFirstname(member.getFirstname());
				personneCotisation.setName(member.getName());
				personneCotisation.setMemberNumber(member.getMemberNumber());
				List<PersonneCotisation> memberCotisationList = member
						.getPersonnesCotisations();
				for (PersonneCotisation memberCotisation : memberCotisationList) {
					if (memberCotisation.getCotisation().getAnneeCotisation() == todayYear) {
						// La cotisation a été payée
						personneCotisation.setPaied("P");
						personneCotisation.setBonus(1);
						Date paymentDate = memberCotisation.getDatePaiement();
						if (paymentDate.compareTo(advantageDate) == -1) {
							personneCotisation.setBonus(2);
						}
					} else {
						personneCotisation.setPaied(StringUtils.EMPTY);
						personneCotisation.setBonus(0);
					}
				}
				personCotisationList.add(personneCotisation);
			}
		} else {
			for (Personne member : personList) {
				int bonus = 0;
				PersonneCotisationRow personneCotisation = new PersonneCotisationRow();
				personneCotisation.setFirstname(member.getFirstname());
				personneCotisation.setName(member.getName());
				personneCotisation.setMemberNumber(member.getMemberNumber());
				List<PersonneCotisation> memberCotisationList = member
						.getPersonnesCotisations();
				for (PersonneCotisation memberCotisation : memberCotisationList) {
					Date paymentDate = memberCotisation.getDatePaiement();
					Calendar paymentDateCalendar = Calendar.getInstance();
					paymentDateCalendar.setTime(paymentDate);
					int paymentDateYear = paymentDateCalendar
							.get(Calendar.YEAR);
					for (Season season : seasonList) {
						Date seasonDateStart = season.getDateStart();
						Calendar seasonDateCalendar = Calendar.getInstance();
						seasonDateCalendar.setTime(seasonDateStart);
						int seasontDateYear = seasonDateCalendar
								.get(Calendar.YEAR);
						if (seasontDateYear == paymentDateYear) {
							if (paymentDateYear == TableauCotisations.YEAR) {
								personneCotisation.setPaied("P");
							}
							advantageDate = new DateTime(seasontDateYear, 4, 1,
									0, 0).toDate();
							if (paymentDate.compareTo(advantageDate) == -1) {
								bonus = bonus + 2;
							} else {
								bonus = bonus + 1;
							}
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

	private List<Season> getSeasons() {
		List<Season> seasonList = this.getSaisonService().findAllSaison();
		return SeasonUtils.getCotisationsEuropeanSeasons(seasonList);
	}

	private List<Personne> getMembers() {
		List<Personne> personneList = this.getPersonneService().findAllPerson(
				false);
		List<Personne> memberList = new ArrayList<Personne>();
		for (Personne personne : personneList) {
			if (personne.getMemberNumber() < 10000) {
				memberList.add(personne);
			}
		}
		Collections.sort(memberList);
		return memberList;
	}

	private PersonneServiceImpl getPersonneService() {
		if (this.personneService == null) {
			this.personneService = new PersonneServiceImpl();
		}
		return this.personneService;
	}

	private SeasonService getSaisonService() {
		if (this.saisonService == null) {
			this.saisonService = new SeasonService();
		}
		return this.saisonService;
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