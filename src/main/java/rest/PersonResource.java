package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtomappers.PersonDTO;
import dtomappers.PersonsDTO;
import entities.Person;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.PersonFacade;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/persons",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String serverIsUp() {
        return "{\"msg\":\"Connection established\"}";
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        long count = FACADE.getPersonCount();
        return "{\"count\":" + count + "}";
    }

    @Path("all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAll() {
        List<Person> personList = FACADE.getAllPersons();
        return GSON.toJson(new PersonsDTO(personList));
    }

    @Path("id/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPerson(@PathParam("id") long id) throws PersonNotFoundException {
        Person person = FACADE.getPerson(id);
        return GSON.toJson(new PersonDTO(person));
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addPerson(String p) {
        PersonDTO pDTO = GSON.fromJson(p, PersonDTO.class);
        Person person = FACADE.addPerson(
                pDTO.getfName(), pDTO.getlName(), pDTO.getPhone(),
                pDTO.getStreet(), pDTO.getZip(), pDTO.getCity());
        return GSON.toJson(new PersonDTO(person));
    }

    @Path("{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String editPerson(@PathParam("id") long id, String p) throws PersonNotFoundException {
        PersonDTO pDTO = GSON.fromJson(p, PersonDTO.class);
        Person person = new Person(
                pDTO.getfName(), pDTO.getlName(), pDTO.getPhone(),
                pDTO.getStreet(), pDTO.getZip(), pDTO.getCity());
        person.setId(id);
        return GSON.toJson(new PersonDTO(FACADE.editPerson(person)));
    }

    @Path("{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePerson(@PathParam("id") long id) throws PersonNotFoundException {
        return GSON.toJson(new PersonDTO(FACADE.deletePerson(id)));
    }
}
