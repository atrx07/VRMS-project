package vrms;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RentalStore {

    private RentalStore() {
    }

    public static String createRental(int vehicleId, int customerId,
                                      LocalDate startDate, LocalDate endDate) throws IOException {
        DataFiles.initialize();

        String[] vehicle = VehicleStore.getVehicleById(vehicleId);
        if (vehicle == null) {
            return "Vehicle not found.";
        }

        if (!vehicle[7].equals("APPROVED") || !vehicle[6].equals("AVAILABLE")) {
            return "This vehicle is no longer available for rent.";
        }

        if (Integer.parseInt(vehicle[1]) == customerId) {
            return "You cannot rent your own vehicle.";
        }

        if (endDate.isBefore(startDate)) {
            return "End date cannot be before the start date.";
        }

        long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));
        double total = days * Double.parseDouble(vehicle[5]);
        int rentalId = nextId();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataFiles.RENTALS_FILE, true))) {
            writer.write(rentalId + "|" + vehicleId + "|" + customerId + "|"
                    + startDate + "|" + endDate + "|"
                    + String.format(Locale.US, "%.2f", total) + "|ACTIVE");
            writer.newLine();
        }

        VehicleStore.updateAvailability(vehicleId, "RENTED");
        return null;
    }

    public static double calculateTotal(double pricePerDay, LocalDate startDate, LocalDate endDate) {
        long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));
        return days * pricePerDay;
    }

    public static List<String[]> getRentalsForCustomer(int customerId) throws IOException {
        List<String[]> result = new ArrayList<>();

        for (String[] rental : readRentals()) {
            if (Integer.parseInt(rental[2]) == customerId) {
                result.add(rental);
            }
        }

        return result;
    }

    private static List<String[]> readRentals() throws IOException {
        DataFiles.initialize();
        List<String[]> rentals = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(DataFiles.RENTALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rental = line.split("\\|", -1);
                if (rental.length == 7) {
                    rentals.add(rental);
                }
            }
        }

        return rentals;
    }

    private static int nextId() throws IOException {
        int maxId = 0;

        for (String[] rental : readRentals()) {
            try {
                maxId = Math.max(maxId, Integer.parseInt(rental[0]));
            } catch (NumberFormatException ignored) {
            }
        }

        return maxId + 1;
    }
}
