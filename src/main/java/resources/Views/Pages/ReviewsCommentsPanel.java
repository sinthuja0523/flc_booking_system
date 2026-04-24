package resources.views.Pages;

import resources.controllers.BookingController;
import resources.models.Lesson;
import resources.models.Review;
import resources.views.components.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class ReviewsCommentsPanel extends JPanel {
    private BookingController system;
    private JFrame parent;
    private DefaultTableModel reviewsModel;

    public ReviewsCommentsPanel(BookingController system, JFrame parent) {
        this.system = system;
        this.parent = parent;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createUI();
    }

    private void createUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblLesson = new JLabel("Select Lesson Type:");
        JComboBox<String> lessonTypeCombo = new JComboBox<>();

        Set<String> lessonTypes = new LinkedHashSet<>();

        for (Lesson lesson : system.getAllLessons()) {
            lessonTypes.add(lesson.getExerciseType());
        }

        for (String type : lessonTypes) {
            lessonTypeCombo.addItem(type);
        }

        JButton btnView = new JButton("View Reviews");
        Style.changeButtonStyle(btnView);

        topPanel.add(lblLesson);
        topPanel.add(lessonTypeCombo);
        topPanel.add(btnView);

        String[] columns = {
                "Lesson ID",
                "Exercise",
                "Month",
                "Weekend",
                "Day",
                "Time",
                "Rating",
                "Comment"
        };

        reviewsModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable reviewsTable = new JTable(reviewsModel);
        reviewsTable.setRowHeight(25);

        btnView.addActionListener(e -> {
            String selectedType = (String) lessonTypeCombo.getSelectedItem();

            if (selectedType == null) {
                JOptionPane.showMessageDialog(parent, "Please select a lesson type.");
                return;
            }

            loadReviewsByLessonType(selectedType);
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(reviewsTable), BorderLayout.CENTER);
    }

    private void loadReviewsByLessonType(String lessonType) {
        reviewsModel.setRowCount(0);

        boolean found = false;

        for (Lesson lesson : system.getAllLessons()) {
            if (lesson.getExerciseType().equalsIgnoreCase(lessonType)) {
                for (Review review : lesson.getReviews()) {
                    found = true;

                    reviewsModel.addRow(new Object[]{
                            lesson.getId(),
                            lesson.getExerciseType(),
                            lesson.getMonth(),
                            lesson.getWeekend(),
                            lesson.getDay(),
                            lesson.getTime(),
                            review.getRating(),
                            review.getText()
                    });
                }
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(parent, "No reviews or comments available for " + lessonType + ".");
        }
    }
}