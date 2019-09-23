package facades;

import entities.Person;
import exceptions.PersonNotFoundException;

import java.util.List;

public interface IPersonFacade {

    Person addPerson(String fName, String lName, String phone, String street, String zip, String city);

    Person deletePerson(long id) throws PersonNotFoundException;

    Person getPerson(long id) throws PersonNotFoundException;

    List getAllPersons();

    Person editPerson(Person p) throws PersonNotFoundException;
}

