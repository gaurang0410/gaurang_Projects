package servlet;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import dao.BookingDAO;
import dao.PaymentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Booking;
import model.Payment;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SessionUtil;

import java.io.IOException;

public class InvoiceServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceServlet.class);
    private BookingDAO bookingDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getLoggedUser(request);

        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
        } catch (NumberFormatException e) {
            logger.error("Invalid booking id: {}", request.getParameter("bookingId"), e);
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Invalid booking id");
            return;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || (!"ADMIN".equals(user.getRole()) && booking.getCustomerId() != user.getId())) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Booking not found");
            return;
        }

        Payment payment = paymentDAO.getLatestPaymentByBookingId(bookingId);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"invoice-" + booking.getInvoiceId() + ".pdf\"");

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
            Font heading = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font small = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);

            // Title
            Paragraph title = new Paragraph("CAR RENTAL SYSTEM - TAX INVOICE\n", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("Invoice No: " + booking.getInvoiceId() + "\n\n", heading);
            subtitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(subtitle);

            document.add(new Paragraph("======================================================", normal));
            
            // Customer Details
            document.add(new Paragraph("CUSTOMER DETAILS", heading));
            document.add(new Paragraph("Name: " + booking.getCustomerName(), normal));
            document.add(new Paragraph("Customer ID: #" + booking.getCustomerId(), normal));
            document.add(new Paragraph("\n"));

            // Booking Details
            document.add(new Paragraph("RESERVATION DETAILS", heading));
            document.add(new Paragraph("Booking ID: #" + booking.getId(), normal));
            document.add(new Paragraph("Vehicle: " + booking.getCarBrand() + " " + booking.getCarModel(), normal));
            document.add(new Paragraph("Pickup: " + booking.getPickupDate() + " at " + booking.getPickupLocation(), normal));
            document.add(new Paragraph("Drop-off: " + booking.getReturnDate() + " at " + booking.getDropLocation(), normal));
            document.add(new Paragraph("Status: " + booking.getStatus(), normal));
            document.add(new Paragraph("\n"));

            // Amount Breakdown
            double rentAmount = booking.getTotalAmount() - booking.getGstAmount();
            document.add(new Paragraph("FINANCIAL BREAKDOWN", heading));
            document.add(new Paragraph(String.format("Base Rental Amount: Rs. %.2f", rentAmount), normal));
            document.add(new Paragraph(String.format("GST (18%%): Rs. %.2f", booking.getGstAmount()), normal));
            document.add(new Paragraph(String.format("Grand Total: Rs. %.2f", booking.getTotalAmount()), new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
            document.add(new Paragraph("\n"));

            // Transaction Details
            document.add(new Paragraph("TRANSACTION DETAILS", heading));
            if (payment != null) {
                document.add(new Paragraph("Payment Method: " + payment.getPaymentMethod(), normal));
                document.add(new Paragraph("Payment Status: " + payment.getStatus(), normal));
                if (payment.getTransactionId() != null) {
                    document.add(new Paragraph("Transaction Ref: " + payment.getTransactionId(), normal));
                }
            } else {
                document.add(new Paragraph("Status: Pending / No transaction data found.", normal));
            }

            document.add(new Paragraph("\n\n======================================================", normal));
            Paragraph footer = new Paragraph("Thank you for choosing us! For support, contact support@carrental.com", small);
            footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            logger.error("Error generating invoice for booking id: {}", bookingId, e);
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Unable to generate invoice PDF");
        }
    }
}
