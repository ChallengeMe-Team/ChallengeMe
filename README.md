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

This guide explains how to **set up and run the ChallengeMe project** (backend + frontend + database) locally. 

Follow the steps carefully.

## 1️⃣ 🗄️Database Setup (PostgreSQL)
To run the backend locally, you need a PostgreSQL database.
There are two ways to install and run it — **the recommended method is using Docker**.

### ✅ Recommended Option: PostgreSQL via Docker

#### 1️⃣ Requirements

Install:**Docker Desktop:**
https://www.docker.com/products/docker-desktop/

Check if Docker is installed: (in terminal)
```bash 
docker --version
```

#### 2️⃣ Start PostgreSQL using Docker
Run this command:
```bash
docker run --name challengeme-db \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=challengeme \
  -p 5432:5432 \
  -d postgres:15
```
This creates a PostgreSQL instance with:

- username: postgres

- password: password

- database: challengeme

- port: 5432

Check if the container is running:
```bash
docker ps
```
Stop database:
```bash
docker stop challengeme-db
```

Start database (***!!! Everytime the project is started***)
```bash
docker start challengeme-db
```

#### 3️⃣ Populate Database with Sample Data
After starting the database, populate it with initial data so that everyone has the same users, badges, challenges, leaderboard, and notifications.
```bash
# Windows PowerShell 
# !!! CHANGE THE PATH OF THE data.sql FILE WITH THE ONE ON YOUR COMPUTER
type D:\ChallengeMe\backend\data.sql | docker exec -i challengeme-db psql -U postgres -d challengeme
```

### 🟦 Alternative Option: Local PostgreSQL + pgAdmin 4
If you prefer not to use Docker, install PostgreSQL manually.

#### 1️⃣ Install PostgreSQL

Download from: https://www.postgresql.org/download/

Make sure the setup includes:
  - PostgreSQL Server
  - pgAdmin 4

#### 2️⃣ Verify Installation
```bash
psql --version
```

#### 3️⃣ Create the Database in pgAdmin

1. Open **pgAdmin 4**

2. Connect to your local PostgreSQL server (default user: **postgres**)

3. Right-click Databases → Create → Database

4. Name it: ***challengeme***

#### 4️⃣ How to run the databae (using pgAdmin):

``` bash
# Windows
net start postgresql-x64-15
```
```bash
# Linux
sudo service postgresql start
```
```bash
# macOS
brew services start postgresql
```

#### 5️⃣ Populate Database with Sample Data

After starting the database, populate it with initial data so that everyone has the same users, badges, challenges, leaderboard, and notifications.

Using ***pgAdmin***:

- Open **pgAdmin 4** and connect to **challengeme**.

- Go to **Tools → Query Tool**.

- Open ***backend/data.sql*** and run it.
---

## 2️⃣ Prerequisites

Besides PostgreSQL, make sure your system has:

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

## 3️⃣ Clone the Repository

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

## 4️⃣ Backend Setup
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

## 5️⃣ Frontend Setup
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

## 6️⃣ Security & Authentication 

The application is now secured with **JWT (JSON Web Token)**.

### 🔑 Default Login Credentials

After populating the database [data.sql](), you can login with:


**Email**: [emilia@example.com]() (or any other user from the list)

**Password**: [123456]()

#### **_Note_**: If any changes are to be made in [data.sql](), please update the login credentials above

### 🛡️ How it works

#### Login: 
Send POST to `/api/auth/login` with credentials. Receive a JWT Token.

#### Protected Requests: 
For any subsequent request (e.g., [GET /api/challenges]()), the Frontend automatically attaches the token in the header:
[Authorization: Bearer eyJhbGciOi...]()

#### Persistance: 
The token is stored in localStorage to keep you logged in after refresh.

## 7️⃣ Git Workflow

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

### ✅ Notes & Tips
- .gitignore is pre-configured - do not commit node_modules, /dist, .idea, etc.
- Use environment variables in environment.ts / environment.prod.ts to centralize backend URLs.
- Make sure the following ports are free:
  - backend port **8080**, 
  - frontend port **4200**, 
  - PostgreSQL port **5432** 
- If something breaks, check:

  a) Node.js version

  b) Java version

  c) Proxy configuration in frontend

  d) Database connection string
