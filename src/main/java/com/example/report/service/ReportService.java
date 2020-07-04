package com.example.report.service;

import com.example.report.model.CellPhone;
import com.example.report.model.CellPhoneUsageByMonth;
import com.example.report.model.MonthData;
import com.example.report.model.ReportData;
import com.example.report.repository.CellPhoneRepository;
import com.example.report.repository.CellPhoneUsageByMonthRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static com.itextpdf.text.html.HtmlTags.FONT;

@Service
public class ReportService {
    @Autowired
    private CellPhoneRepository cellPhoneRepository;

    @Autowired
    private CellPhoneUsageByMonthRepository cellPhoneUsageByMonthRepository;

    public void runCellPhoneUsageReport() throws IOException, DocumentException, PrintException {
        List<CellPhone> companyPhones = cellPhoneRepository.findAll();

        List<ReportData> data = new ArrayList<>();
        for (CellPhone phone : companyPhones) {
            ReportData detail = new ReportData();
            detail.setEmpId(phone.getEmployeeId());
            detail.setName(phone.getEmployeeName());
            detail.setModel(phone.getModel());
            detail.setPurchaseDate(phone.getPurchaseDate());
            List<MonthData> monthlyData = aggregateMonthData(cellPhoneUsageByMonthRepository.findDataByEmpId(phone.getEmployeeId()));
            Collections.sort(monthlyData);
            detail.setMonthlyData(monthlyData);
            data.add(detail);
        }

        buildReport(data);
    }

    private List<MonthData> aggregateMonthData(List<CellPhoneUsageByMonth> monthlyData) {
        HashMap<Integer, MonthData> aggregation = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

        // aggregate the data for the year
        for (CellPhoneUsageByMonth usage : monthlyData) {
            LocalDate date = LocalDate.parse(usage.getWeek(), formatter);
            if (date.getYear() != 2018) continue;
            if (aggregation.containsKey(date.getMonthValue())) {
                MonthData currentData = aggregation.get(date.getMonthValue());
                currentData.setMinutes(currentData.getMinutes() + usage.getTotalMinutes());
                currentData.setData(currentData.getData() + usage.getTotalData());
                aggregation.replace(date.getMonthValue(), currentData);
            } else {
                aggregation.put(date.getMonthValue(), new MonthData(date.getMonthValue(), usage.getTotalMinutes(), usage.getTotalData()));
            }
        }

        // check for months with no data and add empty values if needed
        for (int i = 1; i <= 12; i++) {
            if (!aggregation.containsKey(i)) {
                aggregation.put(i, new MonthData(i, 0, 0));
            }
        }
        return new ArrayList<>(aggregation.values());
    }

    public void buildReport(List<ReportData> data) throws IOException, DocumentException, PrintException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("report.pdf"));
