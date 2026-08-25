# VRMS - Vehicle Rental Management System

A simple Java Swing micro project for managing vehicle rentals.

## Current milestone

First handoff: authentication UI.

Implemented:
- Customer login screen
- Customer registration screen
- Separate admin login screen
- Basic empty-field validation
- Show/hide password control
- Simple polished Swing interface based on the approved mockup direction

Database authentication and the vehicle catalog will be added in later milestones.

## Run

From the repository root:

```bash
javac -d out src/vrms/*.java
java -cp out vrms.Main
```

Requires a JDK with Swing support (Java 17 or newer recommended).
