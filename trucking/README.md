## Trucking Management App

A lightweight trucking operations to store and manage truck loads, invoices, payments using Spring Boot + Thymeleaf + H2. 
User can view active loads, create invoices from loads, make payments and notify stakeholders about an event. 

### How to Run the Project
```bash
# 1. Go to the app folder:
cd to trucking

# 2. Start the app (macOS/Linux):
./mvnw spring-boot:run  
mvnw.cmd spring-boot:run  # Windows

# 3. Run tests:
./mvnw test 
# or 
mvnw.cmd test
```
Open your browser @ http://localhost:8080

### Pages
* /loads - View/Create/Sort loads. Links to load/customer details and documents
* /invoices - List/Create invoices. Make payments
* /customers/{ref} - Customer overview: loads, sent invoices, notifications
* /loads/search - Display searched loads

### Database
* H2 Database
* H2 console @ http://localhost:8080/h2-console

### Design Patterns
* Builder: Load Builder
* Adapter: Payment gateways
* Bridge: Notifications
* Strategy: Tax strategies and Sort strategies
* Singleton: Payment 

### New Major Features
1. Loads Sorting - Sort displaying loads by Customer Name, Rate Amount, Pickup/Delivery Date
2. Loads Searching - Search for loads based on Load ID, Customer Name and Load Status

### Project Coding Style
We follow Google Java Style (indent = 4 spaces, braces on same line, 100-column limit) and Clean Code principles:
* Meaningful names for classes, methods, variables (no magic strings/numbers).
*  Small functions, each doing one thing at one level of abstraction.
*  Minimal parameters (prefer objects or enums over multiple primitive flags).
*  Clear dependencies via constructor injection.
*  Domain logic in services, controllers only orchestrate + map to view models.
*  Tests follow test_something_expectation naming and verify one behavior per test.