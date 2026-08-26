package vrms;

import java.io.*;

public class UserStore {
    public static final String DEFAULT_ADMIN_EMAIL = "admin@vrms.com";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private UserStore() {
    }

    public static void ensureDefaultAdmin() throws IOException {
        DataFiles.initialize();

        if (!emailExists(DEFAULT_ADMIN_EMAIL)) {
            appendUser(nextId(), "Administrator", DEFAULT_ADMIN_EMAIL,
                    "0000000000", DEFAULT_ADMIN_PASSWORD, "ADMIN");
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

        try (BufferedReader reader = new BufferedReader(new FileReader(DataFiles.USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] user = line.split("\\|", -1);
                if (user.length == 6 && user[2].equalsIgnoreCase(email.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String findNameById(int userId) throws IOException {
        DataFiles.initialize();

        try (BufferedReader reader = new BufferedReader(new FileReader(DataFiles.USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] user = line.split("\\|", -1);
                if (user.length == 6 && Integer.parseInt(user[0]) == userId) {
                    return user[1];
                }
            }
        }

        return "User " + userId;
    }

    private static int nextId() throws IOException {
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(DataFiles.USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] user = line.split("\\|", -1);
                if (user.length == 6) {
                    try {
                        maxId = Math.max(maxId, Integer.parseInt(user[0]));
                    } catch (NumberFormatException ignored) {
                    }
                }
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
