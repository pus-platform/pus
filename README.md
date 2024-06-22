# PUs Project

This repository contains three distinct projects: a backend monolithic application, a frontend application, and a backend microservices architecture. Below are the detailed descriptions, setup instructions, and usage guidelines for each project.

## Introduction
PUs is a Java Spring Boot application designed to create a community platform for students. It includes features like the main features of a social media platform and more.

### Features
- **Social Media Features**: Everything you expect from a social media platform: follows, posts, interactions, stories, real time messaging and notifications, and more.
- **Community Interaction**: Connect with students from the same university through posts and stories.
- **Major and Section Communities**: Group chats based on majors and sections.

## Table of Contents
1. [Backend Monolithic Application](#backend-monolithic-application-pus)
2. [Frontend Application](#frontend-application)
3. [Backend Microservices Architecture](#backend-microservices-architecture)
4. [Contributing](#contributing)
5. [License](#license)
6. [Contact](#contact)

## Backend Monolithic Application (PUs)

### Prerequisites
- Java
- Maven
- SQL

### Setup Instructions
1. Clone the repository:
   ```sh
   git clone https://github.com/pus-platform/pus
   ```
2. Navigate to the project directory:
   ```sh
   cd pus/backend
   ```
3. Install dependencies:
   ```sh
   mvn install
   ```
4. Start the server:
   ```sh
   mvn spring-boot:run
   ```
5. Import the database setup:
   - Import the `pus.sql` file into your SQL database.

---

## Frontend Application

The frontend application is designed using react providing enhanced user experience.

### Prerequisites
- Node.js
- npm

### Setup Instructions
1. Navigate to the project directory:
   ```sh
   cd pus/frontend
   ```
2. Install NPM packages:
   ```sh
   npm install
   ```
3. Start the development server:
   ```sh
   npm run dev
   ```

### Built With
- React
- React Query
- Tailwind CSS
- Vite
- Flowbite React
- Lucide Icons

---

## Backend Microservices Architecture

This project demonstrates a microservices architecture using Spring Boot, including components such as an API Gateway, Configuration Server, Discovery Server, and multiple microservices.

### Components
- **API Gateway**: Entry point for client requests.
- **Config Server**: Centralized configuration management.
- **Discovery Server**: Service discovery and communication management.
- **User Service**: Manages user information and authentication.
- **Post Service**: Handles posts.
- **Story Service**: Manages stories.
- **Upload Service**: File upload and storage.

### Prerequisites
- Java
- Maven
- SQL
- Docker and Docker Compose

### Setup Instructions
1. Build & Run each service, make sure to run discovery service first, then config service, then the others:
   ```sh
   cd service-directory
   ./mvnw clean install spring-boot:run
   ```

### Accessing Services
Each service is accessed through the API Gateway on default ports:
- **API Gateway**: `http://localhost:8080`
- Other services are configured and accessed via the API Gateway.

### Building Docker

navigate to the root directory of the project
```sh
run docker compose up
```

---

## License

Distributed under the MIT License. See `LICENSE` for more information.

### Authors
- [Hasan Zawahra](https://github.com/HasanZawahra) or via [Linkedin](https://www.linkedin.com/in/hasan-zawahra/)
- [Ehab Maali](https://github.com/71iq) or via [Linkedin](https://www.linkedin.com/in/ehab-maali/)
- [Nadine AbuOdeh](https://github.com/nadineabuodeh) or via [Linkedin](https://www.linkedin.com/in/nadine-abuodeh/)

