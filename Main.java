package Cemetary;

import javax.swing.*;

public class Main {

    private static final String ADMIN_PASSWORD = "0000"; // Still simple, just hidden when typed

    public static void main(String[] args) {
        CemetaryDB.initializeDatabase();

        Object[] options = {"User", "Admin"};
        int choice = JOptionPane.showOptionDialog(null,
                "Select your role:",
                "Cemetery Management System",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.YES_OPTION) { // User
            SwingUtilities.invokeLater(() -> new UserView().setVisible(true));

        } else if (choice == JOptionPane.NO_OPTION) { // Admin
            JPasswordField passwordField = new JPasswordField();
            int result = JOptionPane.showConfirmDialog(null, passwordField,
                    "Enter admin password:", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String enteredPassword = new String(passwordField.getPassword());
                if (enteredPassword.equals(ADMIN_PASSWORD)) {
                    SwingUtilities.invokeLater(() -> new AdminView().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect password! Exiting.");
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }
}
