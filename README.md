# 🚀 ChallengeMe Project

## Contributors:

- [Emilia Alexandrescu](https://github.com/Andremyyy/) - Product Owner
- [Roger Bostan](https://github.com/Roger1nho) - Scrum Master
- [Calin Befu](https://github.com/PrimeCalinBefu)
- [Alex Carpiuc](https://github.com/AlexCarpiuc)
- [Iustin Bivolaru](https://github.com/Iustinache)
- [Emanuel Corlat](https://github.com/corlatemanuel)
- [Stefan Alexa](https://github.com/stefanalexa25)
- [Tudor Buta](https://github.com/Tudor1508)

---

This guide explains how to **set up and run the ChallengeMe project** (backend + frontend) locally. Follow the steps carefully.

## 1️⃣ Prerequisites

Before starting, make sure you have these installed:

### Java (Backend)
- **Java JDK 21**
```bash
java -version
```
### Node.js & npm (Frontend)
- **Node.js 20.x (LTS recommended)**
- **npm 9.x (comes with Node.js)**
```bash
node -v
npm -v
```

### Angular CLI
```bash
npm install -g @angular/cli
ng version
```
### Git
```bash
git --version
```

## 2️⃣ Clone the Repository

There are two ways to clone the repository:

### Option 1: Using IntelliJ IDEA (recommended)

- Open IntelliJ IDEA.

- Go to File → New → Project from Version Control → Git.

- In Git Repository URL, paste:

```bash
https://github.com/ChallengeMe-Team/ChallengeMe.git
```

- Choose the directory where you want to clone the project.

- Click Clone.

- Once cloned, IntelliJ will open the project automatically.

### Option 2: Using Terminal / Command Line
```bash
git clone https://github.com/ChallengeMe-Team/ChallengeMe.git
cd ChallengeMe
```

✅ Either method will give you the full project (backend + frontend) ready to set up.

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

## 5️⃣ Git Workflow

- **Protected `main` branch** – do **not commit directly**.
- Create a new branch for your work. Branch names **must start with the Jira story identifier**, e.g., `NR-1-feature-description`:
```bash
git checkout -b NR-1-my-feature
```
- Push changes:
```bash
git add .
git commit -m "Describe your changes"
git push origin NR-1-my-feature
```
- Open a Pull Request on GitHub targeting main.
- Make sure the branch name clearly indicates the story it relates to.

## 6️⃣ Notes & Tips
- .gitignore is pre-configured - do not commit node_modules, /dist, .idea, etc.
- Use environment variables in environment.ts / environment.prod.ts to centralize backend URLs.
- Backend default port: 8080, frontend default port: 4200
- Make sure these ports are free before running.
- If something breaks, check:

  a) Node.js version

  b) Java version

  c) Proxy configuration in frontend
