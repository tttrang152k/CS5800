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
* /loads - View/Create loads. Links to load details and documents
* /invoices - List/Create invoices. Make payments
* /customers/{ref} - Customer overview: loads, sent invoices, notifications

### Database
* H2 Database
* H2 console @ http://localhost:8080/h2-console

### Design Patterns
* Builder: LoadBuilder
* Adapter: Payment gateways
* Bridge: Notifications
