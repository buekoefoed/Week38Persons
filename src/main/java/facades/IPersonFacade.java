package facades;

import entities.Person;

import java.util.List;

public interface IPersonFacade {

    Person addPerson(String fName, String lName, String phone);

    Person deletePerson(int id);

    Person getPerson(int id);

    List<Person> getAllPersons();

    Person editPerson(Person p);
}

