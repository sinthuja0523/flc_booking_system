package resources.Views.Layout;

import resources.Controllers.BookingController;
import resources.Models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Layout extends JFrame {
    private BookingController system;
    private DefaultTableModel timetableModel;
    private JTable timetableTable;

    public Layout(BookingController system) {
        this.system = system;
        setTitle("Furzefield Leisure Centre (FLC) Booking System");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Book Lesson", createBookingPanel());
        tabbedPane.addTab("Manage Booking", createManagePanel());
        tabbedPane.addTab("Attend Lesson", createAttendPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        add(tabbedPane);
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Member> memberCombo = new JComboBox<>();
        for (Member m : system.getAllMembers()) {
            memberCombo.addItem(m);
        }

        JRadioButton rbDay = new JRadioButton("By Day");
        JRadioButton rbType = new JRadioButton("By Type");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbDay);
        bg.add(rbType);
        rbDay.setSelected(true);

        JComboBox<String> criteriaCombo = new JComboBox<>(new String[] { "Saturday", "Sunday" });
        rbDay.addActionListener(e -> {
            criteriaCombo.removeAllItems();
            criteriaCombo.addItem("Saturday");
            criteriaCombo.addItem("Sunday");
        });
        rbType.addActionListener(e -> {
            criteriaCombo.removeAllItems();
            criteriaCombo.addItem("Yoga");
            criteriaCombo.addItem("Zumba");
            criteriaCombo.addItem("Aquacise");
            criteriaCombo.addItem("BoxFit");
        });

        JButton btnSearch = new JButton("Search Timetable");

        filterPanel.add(new JLabel("Member:"));
        filterPanel.add(memberCombo);
        filterPanel.add(rbDay);
        filterPanel.add(rbType);
        filterPanel.add(criteriaCombo);
        filterPanel.add(btnSearch);

        // Center Table
        String[] columns = { "ID", "Type", "Month", "Weekend", "Day", "Time", "Price", "Capacity", "Status" };
        timetableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timetableTable = new JTable(timetableModel);
        JScrollPane scrollPane = new JScrollPane(timetableTable);

        btnSearch.addActionListener(e -> {
            timetableModel.setRowCount(0);
            String criteria = (String) criteriaCombo.getSelectedItem();
            List<Lesson> lessons;
            if (rbDay.isSelected()) {
                lessons = system.getLessonsByDay(criteria);
            } else {
                lessons = system.getLessonsByType(criteria);
            }

            for (Lesson l : lessons) {
                String status = l.getCurrentAttendeesCount() + "/" + l.getCapacity();
                timetableModel.addRow(new Object[] {
                        l.getId(), l.getExerciseType(), l.getMonth(), l.getWeekend(),
                        l.getDay(), l.getTime(), l.getPrice(), l.getCapacity(), status
                });
            }
        });

        // Bottom Booking Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBook = new JButton("Book Selected Lesson");
        btnBook.addActionListener(e -> {
            int row = timetableTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a lesson to book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String lessonId = (String) timetableModel.getValueAt(row, 0);
            Lesson lesson = system.getLesson(lessonId);
            Member member = (Member) memberCombo.getSelectedItem();

            String result = system.bookLesson(member, lesson);
            if (result.startsWith("Success")) {
                JOptionPane.showMessageDialog(this, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                btnSearch.doClick(); // refresh table
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        bottomPanel.add(btnBook);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search Bookings By User:"));
        JComboBox<Member> memberCombo = new JComboBox<>();
        for (Member m : system.getAllMembers())
            memberCombo.addItem(m);
        topPanel.add(memberCombo);

        JButton btnSearch = new JButton("Find Bookings");
        topPanel.add(btnSearch);

        String[] cols = { "Booking ID", "Lesson Info", "Lesson ID", "Status" };
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
                        String lessonInfo = b.getLesson().getExerciseType() + " on " + b.getLesson().getDay() + " "
                                + b.getLesson().getTime();
                        manageModel.addRow(new Object[] { b.getBookingId(), lessonInfo, b.getLesson().getId(),
                                b.getStatus().toString() });
                    }
                }
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCancel = new JButton("Cancel Selected Booking");

        bottomPanel.add(btnCancel);
        bottomPanel.add(new JLabel("  ||  Change to New Lesson ID:"));
        JTextField txtNewLessonId = new JTextField(8);
        bottomPanel.add(txtNewLessonId);
        JButton btnChange = new JButton("Change Booking");
        bottomPanel.add(btnChange);

        btnCancel.addActionListener(e -> {
            int row = manageTable.getSelectedRow();
            if (row >= 0) {
                String bId = (String) manageModel.getValueAt(row, 0);
                String res = system.cancelBooking(bId);
                JOptionPane.showMessageDialog(this, res);
                btnSearch.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking from the table first.");
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
                    JOptionPane.showMessageDialog(this, res);
                    btnSearch.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "New Lesson ID is not valid. Find it in the Book Lesson tab.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking from the table first.");
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(manageTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAttendPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search Bookings By User:"));
        JComboBox<Member> memberCombo = new JComboBox<>();
        for (Member m : system.getAllMembers())
            memberCombo.addItem(m);
        topPanel.add(memberCombo);

        JButton btnSearch = new JButton("Find Eligible Bookings");
        topPanel.add(btnSearch);

        String[] cols = { "Booking ID", "Lesson Info", "Lesson ID", "Status" };
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
                        String lessonInfo = b.getLesson().getExerciseType() + " on " + b.getLesson().getDay() + " "
                                + b.getLesson().getTime();
                        attendModel.addRow(new Object[] { b.getBookingId(), lessonInfo, b.getLesson().getId(),
                                b.getStatus().toString() });
                    }
                }
            }
        });

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblRating = new JLabel("Rating (1-5):");
        JComboBox<Integer> cmbRating = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });

        JLabel lblReview = new JLabel("Review text:");
        JTextArea txtReview = new JTextArea(3, 30);

        JButton btnAttend = new JButton("Attend Selected & Submit Review");

        btnAttend.addActionListener(e -> {
            int row = attendTable.getSelectedRow();
            if (row >= 0) {
                String bId = (String) attendModel.getValueAt(row, 0);
                int rating = (int) cmbRating.getSelectedItem();
                String review = txtReview.getText().trim();

                if (review.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Review text cannot be empty.");
                    return;
                }

                String res = system.attendLesson(bId, rating, review);
                JOptionPane.showMessageDialog(this, res);
                if (res.startsWith("Success")) {
                    txtReview.setText("");
                    btnSearch.doClick(); // Refresh table so it disappears (status changes to ATTENDED)
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an eligible booking from the table first.");
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

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(attendTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Month (1 or 2):"));
        JTextField txtMonth = new JTextField(5);
        topPanel.add(txtMonth);

        JButton btnLessonReport = new JButton("Lesson Report");
        JButton btnChampionReport = new JButton("Champion Report");
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
                JOptionPane.showMessageDialog(this, "Invalid month.");
            }
        });

        btnChampionReport.addActionListener(e -> {
            try {
                int month = Integer.parseInt(txtMonth.getText().trim());
                txtReport.setText(system.generateMonthlyChampionReport(month));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid month.");
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtReport), BorderLayout.CENTER);

        return panel;
    }
}
