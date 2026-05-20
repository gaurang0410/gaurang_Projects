package gui;

import dao.AuditLogDAO;
import service.ServiceManager;
import service.VehicleService;
import service.CustomerService;
import service.PdfInvoiceService;
import model.Service;
import model.Vehicle;
import model.Customer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class BillingFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> serviceCombo;
    private JTextArea billArea;
    private JButton generateBtn;
    private JButton printBtn;
    private JButton downloadBtn;
    private JButton generatePdfBtn;
    private JButton clearBtn;
    private ServiceManager serviceManager;
    private CustomerService customerService;
    private java.util.Map<String, Integer> serviceMap;

    public BillingFrame() {
        this(-1);
    }

    public BillingFrame(int serviceId) {
        setTitle("VehicleFlow - Enterprise Billing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 850);
        setLocationRelativeTo(null);

        serviceManager = new ServiceManager();
        customerService = new CustomerService();
        serviceMap = new java.util.HashMap<>();
        
        initComponents();
        
        if (serviceId != -1) {
            for (int i = 0; i < serviceCombo.getItemCount(); i++) {
                String item = serviceCombo.getItemAt(i);
                if (serviceMap.get(item) != null && serviceMap.get(item) == serviceId) {
                    serviceCombo.setSelectedIndex(i);
                    handleGenerateBill();
                    break;
                }
            }
        }
        
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = UITheme.gradientPanel(UITheme.BG_DARK, UITheme.BG_CARD);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(24, 30, 24, 30));
        setContentPane(mainPanel);

        // Header
        JLabel titleLabel = new JLabel("Professional Invoicing");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.ACCENT_CYAN);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        // Top: Combo + Buttons
        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);

        // Selection row
        JPanel selectionRow = new JPanel(new BorderLayout(12, 0));
        selectionRow.setOpaque(false);
        selectionRow.add(UITheme.styledLabel("Select Completed Job:"), BorderLayout.WEST);
        serviceCombo = UITheme.styledComboBox();
        loadServicesToCombo();
        serviceCombo.addActionListener(e -> updateInvoiceActionState());
        selectionRow.add(serviceCombo, BorderLayout.CENTER);
        topSection.add(selectionRow, BorderLayout.NORTH);

        // Buttons - GridLayout ensures equal sizing, no clipping
        JPanel btnPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        
        generateBtn = UITheme.accentButton("Generate Invoice", UITheme.ACCENT_GREEN, UITheme.getIcon("billing", Color.WHITE, 16));
        generateBtn.setToolTipText("Build invoice from selected completed job");
        generateBtn.setPreferredSize(new Dimension(0, 44));
        generateBtn.addActionListener(e -> handleGenerateBill());
        
        generatePdfBtn = UITheme.accentButton("PDF Export", UITheme.ACCENT_CYAN, UITheme.getIcon("reports", Color.WHITE, 16));
        generatePdfBtn.setToolTipText("Create professional PDF invoice");
        generatePdfBtn.setPreferredSize(new Dimension(0, 44));
        generatePdfBtn.addActionListener(e -> generatePdfInvoice());
        
        printBtn = UITheme.accentButton("Print", UITheme.ACCENT_PURPLE, UITheme.getIcon("service", Color.WHITE, 16));
        printBtn.setToolTipText("Print current invoice");
        printBtn.setPreferredSize(new Dimension(0, 44));
        printBtn.addActionListener(e -> handlePrint());

        downloadBtn = UITheme.accentButton("Download", UITheme.ACCENT_BLUE, UITheme.getIcon("billing", Color.WHITE, 16));
        downloadBtn.setToolTipText("Save invoice as text file");
        downloadBtn.setPreferredSize(new Dimension(0, 44));
        downloadBtn.addActionListener(e -> downloadInvoice());
        
        clearBtn = UITheme.accentButton("Reset", UITheme.BORDER_DEFAULT);
        clearBtn.setToolTipText("Clear invoice preview");
        clearBtn.setPreferredSize(new Dimension(0, 44));
        clearBtn.addActionListener(e -> {
            billArea.setText("");
            updateInvoiceActionState();
        });
        
        btnPanel.add(generateBtn);
        btnPanel.add(generatePdfBtn);
        btnPanel.add(printBtn);
        btnPanel.add(downloadBtn);
        btnPanel.add(clearBtn);
        topSection.add(btnPanel, BorderLayout.SOUTH);

        centerPanel.add(topSection, BorderLayout.NORTH);

        // Bill Area
        billArea = new JTextArea();
        billArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        billArea.setBackground(UITheme.BG_INPUT);
        billArea.setForeground(UITheme.TEXT_INPUT);
        billArea.setCaretColor(UITheme.ACCENT_CYAN);
        billArea.setEditable(false);
        billArea.setLineWrap(false);
        billArea.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JScrollPane scrollPane = UITheme.styledScrollPane(billArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        updateInvoiceActionState();

        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void loadServicesToCombo() {
        serviceMap.clear();
        serviceCombo.removeAllItems();
        List<Service> completedServices = serviceManager.getCompletedServices();
        for (Service s : completedServices) {
            String displayText = "Job #" + s.getServiceId() + " [" + s.getServiceType() + "]";
            serviceCombo.addItem(displayText);
            serviceMap.put(displayText, s.getServiceId());
        }

        if (completedServices.isEmpty()) {
            serviceCombo.addItem("No completed services to bill");
        }
        updateInvoiceActionState();
    }

    private void handleGenerateBill() {
        if (serviceCombo.getSelectedItem() == null || serviceCombo.getSelectedItem().toString().startsWith("No completed")) {
            UITheme.showErrorDialog(this, "Selection Error", "Please select a valid job to invoice!");
            return;
        }

        String serviceText = (String) serviceCombo.getSelectedItem();
        Integer serviceId = serviceMap.get(serviceText);

        if (serviceId != null) {
            String bill = generateProfessionalBill(serviceId);
            billArea.setText(bill);
            updateInvoiceActionState();
        } else {
            UITheme.showErrorDialog(this, "Error", "Internal error retrieving job details.");
        }
    }

    private String generateProfessionalBill(int serviceId) {
        Service service = serviceManager.getServiceById(serviceId);
        if (service == null) return "Job not found.";

        VehicleService vService = new VehicleService();
        Vehicle vehicle = vService.getVehicleById(service.getVehicleId());
        Customer customer = null;
        if (vehicle != null) {
            customer = customerService.getCustomerById(vehicle.getCustomerId());
        }
        java.util.List<String> parts = inferPartsForService(service.getServiceType(), service.getCost());
        double partsTotal = service.getCost() * 0.30;
        double laborTotal = service.getCost() - partsTotal;
        double tax = service.getCost() * 0.18;
        double grandTotal = service.getCost() + tax;
        
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------\n");
        sb.append("VehicleFlow Invoice\n");
        sb.append("----------------------------\n");
        sb.append("Invoice ID: ").append(service.getServiceId()).append("\n");
        sb.append("Date: ").append(service.getServiceDate()).append("\n");
        sb.append("----------------------------\n");

        if (customer != null) {
            sb.append("Customer: ").append(customer.getName()).append("\n");
        }
        if (vehicle != null) {
            sb.append("Vehicle: ").append(vehicle.getBrand()).append(" ").append(vehicle.getModel()).append("\n");
            sb.append("Reg No: ").append(vehicle.getRegistrationNumber()).append("\n");
        }
        sb.append("Service: ").append(service.getServiceType()).append("\n");
        sb.append("Status: ").append(service.getStatus()).append("\n");
        sb.append("Parts:\n");
        for (String part : parts) {
            sb.append("  - ").append(part).append("\n");
        }
        sb.append("----------------------------\n");
        sb.append("Labor: ").append(UITheme.formatCurrency(laborTotal)).append("\n");
        sb.append("Parts: ").append(UITheme.formatCurrency(partsTotal)).append("\n");
        sb.append("Sub Total: ").append(UITheme.formatCurrency(service.getCost())).append("\n");
        sb.append("Taxes (18%): ").append(UITheme.formatCurrency(tax)).append("\n");
        sb.append("Grand Total: ").append(UITheme.formatCurrency(grandTotal)).append("\n");
        sb.append("----------------------------\n");
        sb.append("Thank you for choosing VehicleFlow.\n");
        
        return sb.toString();
    }

    private void handlePrint() {
        if (billArea.getText().isEmpty()) {
            UITheme.showInfoDialog(this, "Print Warning", "Nothing to print! Please generate an invoice first.");
            return;
        }

        try {
            billArea.print();
            new AuditLogDAO().log("INVOICE_PRINT", null, "Printed invoice");
        } catch (java.awt.print.PrinterException ex) {
            UITheme.showErrorDialog(this, "System Error", "Printing failed: " + ex.getMessage());
        }
    }

    private java.util.List<String> inferPartsForService(String serviceType, double totalCost) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        String type = serviceType == null ? "" : serviceType.toLowerCase();
        if (type.contains("oil")) {
            parts.add("Engine Oil: " + UITheme.formatCurrency(totalCost * 0.18));
            parts.add("Oil Filter: " + UITheme.formatCurrency(totalCost * 0.12));
        } else if (type.contains("ac")) {
            parts.add("Cabin Filter: " + UITheme.formatCurrency(totalCost * 0.16));
            parts.add("Coolant Top-up: " + UITheme.formatCurrency(totalCost * 0.14));
        } else if (type.contains("brake")) {
            parts.add("Brake Pad Set: " + UITheme.formatCurrency(totalCost * 0.20));
            parts.add("Brake Fluid: " + UITheme.formatCurrency(totalCost * 0.10));
        } else {
            parts.add("General Consumables: " + UITheme.formatCurrency(totalCost * 0.18));
            parts.add("Inspection Kit: " + UITheme.formatCurrency(totalCost * 0.12));
        }
        return parts;
    }

    private void downloadInvoice() {
        if (billArea.getText().isEmpty()) {
            UITheme.showInfoDialog(this, "Info", "Generate invoice first.");
            return;
        }
        int serviceId = -1;
        if (serviceCombo.getSelectedItem() != null) {
            Integer mapped = serviceMap.get(serviceCombo.getSelectedItem().toString());
            if (mapped != null) serviceId = mapped;
        }
        String fileName = "invoice_" + (serviceId > 0 ? serviceId : System.currentTimeMillis()) + ".txt";
        File out = new File(fileName);
        try (PrintWriter writer = new PrintWriter(new FileWriter(out))) {
            writer.print(billArea.getText());
            UITheme.showSuccessDialog(this, "Success", "Invoice saved: " + out.getAbsolutePath());
            new AuditLogDAO().log("INVOICE_TEXT_EXPORT", null, "File: " + out.getAbsolutePath());
        } catch (Exception ex) {
            UITheme.showErrorDialog(this, "Error", "Download failed: " + ex.getMessage());
        }
    }

    private void updateInvoiceActionState() {
        if (serviceCombo == null || generateBtn == null || printBtn == null || downloadBtn == null || billArea == null) {
            return;
        }
        boolean hasSelection = serviceCombo.getSelectedItem() != null
                && !serviceCombo.getSelectedItem().toString().startsWith("No completed");
        boolean hasInvoice = !billArea.getText().trim().isEmpty();
        generateBtn.setEnabled(hasSelection);
        generatePdfBtn.setEnabled(hasInvoice);
        printBtn.setEnabled(hasInvoice);
        downloadBtn.setEnabled(hasInvoice);
    }

    private void generatePdfInvoice() {
        if (billArea.getText().trim().isEmpty()) {
            UITheme.showInfoDialog(this, "Info", "Generate invoice first.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save PDF Invoice");
        chooser.setSelectedFile(new File("invoice_" + System.currentTimeMillis() + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File out = chooser.getSelectedFile();
        if (!out.getName().toLowerCase().endsWith(".pdf")) {
            out = new File(out.getParentFile(), out.getName() + ".pdf");
        }
        try {
            new PdfInvoiceService().exportSimplePdf(billArea.getText(), out);
            UITheme.showSuccessDialog(this, "Success", "PDF generated: " + out.getAbsolutePath());
            new AuditLogDAO().log("INVOICE_PDF_EXPORT", null, "File: " + out.getAbsolutePath());
        } catch (Exception ex) {
            UITheme.showErrorDialog(this, "Error", "PDF generation failed: " + ex.getMessage());
        }
    }
}
