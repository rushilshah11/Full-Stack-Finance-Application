# 💹 Full-Stack Finance Application

A full-stack web application designed to simulate a real-world stock trading and portfolio management platform.  
Users can track **real-time stock quotes**, manage a **virtual cash balance**, and **buy/sell shares**, with all data persisted in a **MySQL database**.

---

## 🚀 Features

The application provides a robust set of features for simulating financial trading:

- 🔐 **User Authentication:** Secure sign-up and login functionality.  
- 🔎 **Real-Time Stock Search:** Search for any publicly traded stock using its ticker symbol (e.g., `GOOG`, `AAPL`).  
- 📈 **Live Market Data:** Displays current stock quotes (price, open, high, low, daily change) and essential company info (IPO date, market cap), powered by the **Finnhub API**.  
- 💰 **Simulated Trading:** Buy and sell shares with real-time validation (ensures sufficient cash or shares).  
- 🏦 **Cash Management:** Users start with a virtual balance that updates dynamically with each trade.  
- 📊 **Portfolio Management:** View includes:
  - Current cash balance  
  - Total portfolio value (cash + current holdings)  
  - Individual holdings with quantity, cost basis, and daily profit/loss  
- 💾 **Persistent Data:** User accounts, balances, and transactions stored in **MySQL**.

---

## 💻 Technology Stack

### **Frontend**
- **HTML**, **CSS**, **JavaScript**
- **AJAX (XMLHttpRequest)** for asynchronous calls to backend servlets

### **Backend (Server-Side)**
- **Java Servlets (JSP/Servlet)** running on **Apache Tomcat**
- Core servlets:
  - `LoginServlet`
  - `SignupServlet`
  - `HomeServlet` (handles stock data)
  - `TradeServlet2` (handles buy/sell transactions)
  - `PortfolioServlet`
- **Gson** for JSON serialization/deserialization  
- **Finnhub API** for live stock market data

### **Database Layer**
- **MySQL** for persistent storage  
- **JDBC (MySQL Connector/J)** for database connectivity

---

## 🗂️ Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── rushilrs_CSCI201_Assignment4/
│   │   │       ├── *.java                # Servlets and data model classes
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       ├── *.html                    # Login, home, portfolio pages
│   │       └── *.css                     # Styling files
├── create.sql                             # Database schema definition
├── gson-2.9.1.jar
├── mysql-connector-j-8.3.0.jar
└── (Eclipse config and build files)
```

---

## 🧠 Software Engineering Principles

### 🏗️ Layered Architecture (Full-Stack)
The application follows a **three-tier architecture** with clear separation of concerns:
- **Presentation Layer:** HTML, CSS, and JavaScript handle the user interface and client-side interactions.  
- **Business Logic Layer:** Java Servlets process user requests, perform validations, and manage application logic.  
- **Data Layer:** MySQL stores persistent data such as user accounts, transactions, and portfolio information.

This structure ensures scalability, maintainability, and modular development.

---

### 🌐 API Integration
The core functionality integrates with the **Finnhub REST API** to fetch **real-time market data**, including stock prices, company information, and historical data.  
This demonstrates **interoperability** and real-world **API consumption** skills, allowing dynamic updates within the application.

---

### 🧩 Data Modeling (OOP)
The application models its domain and external data using Java classes:
- `User`
- `FinnHubObject`
- `FinnHubDescription`

These classes encapsulate key entities and promote **type safety**, **reusability**, and **clean code organization** through **object-oriented design**.

---

### 🔒 Security (Basic)
Security measures include:
- Using **`PreparedStatement`** for all SQL queries to prevent **SQL injection attacks**.  
- Basic user input sanitization and validation both on the **client** and **server** sides.  

This ensures the system adheres to essential web security principles.

---

### ✅ Input Validation
Validation is implemented at multiple layers:
- **Frontend (Client-Side):**  
  Regular expressions ensure correct email format and prevent empty inputs.  
- **Backend (Server-Side):**  
  Checks for missing or blank inputs, sufficient balance for trades, and non-zero quantities before processing transactions.

This redundancy ensures **data integrity** and prevents **invalid operations**.

---

### 📊 Data Aggregation Logic
The `PortfolioServlet` performs **aggregation logic** using Java **HashMaps** to compute:
- Consolidated positions per stock symbol  
- Average cost per share  
- Real-time profit/loss calculations  

This logic ensures that portfolio metrics are accurate and reflect real-world financial principles — a crucial component for trading simulations.

---

## 🧑‍💻 Author
**Rushil Shah**  
📫 [LinkedIn](https://linkedin.com/in/rushilshahh)
💼 Portfolio

