package vrms;

import java.io.File;
import java.io.IOException;

public class DataFiles {
    public static final File DATA_DIR = new File("data");
    public static final File USERS_FILE = new File(DATA_DIR, "users.txt");
    public static final File VEHICLES_FILE = new File(DATA_DIR, "vehicles.txt");
    public static final File RENTALS_FILE = new File(DATA_DIR, "rentals.txt");

    private DataFiles() {
    }

    public static void initialize() throws IOException {
        if (!DATA_DIR.exists() && !DATA_DIR.mkdirs()) {
            throw new IOException("Could not create local data directory.");
        }

        createFile(USERS_FILE);
        createFile(VEHICLES_FILE);
        createFile(RENTALS_FILE);
    }

    private static void createFile(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create " + file.getPath());
        }
    }
}
