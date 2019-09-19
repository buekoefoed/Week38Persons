package rest;

import dtomappers.PersonDTO;
import entities.Person;
import utils.EMF_Creator;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import io.restassured.parsing.Parser;

import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
//@Disabled
class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    //Read this line from a settings-file  since used several places
    private static final String TEST_DB = "jdbc:mysql://localhost:3307/persons_test";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static Person p1, p2, p3;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);

        //NOT Required if you use the version of EMF_Creator.createEntityManagerFactory used above        
        //System.setProperty("IS_TEST", TEST_DB);
        //We are using the database on the virtual Vagrant image, so username password are the same for all dev-databases

        httpServer = startServer();

        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;

        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    static void closeTestServer() {
        //System.in.read();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();
        p1 = new Person("Matias", "Koefoed", "60175242");
        p2 = new Person("Jon", "Bertelsen", "54832910");
        p3 = new Person("Arne", "Wonnegut", "23519965");

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/person").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    void testEndpoint() {
        given()
                .contentType("application/json")
                .get("/person/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Connection established"));
    }

    @Test
    void testCount() {
        given()
                .contentType("application/json")
                .get("/person/count").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(3));
    }

    @Test
    void TestGetAllSize() {
        given()
                .contentType("application/json")
                .get("/person/all").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("all", hasSize(3));
    }

    @Test
    void TestGetAllContent() {
        List<PersonDTO> personDTOS = given()
                .contentType("application/json")
                .get("/person/all").then()
                .extract()
                .body().jsonPath().getList("all", PersonDTO.class);

        PersonDTO pDTO1 = new PersonDTO(p1);
        PersonDTO pDTO2 = new PersonDTO(p2);
        PersonDTO pDTO3 = new PersonDTO(p3);

        //This method requires the object to have an overwritten equals method to compare properly. See PersonDTO.equals()
        assertThat(personDTOS, containsInAnyOrder(pDTO1, pDTO2, pDTO3));
    }

    @Test
    void TestGetPerson() {
        given()
                .contentType("application/json")
                .get("/person/id/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("fName", equalTo("Matias"));
    }

    @Test
    void TestGetPersonException() {
        given()
                .contentType("application/json")
                .get("/person/id/" + p1.getId() + p2.getId() + p3.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Person with the provided ID was not found"));
    }

    @Test
    void TestAddPerson() {
        given()
                .contentType("application/json")
                .body(new PersonDTO("Bart", "Simpson", "66669999"))
                .when()
                .post("/person").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("id", notNullValue())
                .body("fName", equalTo("Bart"))
                .body("lName", equalTo("Simpson"))
                .body("phone", equalTo("66669999"));
    }

    @Test
    void TestEditPerson() {
        given()
                .contentType("application/json")
                .body(new PersonDTO("Matias Bue", "Koefoed", "56957997"))
                .when()
                .put("/person/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                //.body("id", equalTo(p1.getId().toString()))
                .body("fName", equalTo("Matias Bue"))
                .body("lName", equalTo("Koefoed"))
                .body("phone", equalTo("56957997"));
    }

    @Test
    void TestEditPersonException() {
        given()
                .contentType("application/json")
                .body(new PersonDTO())
                .when()
                .put("/person/" + p1.getId() + p2.getId() + p3.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Could not update, person with the provided ID was not found"));
    }

    @Test
    void TestDeletePerson() {
        List<PersonDTO> personDTOS = given()
                .contentType("application/json")
                .get("/person/all").then()
                .extract()
                .body().jsonPath().getList("all", PersonDTO.class);

        PersonDTO pDTO1 = new PersonDTO(p1);
        PersonDTO pDTO2 = new PersonDTO(p2);
        PersonDTO pDTO3 = new PersonDTO(p3);
        assertThat(personDTOS, containsInAnyOrder(pDTO1, pDTO2, pDTO3));

        given()
                .contentType("application/json")
                .delete("/person/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                //.body("id", equalTo(p1.getId().toString()))
                .body("fName", equalTo("Matias"))
                .body("lName", equalTo("Koefoed"))
                .body("phone", equalTo("60175242"));

        personDTOS = given()
                .contentType("application/json")
                .get("/person/all").then()
                .extract()
                .body().jsonPath().getList("all", PersonDTO.class);

        assertThat(personDTOS, containsInAnyOrder(pDTO2, pDTO3));
        assertThat(personDTOS, not(contains(pDTO1)));
    }

    @Test
    void TestDeletePersonException() {
        given()
                .contentType("application/json")
                .delete("/person/" + p1.getId() + p2.getId() + p3.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Could not delete, person with the provided id was not found"));
    }

    @Test
    void TestMethodNotFoundException() {
        given()
                .contentType("application/json")
                .get("/person/EXCEPTION").then()
                .assertThat()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED_405.getStatusCode())
                .body("message", equalTo("Method Not Allowed"));
    }

    @Test
    void TestNotFoundException() {
        given()
                .contentType("application/json")
                .get("/EXCEPTION").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Not Found"));
    }
}
