package vrms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehicleStore {

    private VehicleStore() {
    }

    public static String addVehicle(int ownerId, String name, String type,
                                    String registrationNumber, double pricePerDay) throws IOException {
        DataFiles.initialize();

        if (containsSeparator(name) || containsSeparator(type) || containsSeparator(registrationNumber)) {
            return "The character | cannot be used in vehicle details.";
        }

        String registration = registrationNumber.trim().toUpperCase();
        if (registrationExists(registration)) {
            return "A vehicle with this registration number already exists.";
        }

        int vehicleId = nextId();
        String price = String.format(Locale.US, "%.2f", pricePerDay);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataFiles.VEHICLES_FILE, true))) {
            writer.write(vehicleId + "|" + ownerId + "|" + name.trim() + "|" + type.trim()
                    + "|" + registration + "|" + price + "|AVAILABLE|PENDING");
            writer.newLine();
        }

        return null;
    }

    public static List<String[]> getCatalogVehicles(int currentUserId) throws IOException {
        List<String[]> result = new ArrayList<>();

        for (String[] vehicle : readVehicles()) {
            if (vehicle[7].equals("APPROVED")
                    && vehicle[6].equals("AVAILABLE")
                    && Integer.parseInt(vehicle[1]) != currentUserId) {
                result.add(vehicle);
            }
        }

        return result;
    }

    public static List<String[]> getVehiclesForOwner(int ownerId) throws IOException {
        List<String[]> result = new ArrayList<>();

        for (String[] vehicle : readVehicles()) {
            if (Integer.parseInt(vehicle[1]) == ownerId) {
                result.add(vehicle);
            }
        }

        return result;
    }

    public static List<String[]> getPendingVehicles() throws IOException {
        List<String[]> result = new ArrayList<>();

        for (String[] vehicle : readVehicles()) {
            if (vehicle[7].equals("PENDING")) {
                result.add(vehicle);
            }
        }

        return result;
    }

    public static boolean updateApproval(int vehicleId, String approvalStatus) throws IOException {
        List<String[]> vehicles = readVehicles();
        boolean found = false;

        for (String[] vehicle : vehicles) {
            if (Integer.parseInt(vehicle[0]) == vehicleId) {
                vehicle[7] = approvalStatus;
                found = true;
                break;
            }
        }

        if (found) {
            rewriteVehicles(vehicles);
        }

        return found;
    }

    private static List<String[]> readVehicles() throws IOException {
        DataFiles.initialize();
        List<String[]> vehicles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(DataFiles.VEHICLES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] vehicle = line.split("\\|", -1);
                if (vehicle.length == 8) {
                    vehicles.add(vehicle);
                }
            }
        }

        return vehicles;
    }

    private static void rewriteVehicles(List<String[]> vehicles) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataFiles.VEHICLES_FILE))) {
            for (String[] vehicle : vehicles) {
                writer.write(String.join("|", vehicle));
                writer.newLine();
            }
        }
    }

    private static boolean registrationExists(String registrationNumber) throws IOException {
        for (String[] vehicle : readVehicles()) {
            if (vehicle[4].equalsIgnoreCase(registrationNumber)) {
                return true;
            }
        }
        return false;
    }

    private static int nextId() throws IOException {
        int maxId = 0;

        for (String[] vehicle : readVehicles()) {
            try {
                maxId = Math.max(maxId, Integer.parseInt(vehicle[0]));
            } catch (NumberFormatException ignored) {
            }
        }

        return maxId + 1;
    }

    private static boolean containsSeparator(String value) {
        return value.contains("|") || value.contains("\n") || value.contains("\r");
    }
}
