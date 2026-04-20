package resources.Views.Pages;

import resources.Controllers.BookingController;
import resources.Views.Components.Style;

import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends JPanel {
    private BookingController system;
    private JFrame parent;

    public ReportsPanel(BookingController system, JFrame parent) {
        this.system = system;
        this.parent = parent;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createUI();
    }

    private void createUI() {
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Month (1 or 2):"));

        JTextField txtMonth = new JTextField(5);
        topPanel.add(txtMonth);

        JButton btnLessonReport = new JButton("Lesson Report");
        JButton btnChampionReport = new JButton("Champion Report");

        Style.changeButtonStyle(btnLessonReport);
        Style.changeButtonStyle(btnChampionReport);

        topPanel.add(btnLessonReport);
        topPanel.add(btnChampionReport);

        JTextArea txtReport = new JTextArea();
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReport.setEditable(false);

        btnLessonReport.addActionListener(e -> {
            try {
                int month = Integer.parseInt(txtMonth.getText().trim());
                txtReport.setText(system.generateMonthlyLessonReport(month));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Invalid month.");
            }
        });

        btnChampionReport.addActionListener(e -> {
            try {
                int month = Integer.parseInt(txtMonth.getText().trim());
                txtReport.setText(system.generateMonthlyChampionReport(month));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Invalid month.");
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(txtReport), BorderLayout.CENTER);
    }
}