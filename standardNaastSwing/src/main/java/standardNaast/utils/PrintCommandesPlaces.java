// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PrintCommandesPlaces.java
package standardNaast.utils;

import com.itextpdf.text.BaseColor;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;


import standardNaast.model.AbonnementDB;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PrintCommandesPlaces {

    private String type;

    public static void main(String args1[]) {
        new PrintCommandesPlaces().printCommandeAbonnements("2009-2010");
    }

    public PrintCommandesPlaces() {
        /*this.type = type;
         if (this.type.equals("abonnement")) {
         printCommandeAbonnements(saison);
         }*/
    }

    public String printCommandeAbonnements(String saison) {
        Document pdfDocument = new Document(PageSize.A4.rotate(), -60F, -60F,
                30F, 30F);
        FileOutputStream fo = null;
        File pdfFile = null;
        String pdfPath = null;
        try {
            Image image = Image.getInstance("image_cotisations.PNG");
            image.scalePercent(24F);
            FontFactory.register("comicbd.TTF");
            FontFactory.register("comic.TTF");
            Rectangle r = pdfDocument.getPageSize();
            com.itextpdf.text.Font comic24 = FontFactory.getFont(
                    "ComicSansMS-Bold", "Cp1252", 24F);
            com.itextpdf.text.Font comic16 = FontFactory.getFont(
                    "ComicSansMS-Bold", "Cp1252", 16F);
            pdfPath = "Commande Abonnements-"
                    + new SimpleDateFormat("dd-MM-yyyy").format(new Date())
                    + ".pdf";
            pdfFile = new File(pdfPath);
            fo = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(pdfDocument, fo);
            pdfDocument.open();
            PdfPTable imageTable = new PdfPTable(108);
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

            PdfPTable headerTable = new PdfPTable(108);
            cell = new PdfPCell(
                    new Paragraph("Commande d'abonnements", comic24));
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
            cell = new PdfPCell(new Paragraph("Andrï¿½ Legreve", comic16));
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

            PdfPTable mainTable = new PdfPTable(108);
            setHeadersAbonnement(mainTable);
            mainTable.setHeaderRows(1);

            Vector allAbonnements = new AbonnementDB()
                    .getAllAbonnementsNonCommandes(saison);
            String bonus;
            int total = 0;
            com.itextpdf.text.Font comic12;
            for (Enumeration enumeration = allAbonnements.elements(); enumeration
                    .hasMoreElements();) {
                Vector commandeAbonnement = (Vector) enumeration.nextElement();
                String nom = (String) commandeAbonnement.get(0);
                String prenom = (String) commandeAbonnement.get(1);
                String dateNaissance = (String) commandeAbonnement.get(2);
                String adresse = (String) commandeAbonnement.get(3);
                String carteIdentite = (String) commandeAbonnement.get(4);
                String tarif = "" + (Integer) commandeAbonnement.get(5);
                String bloc = (String) commandeAbonnement.get(6);
                String rang = (String) commandeAbonnement.get(7);
                String place = (String) commandeAbonnement.get(8);
                String montant = "" + (Integer) commandeAbonnement.get(9);

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
            Paragraph p = new Paragraph("");
            pdfDocument.add(p);
            PdfPTable totalTable = new PdfPTable(108);
            comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 10F, 0,
                    BaseColor.BLUE);
            cell = new PdfPCell(new Paragraph("Total: ", comic12));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(5);
            cell.setColspan(103);
            totalTable.addCell(cell);
            comic12 = FontFactory.getFont("ComicSansMS-Bold", "Cp1252", 10F, 0,
                    BaseColor.BLUE);
            cell = new PdfPCell(new Paragraph("" + total + " â‚¬", comic12));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(5);
            cell.setColspan(5);
            totalTable.addCell(cell);
            pdfDocument.add(totalTable);
            pdfDocument.close();
            System.out.println(pdfFile.getAbsolutePath());


        } catch (Exception fnfe) {
            fnfe.printStackTrace();
        }
        return pdfFile.getAbsolutePath();
        // return pdfPath;
    }

    private void setHeadersAbonnement(PdfPTable table) {
        try {
            String headerNames[] = {"Nom", "Prénom", "Date de naissance",
                "Adresse", "NÂ° carte identité", "Tarif", "Bloc", "Rang",
                "Place", "Prix"};
            int colspan[] = {15, 15, 10, 25, 17, 6, 5, 5, 5, 5};
            for (int i = 0; i < 1; i++) {
                for (int j = 0; j < headerNames.length; j++) {
                    com.itextpdf.text.Font comic12 = FontFactory.getFont(
                            "ComicSansMS-Bold", "Cp1252", 12F, 1, BaseColor.BLACK);
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
}