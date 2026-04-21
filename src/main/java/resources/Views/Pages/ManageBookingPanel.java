package resources.views.Pages;

import resources.controllers.BookingController;
import resources.models.Booking;
import resources.models.Lesson;
import resources.models.Member;
import resources.views.components.Style;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ManageBookingPanel extends JPanel {

    private final BookingController system;
    private final JFrame parent;

    public ManageBookingPanel(BookingController system, JFrame parent) {
        this.system = system;
        this.parent = parent;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializeUI();
    }

    private void initializeUI() {
        String[] monthNames = {"January", "February"};

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblMember = new JLabel("Search Bookings By User:");
        JComboBox<Member> memberCombo = new JComboBox<>();
        for (Member m : system.getAllMembers()) {
            memberCombo.addItem(m);
        }

        JButton btnSearch = new JButton("Find Bookings");
        Style.changeButtonStyle(btnSearch);

        topPanel.add(lblMember);
        topPanel.add(memberCombo);
        topPanel.add(btnSearch);

        String[] columns = {"Booking ID", "Lesson Info", "Lesson ID", "Status"};
        DefaultTableModel manageModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable manageTable = new JTable(manageModel);
        JScrollPane tableScroll = new JScrollPane(manageTable);

        JComboBox<String> cmbMonth = new JComboBox<>();
        JComboBox<Integer> cmbWeek = new JComboBox<>();
        JComboBox<String> cmbExercise = new JComboBox<>();
        JComboBox<String> cmbDay = new JComboBox<>();
        JComboBox<String> cmbTime = new JComboBox<>();

        Runnable populateWeeks = () -> {
            Integer selectedWeek = (Integer) cmbWeek.getSelectedItem();
            cmbWeek.removeAllItems();

            Set<Integer> uniqueWeeks = new LinkedHashSet<>();
            for (Lesson lesson : getCandidateLessons(manageTable, manageModel)) {
                if (matchesMonth(lesson, (String) cmbMonth.getSelectedItem(), monthNames)) {
                    uniqueWeeks.add(lesson.getWeekend());
                }
            }

            for (Integer week : uniqueWeeks) {
                cmbWeek.addItem(week);
            }

            restoreSelection(cmbWeek, selectedWeek);
        };

        Runnable populateExercises = () -> {
            String selectedExercise = (String) cmbExercise.getSelectedItem();
            cmbExercise.removeAllItems();

            Set<String> uniqueExercises = new LinkedHashSet<>();
            for (Lesson lesson : getCandidateLessons(manageTable, manageModel)) {
                if (matchesMonth(lesson, (String) cmbMonth.getSelectedItem(), monthNames)
                        && matchesWeek(lesson, (Integer) cmbWeek.getSelectedItem())) {
                    uniqueExercises.add(lesson.getExerciseType());
                }
            }

            for (String exercise : uniqueExercises) {
                cmbExercise.addItem(exercise);
            }

            restoreSelection(cmbExercise, selectedExercise);
        };

        Runnable populateDays = () -> {
            String selectedDay = (String) cmbDay.getSelectedItem();
            cmbDay.removeAllItems();

            Set<String> uniqueDays = new LinkedHashSet<>();
            for (Lesson lesson : getCandidateLessons(manageTable, manageModel)) {
                if (matchesMonth(lesson, (String) cmbMonth.getSelectedItem(), monthNames)
                        && matchesWeek(lesson, (Integer) cmbWeek.getSelectedItem())
                        && matchesText(lesson.getExerciseType(), (String) cmbExercise.getSelectedItem())) {
                    uniqueDays.add(lesson.getDay());
                }
            }

            for (String day : uniqueDays) {
                cmbDay.addItem(day);
            }

            restoreSelection(cmbDay, selectedDay);
        };

        Runnable populateTimes = () -> {
            String selectedTime = (String) cmbTime.getSelectedItem();
            cmbTime.removeAllItems();

            Set<String> uniqueTimes = new LinkedHashSet<>();
            for (Lesson lesson : getCandidateLessons(manageTable, manageModel)) {
                if (matchesMonth(lesson, (String) cmbMonth.getSelectedItem(), monthNames)
                        && matchesWeek(lesson, (Integer) cmbWeek.getSelectedItem())
                        && matchesText(lesson.getExerciseType(), (String) cmbExercise.getSelectedItem())
                        && matchesText(lesson.getDay(), (String) cmbDay.getSelectedItem())) {
                    uniqueTimes.add(lesson.getTime());
                }
            }

            for (String time : uniqueTimes) {
                cmbTime.addItem(time);
            }

            restoreSelection(cmbTime, selectedTime);
        };

        Runnable refreshChangeSelectors = () -> {
            Booking booking = getSelectedBooking(manageTable, manageModel);

            cmbMonth.removeAllItems();
            cmbWeek.removeAllItems();
            cmbExercise.removeAllItems();
            cmbDay.removeAllItems();
            cmbTime.removeAllItems();

            if (booking == null) {
                return;
            }

            Set<String> uniqueMonths = new LinkedHashSet<>();
            for (Lesson lesson : system.getAvailableLessonsForChange(booking.getBookingId())) {
                uniqueMonths.add(monthNames[lesson.getMonth() - 1]);
            }

            for (String month : uniqueMonths) {
                cmbMonth.addItem(month);
            }

            if (cmbMonth.getItemCount() > 0) {
                cmbMonth.setSelectedIndex(0);
                populateWeeks.run();
                populateExercises.run();
                populateDays.run();
                populateTimes.run();
            }
        };

        btnSearch.addActionListener(e -> {
            manageModel.setRowCount(0);

            Member selectedMember = (Member) memberCombo.getSelectedItem();
            if (selectedMember != null) {
                for (Booking booking : system.getAllBookings()) {
                    if (booking.getMember().equals(selectedMember)) {
                        Lesson lesson = booking.getLesson();
                        String lessonInfo = lesson.getExerciseType() + " - "
                                + monthNames[lesson.getMonth() - 1] + ", Week " + lesson.getWeekend()
                                + ", " + lesson.getDay() + ", " + lesson.getTime();

                        manageModel.addRow(new Object[]{
                                booking.getBookingId(),
                                lessonInfo,
                                lesson.getId(),
                                booking.getStatus().toString()
                        });
                    }
                }
            }

            refreshChangeSelectors.run();
        });

        manageTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                refreshChangeSelectors.run();
            }
        });

        cmbMonth.addActionListener(e -> {
            populateWeeks.run();
            populateExercises.run();
            populateDays.run();
            populateTimes.run();
        });

        cmbWeek.addActionListener(e -> {
            populateExercises.run();
            populateDays.run();
            populateTimes.run();
        });

        cmbExercise.addActionListener(e -> {
            populateDays.run();
            populateTimes.run();
        });

        cmbDay.addActionListener(e -> populateTimes.run());

        JButton btnCancel = new JButton("Cancel Selected Booking");
        JButton btnChange = new JButton("Change Booking");

        Style.changeButtonStyle(btnCancel, new Color(231, 76, 60), new Color(192, 57, 43));
        Style.changeButtonStyle(btnChange, new Color(155, 89, 182), new Color(142, 68, 173));

        btnCancel.addActionListener(e -> {
            Booking selectedBooking = getSelectedBooking(manageTable, manageModel);

            if (selectedBooking == null) {
                JOptionPane.showMessageDialog(parent, "Please select a booking from the table first.");
                return;
            }

            String result = system.cancelBooking(selectedBooking.getBookingId());
            JOptionPane.showMessageDialog(parent, result);
            btnSearch.doClick();
        });

        btnChange.addActionListener(e -> {
            Booking selectedBooking = getSelectedBooking(manageTable, manageModel);

            if (selectedBooking == null) {
                JOptionPane.showMessageDialog(parent, "Please select a booking from the table first.");
                return;
            }

            String selectedMonth = (String) cmbMonth.getSelectedItem();
            Integer selectedWeek = (Integer) cmbWeek.getSelectedItem();
            String selectedExercise = (String) cmbExercise.getSelectedItem();
            String selectedDay = (String) cmbDay.getSelectedItem();
            String selectedTime = (String) cmbTime.getSelectedItem();

            if (selectedMonth == null || selectedWeek == null || selectedExercise == null
                    || selectedDay == null || selectedTime == null) {
                JOptionPane.showMessageDialog(parent, "Please choose a valid new lesson.");
                return;
            }

            int monthNumber = getMonthNumber(selectedMonth, monthNames);
            Lesson newLesson = system.findLesson(monthNumber, selectedWeek, selectedDay, selectedTime, selectedExercise);

            if (newLesson == null) {
                JOptionPane.showMessageDialog(parent, "Selected lesson could not be found.");
                return;
            }

            String result = system.changeBooking(selectedBooking.getBookingId(), newLesson);
            JOptionPane.showMessageDialog(parent, result);
            btnSearch.doClick();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(btnCancel);
        bottomPanel.add(new JLabel("Month:"));
        bottomPanel.add(cmbMonth);
        bottomPanel.add(new JLabel("Week:"));
        bottomPanel.add(cmbWeek);
        bottomPanel.add(new JLabel("Exercise:"));
        bottomPanel.add(cmbExercise);
        bottomPanel.add(new JLabel("Day:"));
        bottomPanel.add(cmbDay);
        bottomPanel.add(new JLabel("Time:"));
        bottomPanel.add(cmbTime);
        bottomPanel.add(btnChange);

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private Booking getSelectedBooking(JTable manageTable, DefaultTableModel manageModel) {
        int row = manageTable.getSelectedRow();
        if (row < 0) {
            return null;
        }

        String bookingId = String.valueOf(manageModel.getValueAt(row, 0));
        for (Booking booking : system.getAllBookings()) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    private List<Lesson> getCandidateLessons(JTable manageTable, DefaultTableModel manageModel) {
        Booking booking = getSelectedBooking(manageTable, manageModel);
        if (booking == null) {
            return Collections.emptyList();
        }
        return system.getAvailableLessonsForChange(booking.getBookingId());
    }

    private boolean matchesMonth(Lesson lesson, String selectedMonth, String[] monthNames) {
        return selectedMonth != null && monthNames[lesson.getMonth() - 1].equals(selectedMonth);
    }

    private boolean matchesWeek(Lesson lesson, Integer selectedWeek) {
        return selectedWeek != null && lesson.getWeekend() == selectedWeek;
    }

    private boolean matchesText(String actual, String selected) {
        return selected != null && actual.equals(selected);
    }

    private <T> void restoreSelection(JComboBox<T> comboBox, T oldValue) {
        if (comboBox.getItemCount() == 0) {
            return;
        }

        if (oldValue != null) {
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                T item = comboBox.getItemAt(i);
                if (oldValue.equals(item)) {
                    comboBox.setSelectedItem(oldValue);
                    return;
                }
            }
        }

        comboBox.setSelectedIndex(0);
    }

    private int getMonthNumber(String monthName, String[] monthNames) {
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(monthName)) {
                return i + 1;
            }
        }
        return -1;
    }
}