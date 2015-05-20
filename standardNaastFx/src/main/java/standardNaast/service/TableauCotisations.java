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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

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

	private SeasonServiceImpl saisonService;

	private static final int YEAR = GregorianCalendar.getInstance().get(Calendar.YEAR);

	public static void main(final String args[]) {
		final TableauCotisations tableauCotisations = new TableauCotisations();
		tableauCotisations.creationTableau();
	}

	public String creationTableau() {
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
			pdfPath = "Tableau_Cotisations_" + TableauCotisations.YEAR + ".pdf";
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
			JOptionPane.showMessageDialog(null, "Le fichier des cotisations " + TableauCotisations.YEAR
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
		LocalDate.of(TableauCotisations.YEAR, 4, 1);
		final List<Season> seasonList = this.getSeasons();
		final List<Personne> personList = this.getMembers();
		final List<PersonneCotisationRow> personCotisationList = new ArrayList<>();
		final Date today = new Date();
		final Calendar todayCalendar = GregorianCalendar.getInstance();
		todayCalendar.setTime(today);
		final int todayYear = todayCalendar.get(Calendar.YEAR);
		final Calendar seasonCalendar = GregorianCalendar.getInstance();
		final Season latestSeason = seasonList.get(0);
		seasonCalendar.setTime(latestSeason.getDateEnd());
		final int seasonYear = seasonCalendar.get(Calendar.YEAR);
		// Si l'année de la date d'aujourd'hui et égale à l'année de la première
		// saison de la liste, qui correspond à la
		// dernière saison en cours, alors les compteurs sont remis à zéro.
		if (todayYear == seasonYear) {
			for (final Personne member : personList) {
				final PersonneCotisationRow personneCotisation = new PersonneCotisationRow();
				personneCotisation.setFirstname(member.getFirstname());
				personneCotisation.setName(member.getName());
				personneCotisation.setMemberNumber(member.getMemberNumber());
				final List<PersonneCotisation> memberCotisationList = member.getPersonnesCotisations();
				for (final PersonneCotisation memberCotisation : memberCotisationList) {
					if (memberCotisation.getCotisation().getAnneeCotisation() == todayYear) {
						// La cotisation a été payée
						personneCotisation.setPaied("P");
						personneCotisation.setBonus(1);

					} else {
						personneCotisation.setPaied(StringUtils.EMPTY);
						personneCotisation.setBonus(0);
					}
				}
				personCotisationList.add(personneCotisation);
			}
		} else {
			for (final Personne member : personList) {
				int bonus = 0;
				final PersonneCotisationRow personneCotisation = new PersonneCotisationRow();
				personneCotisation.setFirstname(member.getFirstname());
				personneCotisation.setName(member.getName());
				personneCotisation.setMemberNumber(member.getMemberNumber());
				final List<PersonneCotisation> memberCotisationList = member.getPersonnesCotisations();
				for (final PersonneCotisation memberCotisation : memberCotisationList) {
					final Date paymentDate = memberCotisation.getDatePaiement();
					final Calendar paymentDateCalendar = Calendar.getInstance();
					paymentDateCalendar.setTime(paymentDate);
					final int paymentDateYear = paymentDateCalendar.get(Calendar.YEAR);
					for (final Season season : seasonList) {
						final Date seasonDateStart = season.getDateStart();
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

	private List<Season> getSeasons() {
		final List<Season> seasonList = this.getSaisonService().findAllSaison();
		return SeasonUtils.getCotisationsEuropeanSeasons(seasonList);
	}

	private List<Personne> getMembers() {
		final List<Personne> personneList = this.getPersonneService().findAllPerson(false);
		final List<Personne> memberList = new ArrayList<Personne>();
		for (final Personne personne : personneList) {
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

	private SeasonServiceImpl getSaisonService() {
		if (this.saisonService == null) {
			this.saisonService = new SeasonServiceImpl();
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