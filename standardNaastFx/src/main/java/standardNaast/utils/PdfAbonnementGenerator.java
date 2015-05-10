// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PrintCommandesPlaces.java
package standardNaast.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import standardNaast.entities.Abonnement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfAbonnementGenerator {

	public static void main(final String args1[]) {
		new PdfAbonnementGenerator().printCommandeAbonnements("2009-2010");
	}

	public static byte[] generatePdfAbonnements(
			final List<Abonnement> abonnementsToPurchase) {

		return null;
	}

	public String printCommandeAbonnements(final String saison) {
		final Document pdfDocument = new Document(PageSize.A4.rotate(), -60F,
				-60F, 30F, 30F);
		FileOutputStream fo = null;
		File pdfFile = null;
		String pdfPath = null;
		try {
			final Image image = Image.getInstance("image_cotisations.PNG");
			image.scalePercent(24F);
			FontFactory.register("comicbd.TTF");
			FontFactory.register("comic.TTF");
			// final Rectangle r = pdfDocument.getPageSize();
			final com.itextpdf.text.Font comic16 = FontFactory.getFont(
					"ComicSansMS-Bold", "Cp1252", 16F);
			pdfPath = "Commande Abonnements-"
					+ new SimpleDateFormat("dd-MM-yyyy").format(new Date())
					+ ".pdf";
			pdfFile = new File(pdfPath);
			fo = new FileOutputStream(pdfFile);
			PdfWriter.getInstance(pdfDocument, fo);
			pdfDocument.open();
			final PdfPTable imageTable = new PdfPTable(108);
			PdfPCell cell = new PdfPCell();
			cell.setColspan(20);
			cell.setBorder(0);
			imageTable.addCell(cell);
			cell = new PdfPCell();
			cell.setColspan(68);
			cell.setImage(image);
			cell.setBorder(0);
			imageTable.addCell(cell);
			cell = new PdfPCell();
			cell.setColspan(20);
			cell.setBorder(0);
			imageTable.addCell(cell);
			pdfDocument.add(imageTable);

			final PdfPTable headerTable = new PdfPTable(108);
			cell = new PdfPCell(new Paragraph("Commande d'abonnements",
					FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 24F)));
			cell.setColspan(108);
			cell.setBorder(0);
			cell.setHorizontalAlignment(1);
			headerTable.addCell(cell);
			cell = new PdfPCell();
			cell.setColspan(108);
			cell.setBorder(0);
			cell.setFixedHeight(5F);
			cell.setHorizontalAlignment(1);
			headerTable.addCell(cell);
			cell = new PdfPCell(new Paragraph(
					"Club de Supporter: Standard de Naast", comic16));
			cell.setColspan(54);
			cell.setBorder(1);
			cell.setHorizontalAlignment(1);
			headerTable.addCell(cell);
			cell = new PdfPCell(new Paragraph("Nom responsable:", comic16));
			cell.setColspan(27);
			cell.setBorder(1);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			headerTable.addCell(cell);
			cell = new PdfPCell(new Paragraph("André Legreve", comic16));
			cell.setColspan(27);
			cell.setBorder(1);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			headerTable.addCell(cell);
			cell = new PdfPCell(new Paragraph("", comic16));
			cell.setColspan(54);
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			headerTable.addCell(cell);
			cell = new PdfPCell(new Paragraph("Tel:", comic16));
			cell.setColspan(27);
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			headerTable.addCell(cell);
			cell = new PdfPCell(new Paragraph("0498/11.64.73", comic16));
			cell.setColspan(27);
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			headerTable.addCell(cell);
			cell = new PdfPCell();
			cell.setColspan(108);
			cell.setBorder(0);
			cell.setFixedHeight(5F);
			cell.setHorizontalAlignment(1);
			headerTable.addCell(cell);
			pdfDocument.add(headerTable);

			final PdfPTable mainTable = new PdfPTable(108);
			this.setHeadersAbonnement(mainTable);
			mainTable.setHeaderRows(1);

			final Vector allAbonnements = new Vector();
			/*
			 * new AbonnementDB() .getAllAbonnementsNonCommandes(saison);
			 */
			final String bonus;
			int total = 0;
			com.itextpdf.text.Font comic12;
			for (final Enumeration enumeration = allAbonnements.elements(); enumeration
					.hasMoreElements();) {
				final Vector commandeAbonnement = (Vector) enumeration
						.nextElement();
				final String nom = (String) commandeAbonnement.get(0);
				final String prenom = (String) commandeAbonnement.get(1);
				final String dateNaissance = (String) commandeAbonnement.get(2);
				final String adresse = (String) commandeAbonnement.get(3);
				final String carteIdentite = (String) commandeAbonnement.get(4);
				final String tarif = "" + commandeAbonnement.get(5);
				final String bloc = (String) commandeAbonnement.get(6);
				final String rang = (String) commandeAbonnement.get(7);
				final String place = (String) commandeAbonnement.get(8);
				final String montant = "" + commandeAbonnement.get(9);

				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(nom, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(15);
				mainTable.addCell(cell);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(prenom, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(15);
				mainTable.addCell(cell);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(dateNaissance, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(10);
				mainTable.addCell(cell);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(adresse, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(25);
				mainTable.addCell(cell);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(carteIdentite, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(17);
				mainTable.addCell(cell);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(tarif, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(6);
				mainTable.addCell(cell);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(bloc, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(5);
				mainTable.addCell(cell);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(rang, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(5);
				mainTable.addCell(cell);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(place, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(5);
				mainTable.addCell(cell);
				total = total + (Integer) commandeAbonnement.get(9);
				comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252",
						10F, 0, BaseColor.BLUE);
				cell = new PdfPCell(new Paragraph(montant, comic12));
				cell.setHorizontalAlignment(1);
				cell.setVerticalAlignment(5);
				cell.setColspan(5);
				mainTable.addCell(cell);

			}
			pdfDocument.add(mainTable);
			final Paragraph p = new Paragraph("");
			pdfDocument.add(p);
			final PdfPTable totalTable = new PdfPTable(108);
			comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 10F, 0,
					BaseColor.BLUE);
			cell = new PdfPCell(new Paragraph("Total: ", comic12));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(5);
			cell.setColspan(103);
			totalTable.addCell(cell);
			comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 10F, 0,
					BaseColor.BLUE);
			cell = new PdfPCell(new Paragraph("" + total + " €", comic12));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(5);
			cell.setColspan(5);
			totalTable.addCell(cell);
			pdfDocument.add(totalTable);
			pdfDocument.close();
			System.out.println(pdfFile.getAbsolutePath());

		} catch (final Exception fnfe) {
			fnfe.printStackTrace();
		}
		return pdfFile.getAbsolutePath();
		// return pdfPath;
	}

	private void addRow() {

	}

	private void setHeadersAbonnement(final PdfPTable table) {
		try {
			final String headerNames[] = { "Nom", "Prénom",
					"Date de naissance", "Adresse", "N° carte identité",
					"Tarif", "Bloc", "Rang", "Place", "Prix" };
			final int colspan[] = { 15, 15, 10, 25, 17, 6, 5, 5, 5, 5 };
			for (int i = 0; i < 1; i++) {
				for (int j = 0; j < headerNames.length; j++) {
					final com.itextpdf.text.Font comic12 = FontFactory.getFont(
							"ComicSansMS-Bold", "Cp1252", 12F, 1,
							BaseColor.BLACK);
					final PdfPCell cell = new PdfPCell(new Paragraph(
							headerNames[j], comic12));
					cell.setColspan(colspan[j]);
					cell.setHorizontalAlignment(1);
					table.addCell(cell);
				}

			}

		} catch (final Exception e) {
			throw new ExceptionConverter(e);
		}
	}
}