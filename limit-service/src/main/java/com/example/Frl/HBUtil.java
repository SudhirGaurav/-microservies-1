package com.example.Frl;

/*
 * Helper methods for Hibernate classes. These attempt to simplify the
 * interaction with Hibernate's sessions and transactions.
 *
 * <p>
 * If you're running inside an app server, sessions returned by
 * HBUtil.getSession() will be attached to the current container
 * transaction, if any, and will commit or roll back along with the
 * rest of that transaction.
 *
 * <p>
 * If you're running standalone, e.g. in a test suite, you'll
 * have to manage
 */

import com.rbauction.AppEnv;
import com.rbauction.log.LogUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.web.context.ContextLoader;

import javax.management.ObjectName;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class HBUtil {
    private static final Logger logger = LoggerFactory.getLogger(HBUtil.class);

    /**
     * This property, if set non-null in the <tt>ironplanet.properties</tt> file,
     * enables debugging instrumentation in {@link #getSession() getSession()}
     * and {@link #closeSession(boolean) closeSession()}, including caller-mismatch
     * tracking.
     */
    public static final String HB_SESSION_DEBUG = "ironplanet.hb.session.debug";

    private static SessionFactory sessionFactory;
    private static Configuration cfg;

    public static final ThreadLocal<Session> session = new ThreadLocal<Session>();
    public static final ThreadLocal<Transaction> transaction = new ThreadLocal<Transaction>();
    public static final ThreadLocal<Integer> refCount = new ThreadLocal<Integer>();

    /**
     * Initializes the Hibernate <tt>SessionFactory</tt> instance using the
     * Hibernate configuration described by <tt>cfg</tt>.
     * <p>
     * Provided for use by JUnit tests and other autonomous clients as an
     * alternative to defining the <tt>SessionFactory</tt> as a Spring bean.
     */
    public static void setConfiguration(Configuration cfg) {
        try {
            sessionFactory = cfg.buildSessionFactory();
            HBUtil.cfg = cfg;
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Returns the method name and source file/line number of the code
     * that called this class.
     */
    public static String getCaller() {
        return LogUtils.getCallerOfClass(HBUtil.class.getName());
    }

    /**
     * Stack of caller information for {@link #getSession() getSession()}
     * and {@link # closeSession() closeSession()} debugging.
     */
    static ThreadLocal<Vector<String>> tlStack = new ThreadLocal<Vector<String>>();

    /**
     * returns a string containing stack trace of current thread, separated by newline,
     * indented with 4 whitespaces
     *
     * @param num the number of stacks to be returned, 0 means entire stack trace
     * @return
     */
    private static String getStackTrace(int num) {
        StringBuffer st = new StringBuffer();
        StackTraceElement se[] = Thread.currentThread().getStackTrace();
        int l = se.length;
        if (num > 0 && l > num + 3)
            l = num + 3;
        for (int i = 3; i < l; i++) {
            st.append("\n    ").append(se[i].toString());
        }
        return st.toString();
    }

    /**
     * Returns a Hibernate session with an open transaction. If there is
     * already a session in use by the current thread, returns that session.
     */
    public static Session getSession() {
        /*
         * Optional Hibernate Session debugging, controlled by the HB_SESSION_DEBUG
         * Property in ironplanet.properties. We push the caller info onto a stack
         * to be compared in closeSession().
         */
        String caller = null;
        /*if (null != PropertyManager.getProperty(HB_SESSION_DEBUG)) {
            caller = getCaller();
            synchronized (tlStack) {
                Vector<String> v = (Vector<String>) tlStack.get();
                if (null == v) {
                    v = new Vector<String>();
                    tlStack.set(v);
                }
                v.addElement(caller);
            }
        }*/

        Session s = null;
        boolean newTx = true;

        initHibernate();

        // If we're in a container transaction, use it.
        try {
            s = sessionFactory.getCurrentSession();
        } catch (Exception e) {
        }

        if (s == null) {
            s = session.get();
        }
        if (s == null) {
            s = sessionFactory.openSession();
            session.set(s);
            refCount.set(0);
            transaction.set(s.beginTransaction());
            // if (null != PropertyManager.getProperty(HB_SESSION_DEBUG))
            // logger.debug("Begin a new transaction: " + transaction.get().toString());
        } else {
            newTx = false;
        }

        Integer count = refCount.get();
        if (count == null)
            count = 0;
        refCount.set(count + 1);
        return s;
    }

    public static void initHibernate() {
        synchronized (session) {
            if (HBUtil.cfg == null) {
                Configuration configuration = new Configuration();
                // load properties from source code
                configuration.configure("hibernate-ironplanet.cfg.xml");
                // overwirte DB connections properties from environment variables if exist
                if (AppEnv.getInstance() != null) {
                    configuration.setProperty("hibernate.connection.url", AppEnv.getInstance().getDatasourceUrl());
                    configuration.setProperty("hibernate.connection.username", AppEnv.getInstance().getDatasourceUsername());
                    configuration.setProperty("hibernate.connection.password", AppEnv.getInstance().getDatasourcePassword());
                    String hibernateSlowQueryThresholdInMillis = AppEnv.getInstance().getHibernateSlowQueryThresholdInMillis();
                    if (hibernateSlowQueryThresholdInMillis != null) {
                        logger.info("Setting slow query threshold in millis to " + hibernateSlowQueryThresholdInMillis);
                        configuration.setProperty("hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS", hibernateSlowQueryThresholdInMillis);
                    }
                }
                setConfiguration(configuration);

                try {
                    ApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
                    if (applicationContext != null) {
                        ObjectName on = new ObjectName("Hibernate:type=statistics,application=sample");
                        MBeanExporter mbeanExporter = applicationContext.getAutowireCapableBeanFactory().createBean(MBeanExporter.class);
                        mbeanExporter.registerManagedResource(sessionFactory.getStatistics(), on);
                    }
                } catch (Exception e) {
                    logger.error("Error registering Hibernate StatisticsService: " + e.toString(), e);
                }
            }
        }
    }

    /**
     * @param commit
     * @throws IllegalStateException if the <tt>HB_SESSION_DEBUG</tt> Property is defined in ironplanet.properties
     *                               and the Class which called this method is not also the most recent caller of
     *                               {@link #getSession() getSession()}.
     */
    private static void closeSession(boolean commit) {

        /*if (null != PropertyManager.getProperty(HB_SESSION_DEBUG)) {
            synchronized (tlStack) {
                Vector v = (Vector) tlStack.get();
                if (v.size() < 1)
                    throw new IllegalStateException("Too many closeSession() calls");

                *//*
         * Compare unqualified Class name from caller info vs. last caller
         * of getSession().
         *//*
                String last = (String) v.remove(v.size() - 1);
                String lastClass = last.substring(last.indexOf("(") + 1, last.indexOf("."));
                String caller = getCaller();
                String callerClass = caller.substring(caller.indexOf("(") + 1, caller.indexOf("."));
                if (!lastClass.equals(callerClass))
                    throw new IllegalStateException("Mismatched caller " + caller +
                            " (expected " + last + ")");
            }
        }*/

        // If the user wants to roll back, do that regardless of
        // whether or not we're in a nested transaction. (In a
        // JTA environment this will mark the overall transaction
        // as rolled back.)
        Transaction tx = (Transaction) transaction.get();
        if (tx != null && !commit) {
            tx.rollback();
            transaction.set(null);
        }

        Integer count = (Integer) refCount.get();
        /*if (null != PropertyManager.getProperty(HB_SESSION_DEBUG)) {
            // logger.debug("caller=" + getCaller() + ", refCount: " + count + " -> " + (count.intValue() - 1));
        }*/
        if (count == null)
            return;
        refCount.set(new Integer(count.intValue() - 1));
        if (count.intValue() > 1)
            return;

        // If we're the outermost instance of the session and
        // this is a "commit", commit the transaction.
        HibernateException commitFailure = null;
        if (tx != null && commit) {
            try {
                tx.commit();
                /*if (null != PropertyManager.getProperty(HB_SESSION_DEBUG)) {
                    // logger.debug("Successfully Committed: " + "caller=" + getCaller());
                }*/
            } catch (Exception e) {
                logger.error("Persistence error", e.getCause());
                // logger.error("Hibernate transaction commit failed - release this session.", e);
                try {
                    Session s = (Session) session.get();
                    if (s != null)
                        s.close();
                    session.set(null);
                } catch (Exception ee) {
                    // logger.info(ee, "Failed to close Hibernate session.");
                }
                throw new HibernateException(e.getMessage());

            }
            transaction.set(null);
        }/* else if (null != PropertyManager.getProperty(HB_SESSION_DEBUG)) {
            // logger.debug("Let Container commit the tx:" + HBUtil.getStackTrace(10));
        }*/

        Session s = (Session) session.get();
        if (s != null)
            s.close();
        session.set(null);

        if (commitFailure != null)
            throw commitFailure;
    }

    /**
     * Commits the current transaction and releases the session.
     */
    public static void commit() {
        closeSession(true);
    }

    /**
     * Rolls back the current transaction (if we aren't inside a nested
     * transaction) and releases the session.
     */
    public static void rollback() {
        closeSession(false);
    }

    /**
     * Checks to see if there's a session attached to the current
     * thread. This is called in the JSP epilogue to detect session
     * leaks (which, outside of container-managed transactions, also
     * mean the possibility of uncommitted changes.)
     */
    public static boolean cleanupSession() {
        Session s = (Session) session.get();
        if (s == null)
            return false;
        // logger.warn("Session leak! Rolling back transaction.");
        refCount.set(new Integer(1));
        rollback();
        return true;
    }

    /**
     * Returns the results of a query as a List.
     */
    public static List getList(String q) {
        return getList(q, new Object[]{});
    }

    /**
     * Returns the results of a query without params as a List.
     */
    public static <T> List<T> getList(String q, Class<T> clazz) {
        return getList(q, new Object[]{}, clazz);
    }

    /**
     * Returns the results of a parameterized query as a List.
     */
    public static List getList(String q, Object[] params) {
        try {
            Query query = setupParameterizedQuery(q, params);
            return query.list();
        } finally {
            commit();
        }
    }

    /**
     * Returns the results of a parameterized query as a List.
     *
     * @param q
     * @param params
     * @param maxResults max number of results to retrieve; 0 or less means no max
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List getList(String q, Object[] params, int maxResults) {
        try {
            Query query = setupParameterizedQuery(q, params);

            if (maxResults > 0)
                query.setMaxResults(maxResults);

            return query.list();
        } finally {
            commit();
        }
    }

    /**
     * Returns the results of a parameterized query as a List.
     *
     * @param <T>
     * @param nativeSqlQuery
     * @param params
     * @param maxResults     max number of results to retrieve; 0 or less means no max
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> List executeNativeSql(Class<T> clazz, String classAbbr, String nativeSqlQuery, Object[] params, int maxResults) {
        try {
            Query query = setupNativeParameterizedQuery(clazz, classAbbr, nativeSqlQuery, params);

            if (maxResults > 0)
                query.setMaxResults(maxResults);

            return query.list();
        } finally {
            commit();
        }
    }

    private static Query setupParameterizedQuery(String q, Object[] params) {
        Query query = getSession().createQuery(q);
        for (int i = 0; i < params.length; i += 2) {
            String name = (String) params[i];
            if (params[i + 1] instanceof Collection) {
                query.setParameterList(name,
                        (Collection) params[i + 1]);
            } else {
                query.setParameter(name,
                        params[i + 1]);
            }
        }
        return query;
    }

    public static <T> List<T> getList(String q, Object[] params, Class<T> objectType) {
        return getList(q, params, objectType, 0);
    }

    public static <T> List<T> getList(String q, Object[] params, Class<T> objectType, int maxResult) {
        try {
            TypedQuery<T> query = setupParameterizedQuery(q, params, objectType);
            if (maxResult > 0) {
                query.setMaxResults(maxResult);
            }
            return query.getResultList();
        } finally {
            commit();
        }
    }

    public static <T> List<T> getList(String q, Object[] params, Class<T> objectType, int index, int maxResult) {
        try {
            TypedQuery<T> query = setupParameterizedQuery(q, params, objectType);
            if (index >= 0) {
                query.setFirstResult(index);
            }
            if (maxResult > 0) {
                query.setMaxResults(maxResult);
            }
            return query.getResultList();
        } finally {
            commit();
        }
    }

    private static <T> TypedQuery<T> setupParameterizedQuery(String q, Object[] params, Class<T> objectType) {
        TypedQuery<T> query = getSession().createQuery(q, objectType);
        for (int i = 0; i < params.length; i += 2) {
            String name = (String) params[i];
            if (params[i + 1] instanceof Collection) {
                query.setParameter(name, params[i + 1]);
            } else {
                query.setParameter(name, params[i + 1]);
            }
        }
        return query;
    }


    private static <T> Query setupNativeParameterizedQuery(Class<T> clazz, String classAbbr, String q, Object[] params) {
        Query query = getSession().createSQLQuery(q).addEntity(classAbbr, clazz);
        for (int i = 0; i < params.length; i += 2) {
            String name = (String) params[i];
            if (params[i + 1] instanceof Collection) {
                query.setParameterList(name,
                        (Collection) params[i + 1]);
            } else {
                query.setParameter(name,
                        params[i + 1]);
            }
        }
        return query;
    }

    public static <T> List<T> executeNativeTypedSql(Class<T> clazz, String nativeSqlQuery, Object[] params, int maxResults) {
        try {
            Query<T> query = setupNativeParameterizedTypedQuery(clazz, nativeSqlQuery, params);

            if (maxResults > 0) {
                query.setMaxResults(maxResults);
            }

            return query.list();
        } finally {
            commit();
        }
    }

    private static <T> Query<T> setupNativeParameterizedTypedQuery(Class<T> clazz, String q, Object[] params) {
        Query<T> query = getSession().createNativeQuery(q, clazz);
        for (int i = 0; i < params.length; i += 2) {
            String name = (String) params[i];
            query.setParameter(name, params[i + 1]);
        }
        return query;
    }

    public static List<Object[]> executeNativeSql(List<Class<?>> clazzes, List<String> classAbbrs, String nativeSqlQuery, Object[] params, int maxResults) {
        try {
            Query<Object[]> query = setupNativeParameterizedQueryForMultipleEntityClasses(clazzes, classAbbrs, nativeSqlQuery, params);

            if (maxResults > 0) {
                query.setMaxResults(maxResults);
            }

            return query.list();
        } finally {
            commit();
        }
    }

    private static Query<Object[]> setupNativeParameterizedQueryForMultipleEntityClasses(List<Class<?>> clazzes, List<String> classAbbrs, String q, Object[] params) {
        NativeQuery<Object[]> query = getSession().createNativeQuery(q, Object[].class);
        for (int i = 0; i < clazzes.size(); i++) {
            query.addEntity(classAbbrs.get(i), clazzes.get(i));
        }
        for (int i = 0; i < params.length; i += 2) {
            String name = (String) params[i];
            if (params[i + 1] instanceof Collection) {
                query.setParameterList(name, (Collection) params[i + 1]);
            } else {
                query.setParameter(name, params[i + 1]);
            }
        }
        return query;
    }

    /**
     * Returns the first result from a parameterized query.
     */
    public static Object getUniqueResult(String q, Object[] params) {
        try {
            Query query = setupParameterizedQuery(q, params);
            query.setMaxResults(1);
            return query.uniqueResult();
        } finally {
            commit();
        }
    }

    public static <T> T getUniqueResult(String q, Object[] params, Class<T> entityType) {
        try {
            TypedQuery<T> query = setupParameterizedQuery(q, params, entityType);
            query.setMaxResults(1);
            return query.getSingleResult();
        } finally {
            commit();
        }
    }

    /**
     * Saves an object to the database.
     */
    public static void save(Object o) {
        try {
            getSession().saveOrUpdate(o);
            commit();
        } catch (HibernateException e) {
            // logger.error("Can't save or update : " + e.getMessage(), e);
            rollback();
            throw e;
        }
    }

    public static Object merge(Object o) {
        try {
            Object mergedObject = getSession().merge(o);
            commit();
            return mergedObject;
        } catch (HibernateException e) {
            // logger.error("Can't merge object : " + e.getMessage(), e);
            rollback();
            throw e;
        }
    }

    /**
     * Saves an object to the database.
     */
    public static void saveOrUpdateBatch(Collection<?> objects) {
        try {

            int count = 0;
            Session currentSession = getSession();
            for (Object obj : objects) {
                currentSession.saveOrUpdate(obj);
                count++;
                if (count % 50 == 0) {
                    //flush(send) these 50 objects to database as a batch
                    currentSession.flush();
                    //clear the session after flush these 50 objects to database
                    currentSession.clear();
                }
            }
            commit();
        } catch (HibernateException e) {
            // logger.error("Can't save or update the batch: " + e.getMessage(), e);
            rollback();
            throw e;
        }
    }

    public static void deleteBatch(Collection<?> objects) {
        try {

            int count = 0;
            Session currentSession = getSession();
            for (Object obj : objects) {
                currentSession.delete(obj);
                count++;
                if (count % 50 == 0) {
                    //flush(send) these 50 objects to database as a batch
                    currentSession.flush();
                    //clear the session after flush these 50 objects to database
                    currentSession.clear();
                }
            }
            commit();
        } catch (HibernateException e) {
            logger.error("Can't save or update the batch: " + e.getMessage(), e);
            rollback();
            throw e;
        }
    }

    /**
     * Deletes an object from the database.
     */
    public static void delete(Object o) {
        try {
            getSession().delete(o);
            commit();
        } catch (HibernateException e) {
            rollback();
            throw e;
        }
    }

    /**
     * Executes the specified <tt>update</tt> or <tt>delete</tt> query, and returns
     * the number of rows affected.
     *
     * @param q The <tt>update</tt> or <tt>delete</tt> query to be executed.
     * @return The number of database rows affected by the <tt>update</tt> or <tt>delete</tt>.
     */
    public static int executeUpdate(String q) {
        return executeUpdate(q, new Object[]{});
    }

    /**
     * Prepares the specified <tt>update</tt> or <tt>delete</tt> query by substituting
     * the specified <tt>params</tt>, and returns the number of rows affected.
     * <p><i>N.B.:</i> Support for bulk update/delete queries is provided only by
     * Hibernate's newer, so-called AST parser; the Hibernate Classic parser (v2.x)
     * does <i>not</i> allow such queries.</p>
     * <p>To enable the AST parser, the following property must be set:
     * <pre>
     * query.factory_class=org.hibernate.hql.ast.ASTQueryTranslatorFactory
     * </pre></p>
     *
     * @param q      The <tt>update</tt> or <tt>delete</tt> query to be executed.
     * @param params The array of substitutable parameters.
     * @return The number of database rows affected by the <tt>update</tt> or <tt>delete</tt>.
     */
    public static int executeUpdate(String q, Object[] params) {
        int count = 0;
        try {
            Query query = setupParameterizedQuery(q, params);
            count = query.executeUpdate();
            commit();
        } catch (HibernateException e) {
            // logger.error("Can't execute query \"" + q + "\": " + e.getMessage(), e);
            rollback();
            throw e;
        }
        return count;
    }

    /**
     * Loads an object given its numeric ID.
     */
    public static Object get(Class clazz, long id) {
        return get(clazz, new Long(id));
    }

    /**
     * Loads an object given its numeric ID.
     */
    public static Object get(Class clazz, int id) {
        return get(clazz, new Integer(id));
    }

    /**
     * Loads an object given its ID.
     */
    public static Object get(Class clazz, Serializable id) {
        try {
            Object o = getSession().get(clazz, id);
            commit();
            return o;
        } catch (HibernateException e) {
            rollback();
            throw e;
        }
    }

    public static <T> T find(Class<T> entityType, Object id) {
        try {
            T o = getSession().find(entityType, id);
            commit();
            return o;
        } catch (HibernateException e) {
            rollback();
            throw e;
        }
    }

    public static List getList(String sql, String parameter, Collection<?> values) {

        try {
            Query query = getSession().createQuery(sql);
            query.setParameterList(parameter, values);
            return query.list();
        } finally {
            commit();
        }
    }

    public static List executeNativeSql(String nativeSqlQuery, Object[] params) {
        return executeNativeSql(nativeSqlQuery, params, 0);
    }

    public static List executeNativeSql(String nativeSqlQuery, Object[] params, int maxResults) {
        try {
            Query query = setupNativeParameterizedQuery(nativeSqlQuery, params);

            if (maxResults > 0) {
                query.setMaxResults(maxResults);
            }

            return query.list();
        } finally {
            commit();
        }
    }

    private static Query setupNativeParameterizedQuery(String q, Object[] params) {
        Query query = getSession().createNativeQuery(q);
        for (int i = 0; i < params.length; i += 2) {
            String name = (String) params[i];
            if (params[i + 1] instanceof Collection) {
                query.setParameterList(name, (Collection) params[i + 1]);
            } else {
                query.setParameter(name, params[i + 1]);
            }
        }
        return query;
    }
}
