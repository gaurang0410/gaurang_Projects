package service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PdfInvoiceService {
    public File exportSimplePdf(String invoiceText, File outputFile) throws Exception {
        List<String> lines = new ArrayList<>();
        String[] split = invoiceText.replace("\r", "").split("\n");
        for (String line : split) {
            lines.add(escapePdfText(line));
        }

        StringBuilder content = new StringBuilder();
        content.append("BT\n/F1 10 Tf\n50 780 Td\n");
        boolean first = true;
        for (String line : lines) {
            if (!first) content.append("0 -14 Td\n");
            content.append("(").append(line).append(") Tj\n");
            first = false;
        }
        content.append("ET\n");
        byte[] streamData = content.toString().getBytes(StandardCharsets.UTF_8);

        StringBuilder pdf = new StringBuilder();
        List<Integer> xref = new ArrayList<>();
        pdf.append("%PDF-1.4\n");
        xref.add(pdf.length());
        pdf.append("1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n");
        xref.add(pdf.length());
        pdf.append("2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n");
        xref.add(pdf.length());
        pdf.append("3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>endobj\n");
        xref.add(pdf.length());
        pdf.append("4 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n");
        xref.add(pdf.length());
        pdf.append("5 0 obj<< /Length ").append(streamData.length).append(" >>stream\n");
        int streamStart = pdf.length();
        String prefix = pdf.toString();
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(prefix.getBytes(StandardCharsets.UTF_8));
            fos.write(streamData);
            String suffix = "\nendstream\nendobj\n";
            fos.write(suffix.getBytes(StandardCharsets.UTF_8));

            int xrefPos = streamStart + streamData.length + suffix.length();
            StringBuilder tail = new StringBuilder();
            tail.append("xref\n0 6\n");
            tail.append(String.format("%010d %05d f \n", 0, 65535));
            for (Integer pos : xref) {
                tail.append(String.format("%010d %05d n \n", pos, 0));
            }
            tail.append("trailer<< /Size 6 /Root 1 0 R >>\nstartxref\n").append(xrefPos).append("\n%%EOF");
            fos.write(tail.toString().getBytes(StandardCharsets.UTF_8));
        }
        return outputFile;
    }

    private String escapePdfText(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
