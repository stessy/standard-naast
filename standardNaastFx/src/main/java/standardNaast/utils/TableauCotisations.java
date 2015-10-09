// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TableauCotisations.java
package standardNaast.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import standardNaast.model.MemberCotisationsModel;
import standardNaast.model.PersonModel;
import standardNaast.model.SeasonModel;
import standardNaast.service.CotisationsService;
import standardNaast.service.PersonneServiceImpl;
import standardNaast.service.SeasonServiceImpl;

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

	private final CotisationsService cotisationsService = new CotisationsService();

	private AtomicInteger paidCotisationCounter = new AtomicInteger(0);

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
			FontFactory.register("img/comicbd.ttf");
			FontFactory.register("img/comic.ttf");
			final com.itextpdf.text.Font comic24 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 24F);
			pdfPath = "Tableau_Cotisations_" + this.getCurrentSeason() + ".pdf";
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
			cell = new PdfPCell(new Paragraph("Cotisations Saison " + this.getCurrentSeason(), comic24));
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
		final Map<PersonModel, List<MemberCotisationsModel>> personCotisationMap = new HashMap<>();

		personList.stream().forEach(t -> personCotisationMap.put(t, this.cotisationsService.getMemberCotisations(t)));

		for (final Map.Entry<PersonModel, List<MemberCotisationsModel>> entry : personCotisationMap.entrySet()) {
			final PersonModel member = entry.getKey();
			final List<MemberCotisationsModel> memberCotisations = entry.getValue();

			final PersonneCotisationRow personneCotisation = new PersonneCotisationRow();
			personneCotisation.setName(member.getName());
			personneCotisation.setFirstname(member.getFirstName());
			personneCotisation.setMemberNumber(member.getMemberNumber());
			this.paidCotisationCounter = new AtomicInteger(0);
			final SeasonModel currentSeason = this.getCurrentSeason();
			seasonList.remove(currentSeason);
			memberCotisations.stream().forEach(m -> {
				if (currentSeason.equals(m.getSeason())) {
					personneCotisation.setPaied("P");
					this.paidCotisationCounter.incrementAndGet();
				}
			});

			memberCotisations.stream().forEach(m -> {
				if (seasonList.contains(m.getSeason())) {
					this.paidCotisationCounter.incrementAndGet();
				}
			});
			personneCotisation.setBonus(this.paidCotisationCounter.get());
			personCotisationList.add(personneCotisation);
		}

		Collections.sort(personCotisationList);

		return personCotisationList;
	}

	private List<SeasonModel> getSeasons() {
		final List<SeasonModel> seasonList = this.getSaisonService().findAllSaison();
		return SeasonUtils.getCotisationsEuropeanSeasons(seasonList);
	}

	private List<PersonModel> getMembers() {
		final List<PersonModel> personneList = this.getPersonneService().findAllPerson(false);
		final List<PersonModel> memberList = new ArrayList<PersonModel>();
		for (final PersonModel personne : personneList) {
			if (personne.getMemberNumber() < 1000) {
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

	private SeasonModel getCurrentSeason() {
		return this.getSaisonService().getCurrentSeason();
	}

	static class PersonneCotisationRow implements Comparable<PersonneCotisationRow> {

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

		@Override
		public int compareTo(final PersonneCotisationRow o) {
			return this.getMemberNumber() < o.getMemberNumber() ? -1 : 1;
		}

	}

}