package dtomappers;

import entities.Person;

import java.util.Objects;

public class PersonDTO {

    private long id;
    private String fName;
    private String lName;
    private String phone;
    private String street;
    private String zip;
    private String city;

    public PersonDTO() {}

    public PersonDTO(Person person) {
        if (person.getId() == null){
            this.id = 0;
        }else {
            this.id = person.getId();
        }
        this.fName = person.getFirstName();
        this.lName = person.getLastName();
        this.phone = person.getPhone();
        this.street = person.getAddress().getStreet();
        this.zip = person.getAddress().getZip();
        this.city = person.getAddress().getCity();
    }

    public PersonDTO(String fn, String ln, String phone) {
        this.fName = fn;
        this.lName = ln;
        this.phone = phone;
    }

    public PersonDTO(String fName, String lName, String phone, String street, String zip, String city) {
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.street = street;
        this.zip = zip;
        this.city = city;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDTO)) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return id == personDTO.id &&
                Objects.equals(fName, personDTO.fName) &&
                Objects.equals(lName, personDTO.lName) &&
                Objects.equals(phone, personDTO.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fName, lName, phone);
    }
}
