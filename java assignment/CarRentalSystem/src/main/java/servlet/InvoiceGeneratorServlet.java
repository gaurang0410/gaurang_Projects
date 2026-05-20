package servlet;

import dao.BookingDAO;
import dao.PaymentDAO;
import model.Booking;
import model.Payment;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SessionUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceGeneratorServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceGeneratorServlet.class);
    private BookingDAO bookingDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            
            // Get booking details
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                response.sendRedirect(request.getContextPath() + "/admin/payments?error=Booking not found");
                return;
            }

            // Get payment details
            Payment payment = paymentDAO.getLatestPaymentByBookingId(bookingId);
            if (payment == null) {
                response.sendRedirect(request.getContextPath() + "/admin/payments?error=Payment not found");
                return;
            }

            // Generate PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"Invoice_" + booking.getInvoiceId() + ".pdf\"");

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            // Add company header
            Paragraph header = new Paragraph("INVOICE", new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph company = new Paragraph("Car Rental System", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
            company.setAlignment(Element.ALIGN_CENTER);
            document.add(company);

            document.add(new Paragraph("\n"));

            // Invoice details
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);

            addCell(detailsTable, "Invoice ID:", booking.getInvoiceId(), true);
            addCell(detailsTable, "Booking ID:", "#" + booking.getId(), false);
            addCell(detailsTable, "Invoice Date:", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), true);
            addCell(detailsTable, "Payment Date:", payment.getTransactionDate() != null ?
                new SimpleDateFormat("dd/MM/yyyy").format(payment.getTransactionDate()) : "N/A", false);

            document.add(detailsTable);
            document.add(new Paragraph("\n"));

            // Customer and Car details
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            PdfPCell customerHeader = new PdfPCell(new Paragraph("CUSTOMER DETAILS", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
            customerHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            infoTable.addCell(customerHeader);

            PdfPCell carHeader = new PdfPCell(new Paragraph("CAR DETAILS", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
            carHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            infoTable.addCell(carHeader);

            infoTable.addCell(booking.getCustomerName());
            infoTable.addCell(booking.getCarDetails());

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Rental period details
            Paragraph rentalHeader = new Paragraph("RENTAL PERIOD", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD));
            document.add(rentalHeader);

            PdfPTable rentalTable = new PdfPTable(2);
            rentalTable.setWidthPercentage(100);
            addCell(rentalTable, "Pickup Date:", booking.getPickupDate().toString(), true);
            addCell(rentalTable, "Return Date:", booking.getReturnDate().toString(), false);
            addCell(rentalTable, "Pickup Location:", booking.getPickupLocation(), true);
            addCell(rentalTable, "Drop Location:", booking.getDropLocation(), false);
            document.add(rentalTable);

            document.add(new Paragraph("\n"));

            // Amount breakdown
            Paragraph amountHeader = new Paragraph("AMOUNT BREAKDOWN", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD));
            document.add(amountHeader);

            PdfPTable amountTable = new PdfPTable(2);
            amountTable.setWidthPercentage(100);
            amountTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            double baseAmount = booking.getTotalAmount() - booking.getGstAmount();
            addCell(amountTable, "Base Amount:", "Rs. " + String.format("%.2f", baseAmount), true);
            addCell(amountTable, "GST (18%):", "Rs. " + String.format("%.2f", booking.getGstAmount()), false);
            
            PdfPCell totalCell = new PdfPCell(new Paragraph("TOTAL AMOUNT", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
            totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            amountTable.addCell(totalCell);
            
            PdfPCell amountCell = new PdfPCell(new Paragraph("Rs. " + String.format("%.2f", booking.getTotalAmount()),
                new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
            amountCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            amountTable.addCell(amountCell);

            document.add(amountTable);
            document.add(new Paragraph("\n"));

            // Payment information
            Paragraph paymentHeader = new Paragraph("PAYMENT INFORMATION", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD));
            document.add(paymentHeader);

            PdfPTable paymentTable = new PdfPTable(2);
            paymentTable.setWidthPercentage(100);
            addCell(paymentTable, "Payment Method:", payment.getPaymentMethod(), true);
            addCell(paymentTable, "Payment Status:", payment.getStatus(), false);
            document.add(paymentTable);

            document.add(new Paragraph("\n\n"));

            // Footer
            Paragraph footer = new Paragraph("Thank you for using our Car Rental Service!", 
                new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            Paragraph terms = new Paragraph("This is a computer-generated invoice. No signature required.", 
                new Font(Font.FontFamily.HELVETICA, 8));
            terms.setAlignment(Element.ALIGN_CENTER);
            document.add(terms);

            document.close();
            out.flush();
            out.close();

        } catch (NumberFormatException e) {
            logger.error("Invalid booking id: {}", request.getParameter("bookingId"), e);
            response.sendRedirect(request.getContextPath() + "/admin/payments?error=Invalid booking ID");
        } catch (Exception e) {
            logger.error("Error generating invoice for booking id: {}", request.getParameter("bookingId"), e);
            response.sendRedirect(request.getContextPath() + "/admin/payments?error=Error generating invoice");
        }
    }

    private void addCell(PdfPTable table, String label, String value, boolean alternate) {
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        if (alternate) {
            labelCell.setBackgroundColor(new BaseColor(240, 240, 240));
        }
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Paragraph(value, new Font(Font.FontFamily.HELVETICA, 10)));
        if (alternate) {
            valueCell.setBackgroundColor(new BaseColor(240, 240, 240));
        }
        table.addCell(valueCell);
    }
}
