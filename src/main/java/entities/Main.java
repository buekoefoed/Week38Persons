package entities;

import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.DROP_AND_CREATE);
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(new Person("Matias","Koefoed","34352511"));
            em.persist(new Person("Jon","Bertelsen","23567173"));
            em.persist(new Person("Hans","Andersen","66675301"));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
