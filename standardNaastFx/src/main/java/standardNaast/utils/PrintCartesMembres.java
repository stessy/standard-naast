// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PrintCartesMembres.java
package standardNaast.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class PrintCartesMembres {

	public static void main(final String args[]) {
		final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		new PrintCartesMembres(df.format(new Date()));
	}

	public PrintCartesMembres(final String date) {
		this.imprimerCartesMembres(date);
	}

	public boolean imprimerCartesMembres(final String date) {
		boolean result = false;
		try {
			// PersonneCotisationDB cotisationDB = new PersonneCotisationDB();
			// Vector data = (Vector) cotisationDB.getPrintCarteMembreInfo()
			// .get(0);
			final Vector data = new Vector();
			for (int i = 0; i < 100; i++) {
				final Vector data1 = new Vector();
				data1.add("128");
				data1.add("Delcroix");
				data1.add("Stessy");
				data1.add("2015-2016");
				data.add(data1);
			}
			if (data.size() == 0) {
				JOptionPane.showMessageDialog(null,
						"Aucune nouvelle carte de membre générée");

			} else {
				final Document pdfDocument = new Document(PageSize.A4);

				final Image image = Image.getInstance("img/carte_membre.jpg");

				image.scalePercent(48F, 48F);
				final PdfWriter writer = PdfWriter.getInstance(pdfDocument,
						new FileOutputStream(new File("Cartes_Membres_" + date
								+ ".pdf")));
				pdfDocument.open();
				int x = 0;
				int y = 0;
				boolean first = true;
				for (int i = 1; i <= data.size(); i++) {
					final Vector membre = (Vector) data.get(i - 1);
					if (i > 1) {
						first = false;
					}
					if (((i) % 12) == 0) {
						pdfDocument.newPage();
						x = 0;
						y = 0;
					}
					x = (((i % 2) == 1) && !first) ? 250 : 0;
					if (((i % 2) == 0) && ((i % 12) != 0)) {
						y = y + 135;
					}

					System.out.println("First? " + first + " X: " + x + " Y: "
							+ y);
					image.setAbsolutePosition(20 + x, 800 - 100 - y);
					pdfDocument.add(image);
					final PdfContentByte cb = writer.getDirectContent();
					final String nom = (String) membre.get(1);
					final String prenom = (String) membre.get(2);
					final String numeroMembre = "" + membre.get(0);
					final String annee = (String) membre.get(3);
					final PdfGraphics2D g2d = (PdfGraphics2D) cb.createGraphics(
							PageSize.A4.getWidth(), PageSize.A4.getHeight());
					g2d.setFont(new java.awt.Font("Verdana Bold Italic",
							java.awt.Font.ITALIC, 8));
					g2d.drawString(nom, 135 + x, 63 + y);
					g2d.drawString(prenom, 146 + x, 75 + y);
					g2d.drawString("Martine Daniel", 156 + x, 87 + y);
					g2d.setFont(new java.awt.Font("Verdana Bold Italic",
							java.awt.Font.ITALIC, 12));
					g2d.setColor(Color.WHITE);
					g2d.drawString(numeroMembre, 42 + x, 138 + y);
					final com.itextpdf.text.Font comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 12F, 0,
							new BaseColor(
									0, 128, 0));
					g2d.setFont(new java.awt.Font("ComicSansMS-Bold",
							java.awt.Font.BOLD, 12));
					// g2d.setFont(new java.awt.Font("Verdana Bold Italic",
					// java.awt.Font.ITALIC, 12));
					// g2d.setColor(Color.WHITE);
					// g2d.drawString(annee, 220 + x, 137 + y);
					// g2d.drawString(annee, 220 + x, 136 + y);
					g2d.drawString(annee, 104 + x, 46 + y);
					g2d.dispose();

				}

				pdfDocument.close();
				result = true;
			}
		} catch (final Exception fnfe) {
			fnfe.printStackTrace();
		}
		return result;

	}
}