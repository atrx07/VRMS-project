package vrms;

import javax.swing.JOptionPane;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserStore {
    public static final String DEFAULT_ADMIN_EMAIL = "admin@vrms.com";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private UserStore() {
    }

    public static boolean ensureDefaultAdmin() {
        try {
            DataFiles.initialize();

            if (!emailExists(DEFAULT_ADMIN_EMAIL)) {
                appendUser(nextId(), "Administrator", DEFAULT_ADMIN_EMAIL,
                        "0000000000", DEFAULT_ADMIN_PASSWORD, "ADMIN");
            }

            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "VRMS could not create its local data files.\n" + ex.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static String registerCustomer(String name, String email,
                                          String phone, String password) throws IOException {
        DataFiles.initialize();

        if (containsSeparator(name) || containsSeparator(email)
                || containsSeparator(phone) || containsSeparator(password)) {
            return "The character | cannot be used in registration details.";
        }

        if (emailExists(email)) {
            return "An account with this email already exists.";
        }

        appendUser(nextId(), name.trim(), email.trim(), phone.trim(), password, "CUSTOMER");
        return null;
    }

    public static boolean login(String email, String password, String requiredRole) throws IOException {
        DataFiles.initialize();

        try (BufferedReader reader = new BufferedReader(new FileReader(DataFiles.USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] user = line.split("\\|", -1);
                if (user.length != 6) {
                    continue;
                }

                if (user[2].equalsIgnoreCase(email.trim())
                        && user[4].equals(password)
                        && user[5].equalsIgnoreCase(requiredRole)) {
                    Session.start(Integer.parseInt(user[0]), user[1], user[2], user[5]);
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean emailExists(String email) throws IOException {
        DataFiles.initialize();

        for (String[] user : readUsers()) {
            if (user[2].equalsIgnoreCase(email.trim())) {
                return true;
            }
        }

        return false;
    }

    public static String findNameById(int userId) throws IOException {
        String[] user = getUserById(userId);
        return user == null ? "User " + userId : user[1];
    }

    public static String[] getUserById(int userId) throws IOException {
        DataFiles.initialize();

        for (String[] user : readUsers()) {
            if (Integer.parseInt(user[0]) == userId) {
                return user;
            }
        }

        return null;
    }

    public static String updateProfile(int userId, String name, String email,
                                       String phone, String newPassword) throws IOException {
        DataFiles.initialize();

        name = name.trim();
        email = email.trim();
        phone = phone.trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            return "Name, email and phone cannot be empty.";
        }

        if (containsSeparator(name) || containsSeparator(email)
                || containsSeparator(phone) || containsSeparator(newPassword)) {
            return "The character | cannot be used in profile details.";
        }

        List<String[]> users = readUsers();
        String[] selectedUser = null;

        for (String[] user : users) {
            int id = Integer.parseInt(user[0]);
            if (id != userId && user[2].equalsIgnoreCase(email)) {
                return "Another account already uses this email.";
            }
            if (id == userId) {
                selectedUser = user;
            }
        }

        if (selectedUser == null) {
            return "Account not found.";
        }

        selectedUser[1] = name;
        selectedUser[2] = email;
        selectedUser[3] = phone;
        if (!newPassword.isEmpty()) {
            selectedUser[4] = newPassword;
        }

        rewriteUsers(users);
        Session.start(userId, selectedUser[1], selectedUser[2], selectedUser[5]);
        return null;
    }

    private static List<String[]> readUsers() throws IOException {
        DataFiles.initialize();
        List<String[]> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(DataFiles.USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] user = line.split("\\|", -1);
                if (user.length == 6) {
                    users.add(user);
                }
            }
        }

        return users;
    }

    private static void rewriteUsers(List<String[]> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataFiles.USERS_FILE))) {
            for (String[] user : users) {
                writer.write(String.join("|", user));
                writer.newLine();
            }
        }
    }

    private static int nextId() throws IOException {
        int maxId = 0;

        for (String[] user : readUsers()) {
            try {
                maxId = Math.max(maxId, Integer.parseInt(user[0]));
            } catch (NumberFormatException ignored) {
            }
        }

        return maxId + 1;
    }

    private static void appendUser(int id, String name, String email,
                                   String phone, String password, String role) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataFiles.USERS_FILE, true))) {
            writer.write(id + "|" + name + "|" + email + "|" + phone + "|" + password + "|" + role);
            writer.newLine();
        }
    }

    private static boolean containsSeparator(String value) {
        return value.contains("|") || value.contains("\n") || value.contains("\r");
    }
}
