# VRMS - Vehicle Rental Management System

A Java Swing OOP micro project for peer-to-peer vehicle rentals.

## Current storage

VRMS now uses **MySQL through JDBC**.

The Swing pages still call the same store classes:

```text
Swing UI
   |
   +-- UserStore
   +-- VehicleStore
   +-- RentalStore
           |
           v
        JDBC
           |
           v
         MySQL
```

The database contains four main tables:

- `users`
- `vehicles`
- `rentals`
- `payments`

The SQL schema is also available at:

```text
database/vrms.sql
```

## MySQL setup

1. Install and start MySQL locally.
2. Fresh clones do **not** need a manual Connector/J download. The included build script downloads MySQL Connector/J automatically the first time it is needed.
3. By default VRMS connects with:

```text
Host: localhost
Port: 3306
Database: vrms
User: root
Password: empty
```

On startup, the program creates the `vrms` database and required tables if they do not already exist.

On the first `run.bat` launch, VRMS asks for the local MySQL username and password. The password input is hidden and the login is saved locally in `.vrms-db.json` using Windows user encryption. That file is ignored by Git, so normal pulls do not ask for the password again.

To replace the saved login later:

```powershell
.\run.bat -ResetDbConfig
```

Environment variables still override the saved login when needed:

```text
VRMS_DB_USER
VRMS_DB_PASSWORD
VRMS_DB_SERVER_URL
VRMS_DB_URL
```

A default admin is created automatically:

```text
Email: admin@vrms.com
Password: admin123
```

## Compile and run

### Easiest option on Windows

After a fresh clone or pull, run:

```powershell
.\run.bat
```

You can also double-click `run.bat`.

It automatically:
- asks for the MySQL login on the first run and remembers it locally
- checks whether the local MySQL service/port is available
- tries to start an installed MySQL Windows service if it is stopped
- checks for a JDK
- downloads MySQL Connector/J if the JAR is missing
- cleans the old `out/` folder
- compiles every Java source file
- launches VRMS

The downloaded Connector/J JAR stays in `lib/` locally and is ignored by Git, so later pulls do not need to download it again.

### Compile only

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build.ps1
```

### Manual compile/run

If the Connector/J JAR already exists in `lib/`:

```powershell
javac -cp "lib/*" -d out src/vrms/*.java
java -cp "out;lib/*" vrms.Main
```

Java 17 or newer is recommended.

## Main features

- Customer registration and login
- Separate admin login
- Editable customer profile
- Peer-to-peer vehicle listing
- Admin approval and rejection of listings
- Customer vehicle catalog
- My Vehicles
- Date-based rental calculation
- Mock payment flow
- 10% VRMS service fee
- My Rentals
- Return Vehicle
- Admin vehicle management
- Admin Earnings
- MySQL JDBC persistence
- Prepared statements for database operations
- Transaction-based rental/payment confirmation

## Rental and payment flow

The customer selects a vehicle and rental dates. VRMS calculates the rental amount from the listed daily rate.

The payment page shows:

- Rental amount paid to the vehicle owner
- 10% VRMS service fee
- Total amount paid by the customer

Example:

```text
Rental amount:       Rs. 3000.00
VRMS service fee:    Rs.  300.00
Total paid:          Rs. 3300.00
Owner receives:      Rs. 3000.00
VRMS earns:          Rs.  300.00
```

The payment is simulated. No real bank, card, UPI, or external payment gateway is connected.

When a payment is confirmed, JDBC performs the rental creation, payment insertion, and vehicle status update inside one database transaction. Returning a vehicle updates the rental status to `RETURNED` and restores the vehicle to `AVAILABLE`.

## JDBC classes used

The project uses standard JDBC classes from `java.sql`:

- `DriverManager`
- `Connection`
- `PreparedStatement`
- `Statement`
- `ResultSet`

`Database.java` contains the MySQL connection and table initialization code. The store classes contain the SQL used by the application.