//        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream(1024 * 10);
//        PdfWriter.getInstance(document, pdfStream);

        document.open();

        createHeaderSection(document, data);
        Paragraph p = new Paragraph();
        p.add(Chunk.NEWLINE);
        document.add(p);

        for (ReportData item : data) {
            createDetailPhoneSection(document, item);
            createDetailMonthSection(document);
            PdfPTable table = createDetailDataSection(document);
            createDetailDataRows(table, item.getMonthlyData());
            document.add(table);

            Paragraph p2 = new Paragraph();
            p2.add(Chunk.NEWLINE);
            p2.add(Chunk.NEWLINE);
            document.add(p2);
        }

        document.close();

        printReport();
    }

    private void createHeaderSection(Document document, List<ReportData> data) throws DocumentException {
        Paragraph p = new Paragraph();
        Font font = FontFactory.getFont(FONT, BaseFont.TIMES_ROMAN, BaseFont.EMBEDDED, 11);
        p.setFont(font);

        Chunk c1 = new Chunk("Report Run: 2/2/20");
        p.add(c1);
        p.add(Chunk.NEWLINE);

        Chunk c2 = new Chunk("Number of Phones: " + data.size());
        p.add(c2);
        p.add(Chunk.NEWLINE);

        Integer totalMinutes = data.stream()
                .reduce(0, (subtotal, item) ->
                    subtotal + item.getMonthlyData().stream()
                            .reduce(0, (dataSubtotal, month) -> dataSubtotal + month.getMinutes(), Integer::sum), Integer::sum
                );
        Chunk c3 = new Chunk("Total Minutes: " + totalMinutes);
        p.add(c3);
        p.add(Chunk.NEWLINE);

        Float totalData = data.stream()
                .reduce((float) 0, (subtotal, item) ->
                        subtotal + item.getMonthlyData().stream()
                                .reduce((float) 0, (dataSubtotal, month) -> {
                                    return dataSubtotal + month.getData();
                                }, Float::sum), Float::sum
                );
        Chunk c4 = new Chunk("Total Data: " + new DecimalFormat("#.0").format(totalData));
        p.add(c4);
        p.add(Chunk.NEWLINE);

        Chunk c5 = new Chunk("Average Minutes: " + totalMinutes / 12);
        p.add(c5);
        p.add(Chunk.NEWLINE);

        Chunk c6 = new Chunk("Average Data: " + new DecimalFormat("#.0").format(totalData / 12));
        p.add(c6);
        p.add(Chunk.NEWLINE);

        document.add(p);
    }

    private void createDetailPhoneSection(Document document, ReportData item) throws DocumentException {
        PdfPTable table = new PdfPTable(2);

        table.addCell("EmployeeId: " + item.getEmpId());
        table.addCell("Model: " + item.getModel());
        table.addCell("Employee Name: " + item.getName());
        table.addCell("Purchase Date: " + item.getPurchaseDate());

        document.add(table);
    }

    private void createDetailMonthSection(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(12);
        Font font = FontFactory.getFont(FONT, BaseFont.TIMES_ROMAN, BaseFont.EMBEDDED, 11);

        Stream.of("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(0);
                    Phrase phrase = new Phrase(columnTitle);
                    phrase.setFont(font);
                    header.setPhrase(phrase);
                    table.addCell(header);
                });

        document.add(table);
    }

    private PdfPTable createDetailDataSection(Document document) {
        PdfPTable table = new PdfPTable(24);
        Font font = FontFactory.getFont(FONT, BaseFont.TIMES_ROMAN, BaseFont.EMBEDDED, 6);

        Stream.of("Min", "Data", "Min", "Data", "Min", "Data", "Min", "Data", "Min", "Data", "Min", "Data", "Min",
                "Data", "Min", "Data", "Min", "Data", "Min", "Data", "Min", "Data", "Min", "Data")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(0);
                    Phrase phrase = new Phrase(columnTitle, font);
                    header.setPhrase(phrase);
                    table.addCell(header);
                });
        return table;
    }

    private void createDetailDataRows(PdfPTable table, List<MonthData> monthlyData) {
        Font font = FontFactory.getFont(FONT, BaseFont.TIMES_ROMAN, BaseFont.EMBEDDED, 6);

        for (MonthData monthData : monthlyData) {
            PdfPCell mCell = new PdfPCell();
            mCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mCell.setPhrase(new Phrase(String.valueOf(monthData.getMinutes()), font));
            table.addCell(mCell);

            PdfPCell dCell = new PdfPCell();
            dCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dCell.setPhrase(new Phrase(new DecimalFormat("#.0").format(monthData.getData()), font));
            table.addCell(dCell);
        }
    }

    private void printReport() throws PrintException, IOException {
        PrintService ps = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob job = ps.createPrintJob();
        job.addPrintJobListener(new PrintJobAdapter() {
            public void printDataTransferCompleted(PrintJobEvent event){
                System.out.println("data transfer complete");
            }
            public void printJobNoMoreEvents(PrintJobEvent event){
                System.out.println("received no more events");
            }
        });
        FileInputStream fis = new FileInputStream("report.pdf");
        Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        // Doc doc=new SimpleDoc(fis, DocFlavor.INPUT_STREAM.JPEG, null);
        PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
        attrib.add(new Copies(1));
        job.print(doc, attrib);
    }

}
