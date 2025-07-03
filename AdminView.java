package Cemetary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminView extends JFrame {
    private DefaultTableModel model;
    private JTable table;

    // ✅ Declare these up top
    private JTextField searchField;
    private JButton searchButton;

    public AdminView() {
        setTitle("Cemetery Management System - Admin View");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel with search bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        topPanel.add(new JLabel("Search (Name or Plot):"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "Name", "Birthday", "Death", "Plot", "Edit", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        loadTableData(""); // Load all records initially

        // Table button clicks
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                int id = (int) model.getValueAt(row, 0);

                if (col == 5) { // Edit
                    openEditForm(id);
                } else if (col == 6) { // Delete
                    String plot = (String) model.getValueAt(row, 4);
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Delete burial record with ID " + id + " (Plot " + plot + ")?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            CemetaryDB.deleteRecordById(id);
                            JOptionPane.showMessageDialog(null, "Record deleted.");
                            loadTableData(searchField.getText().trim());
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Delete error: " + ex.getMessage());
                        }
                    }
                }
            }
        });

        table.getColumnModel().getColumn(5).setCellRenderer(createButtonRenderer("Edit"));
        table.getColumnModel().getColumn(6).setCellRenderer(createButtonRenderer("Delete"));

        add(scroll, BorderLayout.CENTER);

        // Search button logic
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            loadTableData(keyword);
        });

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back");
        bottomPanel.add(backButton);
        backButton.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> Main.main(null));
        });

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTableData(String keyword) {
        model.setRowCount(0);
        try (ResultSet rs = CemetaryDB.searchRecords(keyword)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("dob"),
                        rs.getString("dod"),
                        rs.getString("plot"),
                        "Edit",
                        "Delete"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void openEditForm(int id) {
        try {
            String[] data = CemetaryDB.getRecordById(id);
            if (data == null) {
                JOptionPane.showMessageDialog(this, "Record not found.");
                return;
            }

            JFrame form = new JFrame("Edit Burial Record");
            form.setSize(300, 350);
            form.setLayout(null);
            form.setLocationRelativeTo(null);

            JLabel nameLabel = new JLabel("Full Name:");
            nameLabel.setBounds(20, 20, 100, 25);
            form.add(nameLabel);

            JTextField nameField = new JTextField(data[0]);
            nameField.setBounds(20, 45, 240, 25);
            form.add(nameField);

            JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
            dobLabel.setBounds(20, 75, 200, 25);
            form.add(dobLabel);

            JTextField dobField = new JTextField(data[1]);
            dobField.setBounds(20, 100, 240, 25);
            form.add(dobField);

            JLabel dodLabel = new JLabel("Date of Death (YYYY-MM-DD):");
            dodLabel.setBounds(20, 130, 200, 25);
            form.add(dodLabel);

            JTextField dodField = new JTextField(data[2]);
            dodField.setBounds(20, 155, 240, 25);
            form.add(dodField);

            JButton saveButton = new JButton("Save");
            saveButton.setBounds(90, 210, 100, 30);
            form.add(saveButton);

            saveButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String dob = dobField.getText().trim();
                String dod = dodField.getText().trim();

                if (name.isEmpty() || dob.isEmpty() || dod.isEmpty()) {
                    JOptionPane.showMessageDialog(form, "Please fill in all fields.");
                    return;
                }

                try {
                    CemetaryDB.updateRecord(id, name, dob, dod);
                    JOptionPane.showMessageDialog(form, "Record updated.");
                    form.dispose();
                    loadTableData(searchField.getText().trim());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(form, "Update error: " + ex.getMessage());
                }
            });

            form.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading record: " + e.getMessage());
        }
    }

    private DefaultTableCellRenderer createButtonRenderer(String text) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JButton button = new JButton(text);
                button.setFocusPainted(false);
                button.setBackground(text.equals("Edit") ? new Color(70, 130, 180) : Color.RED);
                button.setForeground(Color.WHITE);
                return button;
            }
        };
    }
}
