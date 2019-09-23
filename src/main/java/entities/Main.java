package entities;

import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.DROP_AND_CREATE);
        EntityManager em = emf.createEntityManager();
        Person p1, p2, p3;

        p1 = new Person("Matias", "Koefoed", "34352511");
        p2 = new Person("Jon", "Bertelsen", "23567173");
        p3 = new Person("Hans", "Andersen", "66675301");

        p1.addAddress(new Address("Malmøvej 17", "3700", "Rønne"));
        p2.addAddress(new Address("Ndr Frihavnsgade 28", "2100", "København"));
        p3.addAddress(new Address("Vigerslev Allé 68", "2500", "København"));

        try {
            em.getTransaction().begin();
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
