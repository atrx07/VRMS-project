# VRMS - Vehicle Rental Management System

A simple Java Swing micro project for managing vehicle rentals.

## Current milestone

Second phase: functional pages with local file storage. Database integration is intentionally not used yet.

Implemented:
- Customer login and registration
- Separate admin login
- Persistent local user accounts
- Simple session state after login
- Customer vehicle catalog
- List a vehicle for rent
- My Vehicles page
- Admin dashboard for pending vehicle approvals
- Approve or reject vehicle listings
- Local runtime files for users, vehicles, and future rentals

## Local data

On first run, VRMS automatically creates this folder in the project root:

```text
data/
├── users.txt
├── vehicles.txt
└── rentals.txt
```

The `data/` directory is ignored by Git, so local accounts and vehicle records are not committed to the repository.

A default admin account is created automatically:

```text
Email: admin@vrms.com
Password: admin123
```

Customers register normally through the registration page. Vehicle listings are saved with `PENDING` approval status. An admin can approve them, after which they appear in the catalog for other customers.

## Run

From the repository root:

```bash
javac -d out src/vrms/*.java
java -cp out vrms.Main
```

Requires a JDK with Swing support (Java 17 or newer recommended).
