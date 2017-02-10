package fund.mymutual.cfsws.model;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaUtil {
    private static EntityManagerFactory entityManagerFactory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory("fund.mymutual.cfsws.model");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entityManagerFactory;
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    // The following taken from https://github.com/hibernate/hibernate-orm/blob/master/hibernate-testing/src/main/java/org/hibernate/testing/transaction/TransactionUtil.java
    // Hibernate, Relational Persistence for Idiomatic Java
    // License: GNU Lesser General Public License (LGPL), version 2.1 or later.

    /**
     * JPA transaction function
     *
     * @param <T> function result
     */
    @FunctionalInterface
    public interface JPATransactionFunction<T>
            extends Function<EntityManager, T> {
        /**
         * Before transaction completion function
         */
        default void beforeTransactionCompletion() {

        }

        /**
         * After transaction completion function
         */
        default void afterTransactionCompletion() {

        }
    }

    /**
     * JPA transaction function without return value
     */
    @FunctionalInterface
    public interface JPATransactionVoidFunction
            extends Consumer<EntityManager> {
        /**
         * Before transaction completion function
         */
        default void beforeTransactionCompletion() {

        }

        /**
         * After transaction completion function
         */
        default void afterTransactionCompletion() {

        }
    }

    /**
     * Execute function in a JPA transaction
     *
     * @param function function
     * @param <T> result type
     *
     * @return result
     */
    public static <T> T transaction(JPATransactionFunction<T> function) {
        T result = null;
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = createEntityManager();
            function.beforeTransactionCompletion();
            txn = entityManager.getTransaction();
            txn.begin();
            result = function.apply( entityManager );
            if (txn.isActive()) txn.commit();
        }
        catch ( Throwable e ) {
            if ( txn != null && txn.isActive() ) {
                txn.rollback();
            }
            throw e;
        }
        finally {
            function.afterTransactionCompletion();
            if ( entityManager != null ) {
                entityManager.close();
            }
        }
        return result;
    }

    /**
     * Execute function in a JPA transaction without return value
     *
     * @param function function
     */
    public static void transaction(JPATransactionVoidFunction function) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = createEntityManager();
            function.beforeTransactionCompletion();
            txn = entityManager.getTransaction();
            txn.begin();
            function.accept( entityManager );
            if (txn.isActive()) txn.commit();
        }
        catch ( Throwable e ) {
            if ( txn != null && txn.isActive() ) {
                txn.rollback();
            }
            throw e;
        }
        finally {
            function.afterTransactionCompletion();
            if ( entityManager != null ) {
                entityManager.close();
            }
        }
    }

}
