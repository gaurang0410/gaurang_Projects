package gui;

import service.ServiceManager;
import model.Service;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BillingFrame extends JFrame {
    private JComboBox<String> serviceCombo;
    private JTextArea billArea;
    private JButton generateBtn;
    private JButton printBtn;
    private JButton clearBtn;
    private ServiceManager serviceManager;
    private java.util.Map<String, Integer> serviceMap;

    public BillingFrame() {
        setTitle("Billing System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        serviceManager = new ServiceManager();
        serviceMap = new java.util.HashMap<>();
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 245, 250));

        // Title
        JLabel titleLabel = new JLabel("Generate Bill");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(200, 10, 200, 30);
        mainPanel.add(titleLabel);

        // Service Selection
        JLabel serviceLabel = new JLabel("Select Service:");
        serviceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        serviceLabel.setBounds(30, 60, 100, 25);
        mainPanel.add(serviceLabel);

        serviceCombo = new JComboBox<>();
        serviceCombo.setBounds(150, 60, 400, 25);
        loadServicesToCombo();
        mainPanel.add(serviceCombo);

        // Generate Button
        generateBtn = new JButton("Generate Bill");
        generateBtn.setBounds(150, 100, 120, 30);
        generateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleGenerateBill();
            }
        });
        mainPanel.add(generateBtn);

        // Print Button
        printBtn = new JButton("Print");
        printBtn.setBounds(280, 100, 100, 30);
        printBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePrint();
            }
        });
        mainPanel.add(printBtn);

        // Clear Button
        clearBtn = new JButton("Clear");
        clearBtn.setBounds(390, 100, 100, 30);
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                billArea.setText("");
            }
        });
        mainPanel.add(clearBtn);

        // Bill Area
        JLabel billLabel = new JLabel("Bill Details:");
        billLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        billLabel.setBounds(30, 145, 100, 25);
        mainPanel.add(billLabel);

        billArea = new JTextArea();
        billArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        billArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(billArea);
        scrollPane.setBounds(30, 175, 540, 380);
        mainPanel.add(scrollPane);

        add(mainPanel);
    }

    private void loadServicesToCombo() {
        List<Service> completedServices = serviceManager.getCompletedServices();
        for (Service s : completedServices) {
            String displayText = "Service #" + s.getServiceId() + " - " + s.getServiceType();
            serviceCombo.addItem(displayText);
            serviceMap.put(displayText, s.getServiceId());
        }

        if (completedServices.isEmpty()) {
            serviceCombo.addItem("No completed services available");
        }
    }

    private void handleGenerateBill() {
        if (serviceCombo.getSelectedItem() == null || serviceCombo.getSelectedItem().toString().equals("No completed services available")) {
            JOptionPane.showMessageDialog(this, "Please select a valid service!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String serviceText = (String) serviceCombo.getSelectedItem();
        Integer serviceId = serviceMap.get(serviceText);

        if (serviceId != null) {
            String bill = serviceManager.generateBillInfo(serviceId);
            billArea.setText(bill);
        } else {
            JOptionPane.showMessageDialog(this, "Error generating bill!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePrint() {
        if (billArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bill to print! Generate a bill first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            billArea.print();
        } catch (java.awt.print.PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Error printing bill: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
