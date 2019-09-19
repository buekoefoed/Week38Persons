package facades;

import entities.Person;
import exceptions.PersonNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public long getPersonCount() {
        EntityManager em = getEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(r) FROM Person r").getSingleResult();
        } finally {
            em.close();
        }

    }

    @Override
    public Person addPerson(String fName, String lName, String phone) {
        EntityManager em = getEntityManager();
        Person person = new Person(fName, lName, phone);
        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return person;
    }

    @Override
    public Person deletePerson(long id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null) {
            throw new PersonNotFoundException("Could not delete, person with the provided id was not found");
        }
        try {
            em.getTransaction().begin();
            em.remove(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return person;
    }

    @Override
    public Person getPerson(long id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null) {
            throw new PersonNotFoundException("Person with the provided ID was not found");
        }
        return person;
    }

    @Override
    public List<Person> getAllPersons() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Person.getAllRows", Person.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Person editPerson(Person p) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, p.getId());
        if (person == null) {
            throw new PersonNotFoundException("Could not update, person with the provided ID was not found");
        }
        person.setFirstName(p.getFirstName());
        person.setLastName(p.getLastName());
        person.setPhone(p.getPhone());
        person.setLastEdited();
        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return person;
    }
}
