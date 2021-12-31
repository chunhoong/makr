package com.chunhoong.makr;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MergerService {

    public File mergeFiles(List<File> files) throws IOException {
        File mergedFile = File.createTempFile("output", ".pdf");
        List<PdfDocument> pdfDocuments = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            pdfDocuments.add(i == 0 ?
                    new PdfDocument(new PdfReader(file).setUnethicalReading(true), new PdfWriter(mergedFile)) :
                    new PdfDocument(new PdfReader(file).setUnethicalReading(true))
            );
        }

        PdfMerger merger = new PdfMerger(pdfDocuments.get(0));

        for (int i = 1; i < pdfDocuments.size(); i++) {
            PdfDocument pdfDocument = pdfDocuments.get(i);
            merger.merge(pdfDocument, 1, pdfDocument.getNumberOfPages());
            pdfDocument.close();
        }

        pdfDocuments.get(0).close();

        return mergedFile;
    }


}
