# TourGuide API - Java Project 8

## 1. Project Description

**TourGuide** is a Java and Spring Boot back-end application that simulates an API for a tour guide service. The application's purpose is to track user locations in real-time, suggest nearby tourist attractions, and manage a rewards system.

The main goal of this project was to refactor an existing application to **drastically optimize its performance** to handle a very high load (up to 100,000 users) concurrently and efficiently.

---

## 2. Key Features

* **Real-Time Tracking**: Locates and updates the position of multiple users concurrently.
* **Nearby Attractions**: For a given user, returns the 5 closest tourist attractions, along with the distance and associated reward points.
* **Rewards Calculation**: Automatically calculates and assigns reward points when a user visits an attraction.
* **Trip Deals**: Generates a list of offers from travel partners based on user preferences and accumulated reward points.
* **REST API**: Exposes a set of endpoints to interact with the application's features.

---

## 3. Technologies & Libraries

This project is built with the following technologies, as defined in the `pom.xml` file:

* **Language**: Java 8
* **Main Framework**: Spring Boot (`2.1.0.RELEASE`)
* **Web Server**: Tomcat (embedded with Spring Boot)
* **Build & Dependency Management**: Apache Maven
* **Testing**: JUnit 5, Mockito
* **External Libraries (provided as .jar files)**:
    * `gpsUtil`: Simulates calls to a GPS location service.
    * `RewardCentral`: Simulates calls to a points calculation service.
    * `TripPricer`: Simulates calls to a trip pricing service.

---

## 4. Architecture & Performance Optimizations

To meet the high-performance requirements, the application's architecture was thoroughly refactored.

### a. Asynchronous Processing with CompletableFuture

The main bottleneck was the sequential processing of users. The solution was to implement a multi-threaded architecture.

* **`ExecutorService`**: A thread pool (`newFixedThreadPool`) is used in `TourGuideService` and `RewardsService` to manage tasks in a controlled manner, avoiding the costly creation of threads on the fly.
* **`CompletableFuture`**: The `trackAllUsersLocation` and `calculateAllUsersRewards` methods launch the location tracking and rewards calculation processes completely asynchronously. This allows for the parallel processing of thousands of users. The performance tests leverage these methods to start all tasks and then wait for their global completion using `CompletableFuture.allOf(...).join()`.

### b. Strategic Caching

The second performance issue stemmed from repeated calls to slow external resources.

* **Attractions Cache**: The list of attractions is fetched **only once** at application startup within the `RewardsService` constructor and is stored in memory. This eliminates thousands of redundant and slow calls to the `gpsUtil` library, making the rewards calculation almost instantaneous, as demonstrated by the `highVolumeGetRewards` test.

### c. Thread Safety

With parallel processing, it was crucial to prevent race conditions.

* **Synchronization**: Methods that modify shared data, such as `user.addUserReward()`, have been marked as `synchronized` to ensure that concurrent access is handled safely, without data corruption.

---

## 5. Installation & Setup

### Prerequisites

* Java Development Kit (JDK) 8 or higher
* Apache Maven 3+

### Steps

1.  **Clone the repository**:
    ```bash
    git clone [https://github.com/KentinTL/JavaPathENProject8.git](https://github.com/KentinTL/JavaPathENProject8.git)
    cd JavaPathENProject8/TourGuide
    ```

2.  **Install local dependencies**:
    The project uses local libraries. Make sure they are correctly referenced in the `pom.xml` via `<systemPath>`.

3.  **Build the project with Maven**:
    This command will download dependencies, compile the code, and run unit tests. Performance tests are excluded from the default build thanks to the `maven-surefire-plugin` configuration.
    ```bash
    mvn clean install
    ```

4.  **Run the application**:
    ```bash
    java -jar target/tourguide-0.0.1-SNAPSHOT.jar
    ```

The application will start and be accessible at `http://localhost:8080`.

---

## 6. API Endpoints

The API is defined in `TourGuideController.java` and exposes the following endpoints:

| Method | Endpoint | Parameters | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | _None_ | Displays a welcome message. |
| `GET` | `/getLocation` | `userName` (String) | Gets the last known location of the user. |
| `GET` | `/getNearbyAttractions` | `userName` (String) | Returns the 5 nearest attractions for the user. |
| `GET` | `/getRewards` | `userName` (String) | Returns the list of rewards earned by the user. |
| `GET` | `/getAllCurrentLocations`| _None_ | Returns the last known location of all users. |
| `GET` | `/getTripDeals` | `userName` (String) | Gets trip deals for the user. |

**Example usage with cURL:**
```bash
curl "http://localhost:8080/getNearbyAttractions?userName=internalUser1"
