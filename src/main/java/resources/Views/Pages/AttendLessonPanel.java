package resources.Views.Pages;

import resources.Controllers.BookingController;
import resources.Models.*;
import resources.Views.Components.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AttendLessonPanel extends JPanel {
    private BookingController system;
    private JFrame parent;

    public AttendLessonPanel(BookingController system, JFrame parent) {
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

        JButton btnSearch = new JButton("Find Eligible Bookings");
        Style.changeButtonStyle(btnSearch);
        topPanel.add(btnSearch);

        String[] cols = {"Booking ID", "Lesson Info", "Lesson ID", "Status"};
        DefaultTableModel attendModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable attendTable = new JTable(attendModel);

        btnSearch.addActionListener(e -> {
            attendModel.setRowCount(0);
            Member m = (Member) memberCombo.getSelectedItem();

            if (m != null) {
                for (Booking b : system.getAllBookings()) {
                    if (b.getMember().equals(m) &&
                            (b.getStatus() == BookingStatus.BOOKED || b.getStatus() == BookingStatus.CHANGED)) {

                        String lessonInfo = b.getLesson().getExerciseType() + " on " +
                                b.getLesson().getDay() + " " + b.getLesson().getTime();

                        attendModel.addRow(new Object[]{
                                b.getBookingId(),
                                lessonInfo,
                                b.getLesson().getId(),
                                b.getStatus().toString()
                        });
                    }
                }
            }
        });

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblRating = new JLabel("Rating (1-5):");
        JComboBox<Integer> cmbRating = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

        JLabel lblReview = new JLabel("Review text:");
        JTextArea txtReview = new JTextArea(3, 30);

        JButton btnAttend = new JButton("Attend Selected & Submit Review");
        Style.changeButtonStyle(btnAttend);

        btnAttend.addActionListener(e -> {
            int row = attendTable.getSelectedRow();
            if (row >= 0) {
                String bId = (String) attendModel.getValueAt(row, 0);
                int rating = (int) cmbRating.getSelectedItem();
                String review = txtReview.getText().trim();

                if (review.isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "Review text cannot be empty.");
                    return;
                }

                String res = system.attendLesson(bId, rating, review);
                JOptionPane.showMessageDialog(parent, res);

                if (res.startsWith("Success")) {
                    txtReview.setText("");
                    btnSearch.doClick();
                }
            } else {
                JOptionPane.showMessageDialog(parent, "Please select an eligible booking from the table first.");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(lblRating, gbc);

        gbc.gridx = 1;
        bottomPanel.add(cmbRating, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        bottomPanel.add(lblReview, gbc);

        gbc.gridx = 1;
        bottomPanel.add(new JScrollPane(txtReview), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        bottomPanel.add(btnAttend, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(attendTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}