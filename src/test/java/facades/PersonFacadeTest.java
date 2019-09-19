package facades;

import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import entities.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person p1, p2, p3;

    public PersonFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/persons_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = PersonFacade.getPersonFacade(emf);
    }

    /*   **** HINT **** 
        A better way to handle configuration values, compared to the UNUSED example above, is to store those values
        ONE COMMON place accessible from anywhere.
        The file config.properties and the corresponding helper class utils.Settings is added just to do that. 
        See below for how to use these files. This is our RECOMENDED strategy
    */
    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
        // Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    @BeforeEach
    public void setUp() {
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

    @AfterEach
    public void tearDown() {
        // Remove any data after each test was run
    }

    @Test
    void testGetAll() {
        List<Person> personList = facade.getAllPersons();
        assertEquals(3, personList.size(),
                "Assert that you get all persons");
    }

    @Test
    void testGetPerson() throws PersonNotFoundException {
        Person person = facade.getPerson(p1.getId());
        assertEquals("Koefoed", person.getLastName(),
                "Assert that you get the correct person");
    }

    @Test
    void testGetPersonException() {
        assertThrows(PersonNotFoundException.class, () ->
                        facade.getPerson(p1.getId() + p2.getId() + p3.getId()),
                "Assert that correct exception is thrown if person ID is not found");
    }

    @Test
    void testAddPerson() {
        Person person = facade.addPerson("Jon", "Snow", "80081135");
        assertNotNull(person.getId(),
                "If ID is NotNull then DB must have assigned an ID");
    }

    @Test
    void testDeletePerson() throws PersonNotFoundException {
        long i = p1.getId();
        Person person = facade.deletePerson(i);
        assertEquals("Matias", person.getFirstName(),
                "Assert that deleted person name is Matias");
        assertEquals(facade.getAllPersons().size(), 2,
                "Assert that there's only 2 persons left in db");
    }

    @Test
    void testDeletePersonException() {
        assertThrows(PersonNotFoundException.class, () ->
                        facade.deletePerson(p1.getId() + p2.getId() + p3.getId()),
                "Assert that correct exception is thrown if person ID is not found");
    }

    @Test
    void testEditPerson() throws PersonNotFoundException {
        p1.setFirstName("Matias Bue");
        facade.editPerson(p1);
        assertEquals("Matias Bue", p1.getFirstName(),
                "Assert that person is now named Matias Bue");
        assertNotEquals("Matias", p1.getFirstName(),
                "Assert that person is NOT named Matias");
    }

    @Test
    void testEditPersonException() {
        Person person = new Person();
        person.setId(p1.getId() + p2.getId() + p3.getId());
        assertThrows(PersonNotFoundException.class, () ->
                        facade.editPerson(person),
                "Assert that correct exception is thrown if person ID is not found");
    }
}
