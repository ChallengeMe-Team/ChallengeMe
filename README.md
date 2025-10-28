# 🚀 ChallengeMe Project

This guide explains how to **set up and run the ChallengeMe project** (backend + frontend) locally. Follow the steps carefully.

---

## 1️⃣ Prerequisites

Before starting, make sure you have these installed:

### Java (Backend)
- **Java JDK 21**
```bash
java -version
```
## Node.js & npm (Frontend)
- **Node.js 20.x (LTS recommended)**
- **npm 9.x (comes with Node.js)**
```bash
node -v
npm -v
```

## Angular CLI
```bash
npm install -g @angular/cli
ng version
```
## Git
```bash
git --version
```

## 2️⃣ Clone the Repository

## 3️⃣ Backend Setup
Navigate to backend:
```bash
cd backend
```
Run Spring Boot application:
```bash
./gradlew bootRun       # Linux/Mac
gradlew.bat bootRun     # Windows
```
✅ Backend runs on: http://localhost:8080

## 4️⃣ Frontend Setup
Navigate to frontend:
```bash
cd frontend
```
Install dependencies:
```bash
npm install
```
Run Angular development server:
```bash
ng serve --open
```
✅ Frontend runs on: http://localhost:4200
