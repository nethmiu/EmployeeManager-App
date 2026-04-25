# Employee Manager App

A modern, high-performance Android application designed for streamlined employee record management. This project demonstrates advanced Android development practices, focusing on clean architecture, real-time data synchronization, and a seamless user experience.

## Project Overview
The Employee Manager App allows administrators to manage a workforce database through a centralized digital profile system. It bridges the gap between complex enterprise tools and user-friendly mobile interfaces, offering full CRUD (Create, Read, Update, Delete) capabilities coupled with offline data resilience.

- **Platform:** Android (Native)
- **Primary Language:** Kotlin
- **UI Approach:** Jetpack Compose (Declarative UI)
- **Architecture:** MVVM (Model-View-ViewModel)

## Key Features
- **User Authentication:** Secure access control using shared preferences for persistent login sessions.
- **Dynamic Employee Management:** Full lifecycle management of employee profiles including avatars, designations, and departments.
- **Offline Resilience:** Integrated local caching mechanism using GSON and SharedPreferences to ensure data availability without internet connectivity.
- **Advanced Filtering:** Real-time search functionality and department-wise filtering using Compose State and derived state optimizations.
- **Visual Reporting:** Dedicated reporting module providing high-level summaries of the workforce distribution.
- **Theming:** Full support for Material 3 Dynamic Theming, including Dark Mode and Light Mode.

## Technology Stack
- **Jetpack Compose:** For building a modern, responsive UI.
- **Kotlin Coroutines & Flow:** For handling asynchronous background tasks.
- **Retrofit & OkHttp:** For robust REST API communication.
- **ViewModel & LiveData/State:** To maintain architectural separation of concerns and handle configuration changes.
- **Coil:** For optimized and reactive image loading.
- **GSON:** For JSON serialization/deserialization and local data persistence.

## API Endpoints
The application communicates with a RESTful backend. Below are the core endpoints used:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/employees` | Retrieves a full list of employees from the server. |
| POST   | `/employees` | Adds a new employee profile to the database. |
| PUT    | `/employees/{id}` | Updates existing employee records by ID. |
| DELETE | `/employees/{id}` | Permanently removes an employee record. |

## Setup and Run Instructions

### Prerequisites
- Android Studio Ladybug or newer.
- JDK 17 or higher.
- An Android device or emulator running API level 24 (Nougat) or higher.

### Installation
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/nethmiu/EmployeeManager-App.git](https://github.com/nethmiu/EmployeeManager-App.git)

   Open the project:
2.Launch Android Studio and select "Open" to locate the cloned directory.

3.Sync Project:
Wait for the Gradle sync to complete and download all necessary dependencies (Retrofit, Compose, etc.).

4.Configuration:
Ensure the Base URL in RetrofitInstance.kt is correctly pointing to your active backend server.

5.Run:
Click the "Run" button in Android Studio to deploy the application to your device or emulator.

## Project Structure
data/: Contains data models and API service configurations.

ui/: Contains all Compose-based UI components, screens, and themes.

viewmodel/: Implements business logic and data state management.


## Download Apk file - https://drive.google.com/file/d/1KvxTT5cWV4TxIsCdbNhzHBuihuDi2tLN/view?usp=sharing

## App Vedio - https://drive.google.com/file/d/1WMc_VEq3J0xWXRfVAT4YjtBUDIr4WcPn/view?usp=sharing


<img width="398" height="819" alt="image" src="https://github.com/user-attachments/assets/0aabd4a0-e5e9-4cfc-8b1b-c48446200d52" />

<img width="407" height="830" alt="image" src="https://github.com/user-attachments/assets/8633ad6a-def9-48ce-bb92-de495f218890" />

<img width="403" height="832" alt="image" src="https://github.com/user-attachments/assets/8d82da5a-60ae-4a3b-aab9-9862e4064a77" />

<img width="405" height="822" alt="image" src="https://github.com/user-attachments/assets/537569e9-f439-4810-b48c-089608ac1ed7" />

<img width="403" height="830" alt="image" src="https://github.com/user-attachments/assets/62f499bf-8e60-4bf3-adc6-a6b8d1660150" />

<img width="388" height="826" alt="image" src="https://github.com/user-attachments/assets/0823ac7f-6a91-45ee-abdc-84b92f008fd2" />

<img width="404" height="830" alt="image" src="https://github.com/user-attachments/assets/7d24faf0-8e61-4a26-973a-20b31a0176d5" />

<img width="397" height="827" alt="image" src="https://github.com/user-attachments/assets/83475b71-59c6-403f-9742-bc502cd9b4a7" />

<img width="419" height="836" alt="image" src="https://github.com/user-attachments/assets/325d3d44-91a2-4126-9f0b-8c000656e098" />

<img width="407" height="827" alt="image" src="https://github.com/user-attachments/assets/47377ed7-1d7d-4274-95bf-e70341dcda39" />

<img width="422" height="837" alt="image" src="https://github.com/user-attachments/assets/017c20c9-e287-47b5-9106-249a2c39f437" />

<img width="426" height="835" alt="image" src="https://github.com/user-attachments/assets/903da664-0e8c-4eb9-8f2c-d3b8e78777aa" />










