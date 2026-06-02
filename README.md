# ProQuiz — Premium JavaFX Assessment Suite
### Group 2 Project — Department of Computer Science & Software Engineering
**Assessment Deadline:** On or before June 3rd, 2026

---

## 👥 Group 2 Member Registration Roster

| # | Student Full Name | Registration Number | Role / Contribution |
|---|-------------------|---------------------|----------------------|
| 1 | **Abubakar Ismail** | `CIS/STE/23/1011` | Lead Logic Engineer / QA |
| 2 | **Nasir Aminu Musa** | `CIS/STE/23/1013` | Technical Documentation Analyst |
| 3 | **Nasir Saminu Muktar** | `CIS/STE/23/1014` | Lead GUI Layout Developer |
| 4 | **Ahmad Jibril** | `CIS/STE/23/1015` | JavaFX Component Designer |
| 5 | **Abdulhakeem Yushau** | `CIS/STE/23/1018` | Specifications Analyst |
| 6 | **Usman Iliyasu** | `CIS/STE/23/1019` | UI Theme & CSS Specialist |
| 7 | **Imran Muhammad** | `CIS/STE/23/1020` | Native Diagnostic Systems |

---

## 🚀 Key Features

* **Modular Clean Code Architecture**: Encapsulates data models, grading enums, state engines, and visual scenes directly under [App.java](src/App.java) using static nested classes, enabling an ultra-flat directory structure without deep packages.
* **Premium Dark Glassmorphism UI**: Uses a high-fidelity Charcoal/Slate linear gradient styling theme with glowing indigo/cyan cards, custom progress indicators, and hover micro-animations.
* **Real-time Live Scoring**: Tracks and calculates the user's correct answers dynamically as they click option cards, updating the live progress header in real-time.
* **Smart Dashboard**: Displays a beautiful radial arc percentage completion accuracy ring alongside a grade-specific badge:
  * **Score 7 - 10**: Grade **A** *(Outstanding)*
  * **Score 6**: Grade **B** *(Very Good)*
  * **Score 5**: Grade **C** *(Good)*
  * **Score 4**: Grade **D** *(Satisfactory)*
  * **Score 3**: Grade **E** *(Pass)*
  * **Score 0 - 2**: Grade **F** *(Needs Improvement)*
* **Detailed Response Review**: Displays a scrollable card checklist highlighting the user's selected choices, the correct options, and corresponding Emerald Check (✓) or Rose Cross (✗) status indicators.
* **Native Zero-Dependency Diagnostic Tests**: Includes 6 comprehensive automated diagnostic tests inside `App.AppTest` using pure native Java assertions, completely eliminating the need for third-party libraries (like JUnit) on the classpath!

---

## 🛠️ System Requirements & Dependencies

* **Java Development Kit (JDK)**: JDK 21 or later.
* **JavaFX SDK**: JavaFX Controls and FXML.
* **Build System**: Maven (optional, but highly recommended for easy dependency resolution).

---

## 💻 Compilation & Running Guide

### Method A: Standard IDE (IntelliJ, Eclipse, NetBeans, VS Code) — *Recommended*
1. Open your IDE and import this project folder `QuizApplication-Group2`.
2. Locate `src/App.java` under the `src` folder.
3. Right-click `App.java` and select **Run 'App.main()'** (or click the green Play button). Your IDE automatically configures the JavaFX SDK modules and launches the GUI instantly!

### Method B: Easy Command Line (via Maven)
If you have Maven on your terminal, Maven handles all dependency resolution and JavaFX module path configs behind the scenes. Run:
```bash
mvn javafx:run
```

### Method C: Standalone Command Line (Direct JDK Compilation)
If you are compiling directly from a command terminal without Maven, provide your local path to the JavaFX SDK libraries:
* **Compile**:
  ```bash
  javac --module-path /path/to/javafx/lib --add-modules javafx.controls src/App.java
  ```
* **Run the Application GUI**:
  ```bash
  java --module-path /path/to/javafx/lib --add-modules javafx.controls -cp src App
  ```

---

## 🧪 Running Diagnostic Tests

To verify modular business logic, bounds assertions, parameter constraints, and grading mappings, we implemented two simple test triggers:

### Trigger 1: Graphic UI Diagnostics Button
On the welcome login screen, click the **"RUN TESTS"** button next to "Start Assessment". This will run all 6 diagnostics instantly and display a popup:
`✓ All 6 automated diagnostic tests passed successfully!`

### Trigger 2: CLI Command Line Flag
Run the application by passing a `-test` or `--test` argument:
```bash
java --module-path /path/to/javafx/lib --add-modules javafx.controls -cp src App -test
```
This executes the suite on the terminal output console and exits instantly:
```text
========================================================
  STARTING AUTOMATED NATIVE ASSESSMENT DIAGNOSTICS      
========================================================
Running testInitialState()................. [ PASSED ]
Running testUserNameValidation()............ [ PASSED ]
Running testNavigationBounds()............. [ PASSED ]
Running testAnswerRegistration()........... [ PASSED ]
Running testGradingMapping()............... [ PASSED ]
Running testResetState()................... [ PASSED ]
========================================================
  SUCCESS: ALL 6 DIAGNOSTIC TEST CASES COMPLETED        
========================================================
```

---

## 🗂️ Project Map

```text
QuizApplication-Group2/
├── .gitignore
├── pom.xml         <-- Compiler & Maven coordinates
├── README.md       <-- Roster & Project documentation
└── src/
    ├── App.java    <-- Unified single-source file (GUI + Models + Tests)
    └── styles.css  <-- Dark Glassmorphism Styling sheet
```
