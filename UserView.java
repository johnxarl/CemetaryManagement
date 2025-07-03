package Cemetary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserView extends JFrame {
    private static final int ROWS = 4;
    private static final int COLS = 4;

    public UserView() {
        setTitle("Cemetery Management System - User View");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 10, 10));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String plotId = (char) ('A' + row) + String.valueOf(col + 1);
                JButton plotButton = new JButton();
                plotButton.setPreferredSize(new Dimension(150, 100));
                plotButton.setBackground(Color.GREEN);

                if (CemetaryDB.isPlotOccupied(plotId)) {
                    String[] data = CemetaryDB.getPlotDetails(plotId);
                    plotButton.setText("<html><center>RIP<br>" + "Name: " + data[0] + "<br>" + "Birthday: " + data[1] + "<br>" + "Death: " + data[2] + "</center></html>");
                    plotButton.setBackground(Color.LIGHT_GRAY);
                } else {
                    plotButton.setText(plotId);
                }

                final String finalPlotId = plotId;
                plotButton.addActionListener(e -> {
                    if (!CemetaryDB.isPlotOccupied(finalPlotId)) {
                        openAddForm(finalPlotId);
                    } else {
                        JOptionPane.showMessageDialog(null, "This plot is already occupied.");
                    }
                });

                gridPanel.add(plotButton);
            }
        }

        add(new JLabel("Cemetery Layout (User)", SwingConstants.CENTER), BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
    }

    private void openAddForm(String plotId) {
        JFrame form = new JFrame("Add Burial - Plot " + plotId);
        form.setSize(300, 350);
        form.setLayout(null);
        form.setLocationRelativeTo(null);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(20, 20, 200, 25);
        form.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(20, 45, 240, 25);
        form.add(nameField);

        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobLabel.setBounds(20, 75, 240, 25);
        form.add(dobLabel);

        JTextField dobField = new JTextField();
        dobField.setBounds(20, 100, 240, 25);
        form.add(dobField);

        JLabel dodLabel = new JLabel("Date of Death (YYYY-MM-DD):");
        dodLabel.setBounds(20, 130, 240, 25);
        form.add(dodLabel);

        JTextField dodField = new JTextField();
        dodField.setBounds(20, 155, 240, 25);
        form.add(dodField);

        // Limit input length to 10 characters for DOB and DOD
        setCharacterLimit(dobField, 10);
        setCharacterLimit(dodField, 10);

        // Styled Submit Button
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(90, 210, 100, 30);
        submitButton.setBackground(new Color(0, 153, 76)); // Green
        submitButton.setForeground(Color.WHITE); // White text
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0), 1, true)); // Rounded edge

        form.add(submitButton);

        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dob = dobField.getText().trim();
            String dod = dodField.getText().trim();

            if (name.isEmpty() || dob.isEmpty() || dod.isEmpty()) {
                JOptionPane.showMessageDialog(form, "Please fill in all fields.");
                return;
            }

            // Format validation
            if (!dob.matches("\\d{4}-\\d{2}-\\d{2}") || !dod.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(form, "Please enter dates in YYYY-MM-DD format.");
                return;
            }

            try {
                CemetaryDB.insertRecord(name, dob, dod, plotId);
                JOptionPane.showMessageDialog(form, "Record Added to Plot " + plotId);
                form.dispose();
                this.dispose();
                new UserView().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(form, "Error: " + ex.getMessage());
            }
        });

        form.setVisible(true);
    }

    // Helper method to limit character input
    private void setCharacterLimit(JTextField textField, int limit) {
        textField.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null || getLength() + str.length() > limit) {
                    return;
                }
                super.insertString(offs, str, a);
            }
        });
    }
}
