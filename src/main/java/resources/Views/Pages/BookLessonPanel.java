package resources.Views.Pages;

import resources.Controllers.BookingController;
import resources.Models.*;
import resources.Views.Components.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookLessonPanel extends JPanel {
    private BookingController system;
    private JFrame parent;
    private DefaultTableModel timetableModel;
    private JTable timetableTable;

    public BookLessonPanel(BookingController system, JFrame parent) {
        this.system = system;
        this.parent = parent;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        createUserInterface();
    }

    private void createUserInterface() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JComboBox<Member> memberCombo = new JComboBox<>();
        for (Member m : system.getAllMembers()) {
            memberCombo.addItem(m);
        }

        JRadioButton radbtn_day = new JRadioButton("Filter by Day");
        JRadioButton radbtn_type = new JRadioButton("Filter by Type");

        ButtonGroup bg = new ButtonGroup();
        bg.add(radbtn_day);
        bg.add(radbtn_type);
        radbtn_day.setSelected(true);

        JComboBox<String> day_selection = new JComboBox<>(new String[] { "Saturday", "Sunday" });

        radbtn_day.addActionListener(e -> {
            day_selection.removeAllItems();
            day_selection.addItem("Saturday");
            day_selection.addItem("Sunday");
        });

        radbtn_type.addActionListener(e -> {
            day_selection.removeAllItems();
            day_selection.addItem("Yoga");
            day_selection.addItem("Zumba");
            day_selection.addItem("Aquacise");
            day_selection.addItem("Box Fit");
            day_selection.addItem("Body Blitz");
        });

        JButton search_button = new JButton("Search");
        Style.changeButtonStyle(search_button);

        filterPanel.add(new JLabel("Member:"));
        filterPanel.add(memberCombo);
        filterPanel.add(radbtn_day);
        filterPanel.add(radbtn_type);
        filterPanel.add(day_selection);
        filterPanel.add(search_button);

        String[] columns = { "ID", "Type", "Month", "Weekend", "Day", "Time", "Price", "Capacity", "Status" };
        timetableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        timetableTable = new JTable(timetableModel);
        JScrollPane scrollPane = new JScrollPane(timetableTable);

        search_button.addActionListener(e -> {
            timetableModel.setRowCount(0);
            String criteria = (String) day_selection.getSelectedItem();
            List<Lesson> lessons;

            if (radbtn_day.isSelected()) {
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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btn_book_lesson = new JButton("Book Selected Lesson");
        Style.changeButtonStyle(btn_book_lesson);

        btn_book_lesson.addActionListener(e -> {
            int row = timetableTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(parent, "Please select a lesson to book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String lessonId = (String) timetableModel.getValueAt(row, 0);
            Lesson lesson = system.getLesson(lessonId);
            Member member = (Member) memberCombo.getSelectedItem();

            String result = system.bookLesson(member, lesson);
            if (result.startsWith("Success")) {
                JOptionPane.showMessageDialog(parent, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                search_button.doClick();
            } else {
                JOptionPane.showMessageDialog(parent, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottomPanel.add(btn_book_lesson);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}