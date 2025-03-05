# Banking Application - Microservices Architecture

## Overview
This **Banking Application** enables users of different roles (**Admin, Banker, and Client**) to manage bank accounts, request cards, and perform transactions. The application follows a **microservices architecture**, ensuring modularity, scalability, and security.

## Features

### Authentication Service
- Secure login using **JWT (JSON Web Token)** authentication.
- Role-based access control (RBAC) with **Spring Security**.

### User Management Service
- **Admin** can create/update/delete **Bankers**.
- **Banker** can create/update/delete **Clients**.
- Users are managed with **Spring Data JPA** and **MySQL**.

### Banking & Transaction Service
- **Bank Accounts**
  - Clients can request a **Current Account**, which requires Banker approval.
  - Supports **IBAN, Balance, Currency (EUR), and Interest Calculation**.
- **Cards**
  - Clients can request a **Debit Card** (linked to an active Current Account).
  - Clients can request a **Credit Card**, which requires salary verification and Banker approval.
  - Approved Credit Cards automatically create a linked **Technical Account**.
- **Transactions**
  - Users can transfer money between accounts using **IBAN**.
  - Validation ensures sufficient balance for Debit Cards.
  - Credit Card transactions support **negative balances with a credit limit**.
  - **Interest calculations** are applied based on salary brackets.
  - Each account maintains its own **debit (-) or credit (+) transaction records**.

## Technology Stack

### Backend
- **Java 23**
- **Spring Boot (Microservices Architecture)**
- **Spring Security (JWT Authentication)**
- **Spring Data JPA & Hibernate**
- **MySQL**
- **Maven (Dependency Management)**
- **Logging (SLF4J + Logback)**

### Version Control & Deployment
- **Git & GitHub** for version control.


