package com.chunhoong.makr;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class WatermarkService {

    public File addWatermark(File pdf, String watermarkText, int fontSize) throws IOException {
        File output = File.createTempFile("output.", ".pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(pdf).setUnethicalReading(true), new PdfWriter(output));
        Document document = new Document(pdfDocument);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Paragraph paragraph = new Paragraph(watermarkText).setFont(font).setFontSize(fontSize);

        PdfExtGState gs1 = new PdfExtGState().setFillOpacity(0.3f);

        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            PdfPage pdfPage = pdfDocument.getPage(i);
            Rectangle rectangle = pdfPage.getPageSize();
            float x = (rectangle.getLeft() + rectangle.getRight()) / 2;
            float y = (rectangle.getTop() + rectangle.getBottom()) / 2;
            PdfCanvas over = new PdfCanvas(pdfPage);
            over.saveState();
            over.setExtGState(gs1);
            document.showTextAligned(paragraph, x, y, i, TextAlignment.CENTER, VerticalAlignment.TOP, calculateAngle(rectangle.getWidth(), rectangle.getHeight()));
            over.restoreState();
        }

        document.close();

        return output;
    }

    private float calculateAngle(float width, float height) {
        return (float) Math.atan(height / width);
    }
}
