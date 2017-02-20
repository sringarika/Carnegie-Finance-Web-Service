package fund.mymutual.cfsws.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaUtil {
    private static EntityManagerFactory entityManagerFactory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            try {
                Map<String, Object> config = new HashMap<String, Object>();
                if (System.getenv("RDS_DB_NAME") != null) {
                    String dbName = System.getenv("RDS_DB_NAME");
                    String userName = System.getenv("RDS_USERNAME");
                    String password = System.getenv("RDS_PASSWORD");
                    String hostname = System.getenv("RDS_HOSTNAME");
                    String port = System.getenv("RDS_PORT");
                    String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName +
                            "?useUnicode=true&characterEncoding=UTF-8" +
                            "&user=" + userName + "&password=" + password;
                    config.put("javax.persistence.jdbc.url", jdbcUrl);
                    config.put("hibernate.hbm2ddl.auto", "validate");
                }
                config.put("hibernate.show_sql", "false");
                config.put("hibernate.format_sql", "false");
                entityManagerFactory = Persistence.createEntityManagerFactory("fund.mymutual.cfsws.model", config);
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
    public interface JPATransactionFunction<T, E extends Throwable> {

        T apply(EntityManager em) throws E;

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
    public interface JPATransactionVoidFunction<E extends Throwable> {

        void accept(EntityManager em) throws E;

        /**
         * Before transaction completion function
         */
        default void beforeTransactionCompletion() throws E {

        }

        /**
         * After transaction completion function
         */
        default void afterTransactionCompletion() throws E {

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
    public static <T, E extends Throwable> T transaction(JPATransactionFunction<T, E> function) throws E {
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
    public static <E extends Throwable> void transaction(JPATransactionVoidFunction<E> function) throws E {
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
