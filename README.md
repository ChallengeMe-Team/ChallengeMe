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

## 1️⃣ 🗄️Database Setup (Docker Compose & Flyway)
To run the project locally, you need a PostgreSQL database. 
The recommended method is using **Docker Compose**, which automates the entire infrastructure setup.
### ✅ Recommended Option: PostgreSQL via Docker

#### 1️⃣ Requirements

Install:**Docker Desktop:**
https://www.docker.com/products/docker-desktop/

Check if Docker is installed: (in terminal)
```bash 
docker --version
```

#### 2️⃣ Start the Database

The ```bash docker-compose.yml ``` is located in the ```bash backend ``` directory.

From your terminal, navigate there and run:
```bash
cd backend
docker-compose up -d
```
This creates a PostgreSQL instance with the following default settings:

-container name: challengeme-db

- username: postgres

- password: password

- database: challengeme

- port: 5432

Check if the container is running:
```bash
docker ps
```

#### 3️⃣ Database Migrations (Flyway)
Flyway is an **open-source** database migration tool. 

It allows the team to share database schema changes (like new tables or columns) just like we share code.

**How it works:**

  - When the Backend starts, Flyway scans the ```src/main/resources/db/migration``` folder for SQL scripts.

  - It checks a special table in the database called ```flyway_schema_history```.

  - If a script has a higher version number than what is recorded in the table, Flyway executes it automatically.

**Note**: You no longer need to manually run ``` data.sql``` to create your tables.

The project now uses **_Flyway_** for database versioning and migrations.

Flyway automatically creates the tables when the _Backend application starts_ for the first time.

Migration scripts are located in 
```backend/src/main/resources/db/migration```.

#### 4️⃣ Naming Conventions for Migration Files
Flyway relies on **a strict naming convention** to order migrations correctly. 

If the name is incorrect, the migration will be ignored or cause an error.

**File Format:** ```V<Version>__<Description>.sql```

| Part        | Rule                                                               | Example                              |
|-------------|--------------------------------------------------------------------|--------------------------------------|
| Prefix      | Must start with a capital ```V```                                  | ```V...```                           |
| Version     | Unique numeric version (use dots or underscores)                   | ```V1__```, ```V1.1__```, ```V2__``` |
| Separator   | Two underscores ```(__)```                                         | ```V1__Init```                       |
| Description | Brief text describing the change (words separated by underscores)  | ```V1__Create_users_table```         |
| Extension   | Must end in ```.sql```                                             | ```.sql```                           |

#### 5️⃣ Database Management Commands

```Bash
# Check if the database is running:
docker ps

# Stop the database and remove containers:
docker-compose down

# Check existing tables (to verify Flyway migration):
docker exec -it challengeme-db psql -U postgres -d challengeme -c "\dt"
```

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

**Password**: [Password_123]()

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
