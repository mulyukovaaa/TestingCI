package org.itmo.testing.lab2.integration;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import org.itmo.testing.lab2.controller.UserAnalyticsController;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserAnalyticsIntegrationTest {

    private Javalin app;
    private int port = 7000;

    @BeforeAll
    void setUp() {
        app = UserAnalyticsController.createApp();
        app.start(port);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @AfterAll
    void tearDown() {
        app.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Тест регистрации пользователя")
    void testUserRegistration() {
        given()
                .queryParam("userId", "user1")
                .queryParam("userName", "Alice")
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body(equalTo("User registered: true"));
    }

    @Test
    @Order(2)
    @DisplayName("Регистрация без параметров")
    void testRegisterUser_MissingParams() {
        given()
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }

    @Test
    @Order(3)
    @DisplayName("Регистрация без имени")
    void testRegisterUser_NameMissingParams() {
        given()
                .queryParam("userId", "user1")
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }

    @Test
    @Order(4)
    @DisplayName("Регистрация без id")
    void testRegisterUser_IdMissingParams() {
        given()
                .queryParam("userName", "Alice")
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body(equalTo("Missing parameters"));
    }

//    @Test
//    @Order(5)
//    @DisplayName("Регистрация с пустым id")
//    void testRegisterUser_IdEmptyParams() {
//        given()
//                .queryParam("userId", "")
//                .queryParam("userName", "Alice")
//                .when()
//                .post("/register")
//                .then()
//                .statusCode(500)
//                .body(equalTo("Empty id"));
//    }
//
//    @Test
//    @Order(6)
//    @DisplayName("Регистрация с пустым именем")
//    void testRegisterUser_NameEmptyParams() {
//        given()
//                .queryParam("userId", "12")
//                .queryParam("userName", "")
//                .when()
//                .post("/register")
//                .then()
//                .statusCode(500)
//                .body(equalTo("Empty id"));
//    }

    @Test
    @Order(7)
    @DisplayName("Тест записи сессии")
    void testRecordSession() {
        LocalDateTime now = LocalDateTime.now();
        given()
                .queryParam("userId", "user1")
                .queryParam("loginTime", now.minusHours(1).toString())
                .queryParam("logoutTime", now.toString())
                .when()
                .post("/recordSession")
                .then()
                .statusCode(200)
                .body(equalTo("Session recorded"));
    }

    @Test
    @Order(8)
    @DisplayName("Запись сессии с некорректными датами")
    void testRecordSession_InvalidDates() {
        given()
                .queryParam("userId", "123")
                .queryParam("loginTime", "INVALID")
                .queryParam("logoutTime", "2024-02-10T11:30:00")
                .when()
                .post("/recordSession")
                .then()
                .statusCode(400)
                .body(containsString("Invalid data"));
    }

    @Test
    @Order(9)
    @DisplayName("Запись сессии без времени")
    void testRecordSession_TimeMissingParams() {
        given()
                .queryParam("userId", "123")
                .when()
                .post("/recordSession")
                .then()
                .statusCode(400)
                .body(containsString("Missing parameters"));
    }

    @Test
    @Order(10)
    @DisplayName("Запись сессии без id")
    void testRecordSession_MissingParams() {
        given()
                .queryParam("loginTime", "2024-02-09T11:30:00")
                .queryParam("logoutTime", "2024-02-10T11:30:00")
                .when()
                .post("/recordSession")
                .then()
                .statusCode(400)
                .body(containsString("Missing parameters"));
    }

    @Test
    @Order(11)
    @DisplayName("Тест получения общего времени активности")
    void testGetTotalActivity() {
        given()
                .queryParam("userId", "user1")
                .when()
                .get("/totalActivity")
                .then()
                .statusCode(200)
                .body(containsString("Total activity:"))
                .body(containsString("minutes"));
    }

    @Test
    @Order(12)
    @DisplayName("Получение активности без id")
    void testTotalActivity_MissingUserId() {
        given()
                .when()
                .get("/totalActivity")
                .then()
                .statusCode(400)
                .body(equalTo("Missing userId"));
    }

//    @Test
//    @Order(13)
//    @DisplayName("Поиск неактивных пользователей")
//    void testInactiveUsers_Success() {
//        given()
//                .queryParam("days", "30")
//                .when()
//                .get("/inactiveUsers")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON);
//    }
//
//    @Test
//    @Order(14)
//    @DisplayName("Получение метрик активности за месяц")
//    void testMonthlyActivity_Success() {
//        given()
//                .queryParam("userId", "user1")
//                .queryParam("month", "2025-03")
//                .when()
//                .get("/monthlyActivity")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON);
//    }

}
