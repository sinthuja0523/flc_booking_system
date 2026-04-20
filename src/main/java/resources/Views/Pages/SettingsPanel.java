package resources.Views.Pages;

import resources.Controllers.BookingController;
import resources.Views.Components.Style;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private BookingController system;
    private JFrame parent;
    private BookLessonPanel bookLessonPanel;

    public SettingsPanel(BookingController system, JFrame parent, BookLessonPanel bookLessonPanel) {
        this.system = system;
        this.parent = parent;
        this.bookLessonPanel = bookLessonPanel;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        createUI();
    }

    private void createUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblExercise = new JLabel("Exercise Type:");
        JComboBox<String> cmbExercise = new JComboBox<>();

        for (String type : system.getAllExerciseTypes()) {
            cmbExercise.addItem(type);
        }

        JLabel lblCurrentPrice = new JLabel("Current Price:");
        JTextField txtCurrentPrice = new JTextField(10);
        txtCurrentPrice.setEditable(false);

        JLabel lblNewPrice = new JLabel("New Price:");
        JTextField txtNewPrice = new JTextField(10);

        JButton btnLoad = new JButton("Load Price");
        JButton btnUpdate = new JButton("Update Price");

        Style.changeButtonStyle(btnLoad);
        Style.changeButtonStyle(btnUpdate);

        btnLoad.addActionListener(e -> {
            String selectedType = (String) cmbExercise.getSelectedItem();
            double price = system.getPriceByExerciseType(selectedType);
            txtCurrentPrice.setText(String.valueOf(price));
        });

        btnUpdate.addActionListener(e -> {
            String selectedType = (String) cmbExercise.getSelectedItem();

            try {
                double newPrice = Double.parseDouble(txtNewPrice.getText().trim());
                String result = system.updatePriceByExerciseType(selectedType, newPrice);
                JOptionPane.showMessageDialog(parent, result);

                if (result.startsWith("Success")) {
                    txtCurrentPrice.setText(String.valueOf(newPrice));
                    txtNewPrice.setText("");
                    bookLessonPanel.refreshLessonTable();
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Please enter a valid price.");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblExercise, gbc);

        gbc.gridx = 1;
        formPanel.add(cmbExercise, gbc);

        gbc.gridx = 2;
        formPanel.add(btnLoad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblCurrentPrice, gbc);

        gbc.gridx = 1;
        formPanel.add(txtCurrentPrice, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblNewPrice, gbc);

        gbc.gridx = 1;
        formPanel.add(txtNewPrice, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(btnUpdate, gbc);

        add(formPanel, BorderLayout.NORTH);
    }
}