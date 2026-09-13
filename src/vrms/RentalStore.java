package vrms;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RentalStore {
    public static final double PLATFORM_FEE_RATE = 0.10;

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

        double rentalAmount = calculateTotal(Double.parseDouble(vehicle[5]), startDate, endDate);
        double platformFee = calculatePlatformFee(rentalAmount);
        double totalPaid = rentalAmount + platformFee;
        int rentalId = nextId();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataFiles.RENTALS_FILE, true))) {
            writer.write(rentalId + "|" + vehicleId + "|" + customerId + "|"
                    + startDate + "|" + endDate + "|"
                    + money(rentalAmount) + "|"
                    + money(platformFee) + "|"
                    + money(totalPaid) + "|ACTIVE");
            writer.newLine();
        }

        VehicleStore.updateAvailability(vehicleId, "RENTED");
        return null;
    }

    public static String returnRental(int rentalId, int customerId) throws IOException {
        List<String[]> rentals = readRentals();
        String[] selectedRental = null;

        for (String[] rental : rentals) {
            if (Integer.parseInt(rental[0]) == rentalId) {
                selectedRental = rental;
                break;
            }
        }

        if (selectedRental == null) {
            return "Rental not found.";
        }

        if (Integer.parseInt(selectedRental[2]) != customerId) {
            return "This rental does not belong to the current user.";
        }

        if (!selectedRental[8].equals("ACTIVE")) {
            return "This vehicle has already been returned.";
        }

        int vehicleId = Integer.parseInt(selectedRental[1]);
        selectedRental[8] = "RETURNED";
        rewriteRentals(rentals);
        VehicleStore.updateAvailability(vehicleId, "AVAILABLE");

        return null;
    }

    public static double calculateTotal(double pricePerDay, LocalDate startDate, LocalDate endDate) {
        long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));
        return days * pricePerDay;
    }

    public static double calculatePlatformFee(double rentalAmount) {
        return rentalAmount * PLATFORM_FEE_RATE;
    }

    public static double calculateCustomerTotal(double rentalAmount) {
        return rentalAmount + calculatePlatformFee(rentalAmount);
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

    public static List<String[]> getAllRentals() throws IOException {
        return readRentals();
    }

    public static double getTotalPlatformEarnings() throws IOException {
        double total = 0;
        for (String[] rental : readRentals()) {
            total += Double.parseDouble(rental[6]);
        }
        return total;
    }

    public static double getTotalOwnerPayouts() throws IOException {
        double total = 0;
        for (String[] rental : readRentals()) {
            total += Double.parseDouble(rental[5]);
        }
        return total;
    }

    public static double getTotalCustomerPayments() throws IOException {
        double total = 0;
        for (String[] rental : readRentals()) {
            total += Double.parseDouble(rental[7]);
        }
        return total;
    }

    private static List<String[]> readRentals() throws IOException {
        DataFiles.initialize();
        List<String[]> rentals = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(DataFiles.RENTALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rental = line.split("\\|", -1);

                if (rental.length == 9) {
                    rentals.add(rental);
                } else if (rental.length == 7) {
                    // Compatibility with rentals created before the payment page existed.
                    rentals.add(new String[]{
                            rental[0], rental[1], rental[2], rental[3], rental[4],
                            rental[5], "0.00", rental[5], rental[6]
                    });
                }
            }
        }

        return rentals;
    }

    private static void rewriteRentals(List<String[]> rentals) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataFiles.RENTALS_FILE))) {
            for (String[] rental : rentals) {
                writer.write(String.join("|", rental));
                writer.newLine();
            }
        }
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

    private static String money(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }
}
