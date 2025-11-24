## Trucking Management App

A lightweight trucking operations to store and manage truck loads, invoices, payments using Spring Boot + Thymeleaf + H2. 
User can view active loads, create invoices from loads, make payments and notify stakeholders about an event. 

### How to Run the Project
1. Change the directory to /trucking:
    'cd to trucking'
2. Run this command in the project folder:
    './mvnw spring-boot:run'
    'mvn clean spring-boot:run'
3. Open the browser @ http://localhost:8000

### Pages
* /loads
* /invoices
* /customers/{ref}
* etc

### Database
* H2 Dev Database
* H2 console @ http://localhost:8080/h2

### Design Patterns
* Builder: LoadBuilder
* Adapter: Payment gateways
* Bridge: Notifications
