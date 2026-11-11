<div align="center">

<img src="src/main/resources/image/logoHitam.png" alt="HerPathway" width="260" />

### A mentoring platform that connects women with the mentors who can help them grow.

![Java](https://img.shields.io/badge/Java-17+-ED8B00?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-1E88E5)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)

</div>

---

HerPathway is a desktop app for mentoring. Mentees look for a mentor and apply to them. Mentors review the applications and accept or decline them. Once a mentee is accepted, the two can chat. Everyone shares a discussion forum and a learning library, and users write all of its content themselves.

## ✨ Features

| | Feature | What it does |
|---|---|---|
| 🔍 | **Find a mentor** | Mentees browse mentor profiles and apply with a short questionnaire. Each mentee can apply to a mentor only once. |
| ✅ | **Application review** | Mentors read incoming requests and accept or decline them. Each mentee gets one assigned mentor. |
| 📊 | **Dashboard** | A home screen for each role. Mentees can see whether each application is *waiting*, *accepted* or *declined*. |
| 💬 | **Messaging** | Private chat between a mentor and the mentees they accepted. Messages are saved to the database and appear live when the chat server is running. |
| 🗣️ | **Forum** | Start discussions in categories, reply, like, search, and delete your own posts. |
| 📚 | **Learning library** | Articles, videos and e-books. Mentors and organizations publish them, and everyone can read them. |
| 🔐 | **Secure accounts** | Passwords are hashed with PBKDF2. Accounts that still have plain-text passwords are upgraded automatically the next time they log in. |

## 👥 Roles

```
            ┌──────────────┐   applies to    ┌──────────────┐
            │    Mentee    │ ──────────────▶ │    Mentor    │
            │              │ ◀────────────── │              │
            └──────┬───────┘  accept / chat  └──────┬───────┘
                   │                                │
                   ▼                                ▼
            ┌───────────────────────────────────────────────┐
            │       Forum  ·  Learning library  ·  Home     │
            └───────────────────────────────────────────────┘
                                   ▲
                                   │ publishes content
                           ┌───────┴──────┐
                           │ Organization │
                           └──────────────┘
```

- **Mentee:** finds a mentor, applies, follows the status of each application, chats with their mentor, and reads learning content.
- **Mentor:** reviews and accepts mentees, chats with them, and publishes learning content.
- **Organization:** manages its profile, takes part in the forum, and publishes learning content.

## 🚀 Getting started

### Prerequisites

- **JDK 17 or newer**
- **Maven 3.8 or newer**
- **MySQL 8** running locally

### 1. Create the database

```sql
CREATE DATABASE herpath;
```

You only need to create the database itself. The app creates every table the first time it connects (see `SchemaMigrator` and `src/main/resources/db/schema.sql`).

### 2. Configure credentials

```bash
cp db.properties.example db.properties
```

```properties
db.url=jdbc:mysql://localhost:3306/herpath
db.user=root
db.password=your-password
```

> `db.properties` is git-ignored. You can also set `HERPATH_DB_URL`, `HERPATH_DB_USER` and `HERPATH_DB_PASSWORD` as environment variables instead.

### 3. Run it

```bash
# optional: start the chat server for live messaging (port 12345)
mvn exec:java

# start the app
mvn javafx:run
```

Run both commands from the project root, because uploaded files are stored in `uploads/` relative to the working directory.

**VS Code:** open the folder and let the Java extension import `pom.xml`. Then use the **ChatServer** and **HerPathway App** launch configurations.

## 🏗️ Architecture

Dependencies only flow in one direction:

```
controller  ──▶  dao  ──▶  database  ──▶  config
 (UI logic)     (SQL)     (JDBC + schema)  (credentials)
```

Controllers never write SQL or open connections themselves. All queries live in the DAO layer.

```
src/main
├── java/com/herpathway
│   ├── app/          Main (JavaFX bootstrap) and Launcher (plain classpath entry point)
│   ├── config/       DatabaseConfig: reads credentials from env vars or db.properties
│   ├── database/     DatabaseConnection, SchemaMigrator, StarterContent
│   ├── dao/          All SQL: UserDao, MentorDao, MenteeDao, ChatDao, ForumDao, LearningDao, ...
│   ├── model/        Plain data objects (MenteeProfile, MentorProfile, Questionnaire, ...)
│   ├── navigation/   View (screen registry) and Navigator (screen switching)
│   ├── session/      SessionManager: the currently logged-in user
│   ├── chat/         ChatServer (socket server) and ChatClient
│   ├── util/         Alerts, Ui, Images, FileStorage, ImageCompressor, Passwords
│   └── controller/   FXML controllers
└── resources
    ├── com/herpathway/view/   FXML screens
    ├── css/app.css            The single stylesheet
    ├── db/schema.sql          Table definitions, applied automatically
    └── image/                 UI images
```

## 🛠️ Contributing

A few conventions keep the codebase consistent:

| Task | How |
|---|---|
| **Switch screens** | `Navigator.goTo(View.X)` replaces the whole window. `Navigator.show(View.X)` replaces the content area inside the shell. `Navigator.openWindow(View.X, "Title", ...)` opens a new window. |
| **Pass data to a screen** | `Navigator.<ProfilMentorController>show(View.MENTOR_PUBLIC_PROFILE, c -> c.setMentorData(m));` |
| **Add a screen** | Put the FXML in `resources/com/herpathway/view/` and the controller in `controller/`, then register it in `View`. Sidebar menus for each role are defined in `ShellController`. |
| **Add a table or column** | For a new table, add `CREATE TABLE IF NOT EXISTS` to `schema.sql`. For a new column on an existing table, add `addColumnIfMissing(...)` to `SchemaMigrator`. |
| **Add a query** | Add a method to the matching DAO, not to the controller. |
| **Styling** | Use the existing classes in `app.css` (`page`, `card`, `page-title`, `btn-primary`, `btn-secondary`, `badge`, ...) and avoid inline `style="..."`. |
| **Images** | Store them in `resources/image/` and reference them from FXML with `@../../../image/name.png`. |
| **Messages and uploads** | Use `Alerts.info(...)` / `Alerts.error(...)` for messages and `FileStorage.choose(...)` followed by `FileStorage.store(...)` for uploads. |

---

<div align="center">
Made with 💜 to help more women find their path.
</div>
