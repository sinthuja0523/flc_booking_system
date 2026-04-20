package resources.Views.Pages;

import resources.Controllers.BookingController;
import resources.Models.*;
import resources.Views.Components.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageBookingPanel extends JPanel {
    private BookingController system;
    private JFrame parent;

    public ManageBookingPanel(BookingController system, JFrame parent) {
        this.system = system;
        this.parent = parent;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createUI();
    }

    private void createUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search Bookings By User:"));

        JComboBox<Member> memberCombo = new JComboBox<>();
        for (Member m : system.getAllMembers()) {
            memberCombo.addItem(m);
        }
        topPanel.add(memberCombo);

        JButton btnSearch = new JButton("Find Bookings");
        Style.changeButtonStyle(btnSearch);
        topPanel.add(btnSearch);

        String[] cols = {"Booking ID", "Lesson Info", "Lesson ID", "Status"};
        DefaultTableModel manageModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable manageTable = new JTable(manageModel);

        btnSearch.addActionListener(e -> {
            manageModel.setRowCount(0);
            Member m = (Member) memberCombo.getSelectedItem();
            if (m != null) {
                for (Booking b : system.getAllBookings()) {
                    if (b.getMember().equals(m)) {
                        String lessonInfo = b.getLesson().getExerciseType() + " on " +
                                b.getLesson().getDay() + " " + b.getLesson().getTime();

                        manageModel.addRow(new Object[]{
                                b.getBookingId(),
                                lessonInfo,
                                b.getLesson().getId(),
                                b.getStatus().toString()
                        });
                    }
                }
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCancel = new JButton("Cancel Selected Booking");
        JButton btnChange = new JButton("Change Booking");
        JTextField txtNewLessonId = new JTextField(8);

        Style.changeButtonStyle(btnCancel);
        Style.changeButtonStyle(btnChange);

        bottomPanel.add(btnCancel);
        bottomPanel.add(new JLabel("  ||  Change to New Lesson ID:"));
        bottomPanel.add(txtNewLessonId);
        bottomPanel.add(btnChange);

        btnCancel.addActionListener(e -> {
            int row = manageTable.getSelectedRow();
            if (row >= 0) {
                String bId = (String) manageModel.getValueAt(row, 0);
                String res = system.cancelBooking(bId);
                JOptionPane.showMessageDialog(parent, res);
                btnSearch.doClick();
            } else {
                JOptionPane.showMessageDialog(parent, "Please select a booking from the table first.");
            }
        });

        btnChange.addActionListener(e -> {
            int row = manageTable.getSelectedRow();
            if (row >= 0) {
                String bId = (String) manageModel.getValueAt(row, 0);
                String newLId = txtNewLessonId.getText().trim();
                Lesson newL = system.getLesson(newLId);

                if (newL != null) {
                    String res = system.changeBooking(bId, newL);
                    JOptionPane.showMessageDialog(parent, res);
                    btnSearch.doClick();
                } else {
                    JOptionPane.showMessageDialog(parent, "New Lesson ID is not valid. Find it in the Book Lesson tab.");
                }
            } else {
                JOptionPane.showMessageDialog(parent, "Please select a booking from the table first.");
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(manageTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}