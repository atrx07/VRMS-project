# VRMS - Vehicle Rental Management System

A Java Swing micro project for managing peer-to-peer vehicle rentals.

## Current milestone

Second phase: functional pages with local file storage. Database integration is intentionally not used yet.

Implemented:
- Customer login and registration
- Separate admin login
- Persistent local user accounts
- Simple session state after login
- Card-based customer vehicle catalog
- Customer menu for catalog actions
- Editable customer profile for name, email, phone, and optional password change
- Vehicle owner username shown on catalog cards
- List a vehicle for rent
- My Vehicles page
- Rent Vehicle page with date-based rental calculation
- Mock Payment page
- Fixed 10% VRMS service fee added to each new rental payment
- Vehicle owner receives the listed rental amount while VRMS retains the service fee
- My Rentals page with payment breakdown
- Return Vehicle action for active rentals
- Local rental records and vehicle availability updates
- Admin vehicle catalog
- Pending approval notification badge
- Separate pending approvals page
- Approve or reject vehicle listings
- Admin delete action for available catalog vehicles
- Admin Earnings page with platform earnings, owner payouts, customer payments, and transaction cards
- Local runtime files for users, vehicles, and rentals

## Customer menu

The main customer catalog keeps the vehicle cards as the focus. Customer actions are grouped under the `Menu` button:

- Refresh Catalog
- My Rentals
- My Vehicles
- List Vehicle
- Profile
- Logout

The Profile page loads the currently signed-in customer's local account information. The customer can update their name, email and phone number, and can optionally enter a new password. Leaving the password field empty keeps the existing password.

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

## Rental and payment flow

The customer selects rental dates on the Rent Vehicle page. The listed daily rate is used to calculate the rental amount.

The Payment page then shows:
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

The Pay button is a mock payment action. No bank, card, UPI, or payment gateway is connected. A successful mock payment creates the rental and changes the vehicle from `AVAILABLE` to `RENTED`.

The renter can later use Return Vehicle from My Rentals. Returning changes the rental status to `RETURNED` and restores the vehicle to `AVAILABLE`.

The admin can open Earnings from the admin catalog to view total VRMS service-fee earnings and the payment history.

Rentals created by older versions of the project remain readable. Older records have a zero platform fee because no payment-fee feature existed when those records were created.

## Run

From the repository root:

```bash
javac -d out src/vrms/*.java
java -cp out vrms.Main
```

Requires a JDK with Swing support (Java 17 or newer recommended).
