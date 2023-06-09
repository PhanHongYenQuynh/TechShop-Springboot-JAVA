package com.shoptech.admin.brand;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shoptech.admin.AbstractExporter;
import com.shoptech.entity.Brand;
import com.shoptech.entity.Category;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class BrandPdfExporter extends AbstractExporter {
    public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/pdf", ".pdf", "brand");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLACK);

        Paragraph paragraph = new Paragraph("List of Brand", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1.2f, 3.5f, 3.0f});

        writeTableHeader(table);
        writeTableData(table, listBrands);

        document.add(table);
        document.close();
    }

    private void writeTableData(PdfPTable table, List<Brand> listBrands) {
        for (Brand brand: listBrands) {
            table.addCell(String.valueOf(brand.getId()));
            table.addCell(brand.getName());
            table.addCell(brand.getCategories().toString());
        }
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLACK);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Category", font));
        table.addCell(cell);

    }
}
