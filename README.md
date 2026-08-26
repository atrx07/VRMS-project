# VRMS - Vehicle Rental Management System

A Java Swing micro project for managing vehicle rentals.

## Current milestone

Second phase: functional pages with local file storage. Database integration is intentionally not used yet.

Implemented:
- Customer login and registration
- Separate admin login
- Persistent local user accounts
- Simple session state after login
- Card-based customer vehicle catalog
- Vehicle owner username shown on catalog cards
- List a vehicle for rent
- My Vehicles page
- Rent Vehicle page with date-based total calculation
- My Rentals page
- Local rental records and vehicle availability updates
- Admin vehicle catalog
- Pending approval notification badge
- Separate pending approvals page
- Approve or reject vehicle listings
- Admin delete action for available catalog vehicles
- Local runtime files for users, vehicles, and rentals

## Local data

On first run, VRMS automatically creates this folder in the project root:

```text
data/
├── users.txt
├── vehicles.txt
└── rentals.txt
```

The `data/` directory is ignored by Git, so local accounts, vehicle records, and rentals are not committed to the repository.

A default admin account is created automatically:

```text
Email: admin@vrms.com
Password: admin123
```

Customers register normally through the registration page. Vehicle listings begin with `PENDING` approval status. After admin approval, the listing appears in the customer catalog and the admin catalog.

All approved and available vehicles are visible in the customer catalog, including the current customer's own listings. The owner username is shown on every card. A customer's own vehicle is visible but cannot be rented by that same customer.

When another customer confirms a rental, the rental is stored in `rentals.txt` and the vehicle changes from `AVAILABLE` to `RENTED`, removing it from the available customer catalog.

## Run

From the repository root:

```bash
javac -d out src/vrms/*.java
java -cp out vrms.Main
```

Requires a JDK with Swing support (Java 17 or newer recommended).
