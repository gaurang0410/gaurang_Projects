package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class DatePicker extends JPanel {
    private int month = Calendar.getInstance().get(Calendar.MONTH);
    private int year = Calendar.getInstance().get(Calendar.YEAR);
    private JLabel l = new JLabel("", JLabel.CENTER);
    private String dayStr = "";
    private JDialog d;
    private JButton[] button = new JButton[49];

    public DatePicker(JFrame parent) {
        d = new JDialog(parent, "Select Date", true);
        d.setModal(true);
        String[] header = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        JPanel p1 = new JPanel(new GridLayout(7, 7));
        p1.setPreferredSize(new Dimension(430, 120));

        for (int x = 0; x < button.length; x++) {
            final int selection = x;
            button[x] = new JButton();
            button[x].setFocusPainted(false);
            button[x].setBackground(Color.white);
            if (x > 6) {
                button[x].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        dayStr = button[selection].getActionCommand();
                        d.dispose();
                    }
                });
            }
            if (x < 7) {
                button[x].setText(header[x]);
                button[x].setForeground(Color.red);
            }
            p1.add(button[x]);
        }
        JPanel p2 = new JPanel(new GridLayout(1, 3));
        JButton previous = new JButton("<< Previous");
        previous.addActionListener(e -> {
            month--;
            displayDate();
        });
        p2.add(previous);
        p2.add(l);
        JButton next = new JButton("Next >>");
        next.addActionListener(e -> {
            month++;
            displayDate();
        });
        p2.add(next);
        d.add(p1, BorderLayout.CENTER);
        d.add(p2, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(parent);
        displayDate();
        d.setVisible(true);
    }

    public void displayDate() {
        for (int x = 7; x < button.length; x++) {
            button[x].setText("");
            button[x].setEnabled(false);
            button[x].setBackground(Color.white);
            button[x].setForeground(Color.black);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (int x = 6 + dayOfWeek, day = 1; day <= daysInMonth; x++, day++) {
            button[x].setText("" + day);
            button[x].setEnabled(true);

            Calendar cell = Calendar.getInstance();
            cell.set(year, month, day, 0, 0, 0);
            cell.set(Calendar.MILLISECOND, 0);

            if (cell.before(today)) {
                button[x].setEnabled(false);
                button[x].setForeground(Color.GRAY);
                button[x].setBackground(new Color(245, 245, 245));
            } else {
                button[x].setForeground(Color.BLACK);
                if (dayStr.equals(String.valueOf(day))) {
                    button[x].setBackground(new Color(0, 120, 215));
                    button[x].setForeground(Color.WHITE);
                } else {
                    button[x].setBackground(Color.WHITE);
                }
            }
        }
        l.setText(new java.text.SimpleDateFormat("MMMM yyyy").format(cal.getTime()));
        d.setTitle("Date Picker");
    }

    public String setPickedDate() {
        if (dayStr.equals("")) return dayStr;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, Integer.parseInt(dayStr));
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }
}
