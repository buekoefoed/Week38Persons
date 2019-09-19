package dtomappers;

import entities.Person;

import java.util.Objects;

public class PersonDTO {

    private long id;
    private String fName;
    private String lName;
    private String phone;

    public PersonDTO() {}

    public PersonDTO(Person p) {
        this.fName = p.getFirstName();
        this.lName = p.getLastName();
        this.phone = p.getPhone();
        this.id = p.getId();
    }

    public PersonDTO(String fn,String ln, String phone) {
        this.fName = fn;
        this.lName = ln;
        this.phone = phone;
    }

    public long getId() {
        return id;
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
