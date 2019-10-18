/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.persistence;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.time.DateUtils;
import org.eclipse.jubula.client.core.businessprocess.progress.OperationCanceledUtil;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PM to handle all test result summaries related Persistence (JPA / EclipseLink) queries
 * 
 * @author BREDEX GmbH
 * @created Mar 3, 2010
 */
public class TestResultSummaryPM {

    /** standard logging */
    private static Logger log = 
        LoggerFactory.getLogger(TestResultSummaryPM.class);
    
    /** name of Test Result Summary's (internal) "GUID" property */
    private static final String PROPNAME_GUID = "internalGuid"; //$NON-NLS-1$

    /** name of Test Result Summary's (internal) "Project GUID" property */
    private static final String PROPNAME_PROJECT_GUID = "internalProjectGuid"; //$NON-NLS-1$

    /** name of Test Result Summary's "Project Major Version" property */
    private static final String PROPNAME_PROJECT_MAJOR_VERSION = "projectMajorVersion"; //$NON-NLS-1$

    /** name of Test Result Summary's "Project Minor Version" property */
    private static final String PROPNAME_PROJECT_MINOR_VERSION = "projectMinorVersion"; //$NON-NLS-1$
    
    /** name of Test Result Summary's "Project Micro Version" property */
    private static final String PROPNAME_PROJECT_MICRO_VERSION = "projectMicroVersion"; //$NON-NLS-1$
    
    /** name of Test Result Summary's "Project VersionQualifier" property */
    private static final String PROPNAME_PROJECT_VERSION_QUALIFIER = "projectVersionQualifier"; //$NON-NLS-1$

    /** name of Test Result Summary's "AUT Agent Name" property */
    private static final String PROPNAME_AUT_AGENT_NAME = "autAgentName"; //$NON-NLS-1$
    
    /** name of Test Result Summary's "Executed Test Steps" property */
    private static final String PROPNAME_EXECUTED_TESTSTEPS = "testsuiteExecutedTeststeps"; //$NON-NLS-1$
    
    /** name of Test Result Summary's "AUT Hostname" property */
    private static final String PROPNAME_AUT_HOSTNAME = "autHostname"; //$NON-NLS-1$
    
    /** name of Test Result Summary's "Test Suite Date" property */
    private static final String PROPNAME_TESTSUITE_DATE = "testsuiteDate"; //$NON-NLS-1$

    /**
     * hide
     */
    private TestResultSummaryPM() {
    // empty
    }

    /**
     * @param proj
     *            the project to search in
     * @param se
     *            the Persistence (JPA / EclipseLink) session to use for query (optional)
     * @return a list of all test result summaries for the given project for all
     *         available project version
     * @throws PMException
     *             in case of any DB error.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final List<ITestResultSummaryPO> getAllTestResultSummaries(
            IProjectPO proj, EntityManager se) throws PMException {
        List<ITestResultSummaryPO> ltrs = null;
        EntityManager s = null;
        try {
            s = se != null ? se : Persistor.instance().openSession();
            
            CriteriaBuilder builder = s.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery();
            Root from = query.from(PoMaker.getTestResultSummaryClass());
            query.select(from).where(
                    builder.equal(
                        from.get(PROPNAME_PROJECT_GUID), 
                        proj.getGuid()));
            
            ltrs = s.createQuery(query).getResultList();
        } catch (PersistenceException e) {
            OperationCanceledUtil.checkForOperationCanceled(e);
            PersistenceManager.handleDBExceptionForAnySession(null, e, s);
        } finally {
            Persistor.instance().dropSession(s);
        }
        return ltrs;
    }
    
    /**
     * @param proj the project to search in
     * @param se the Persistence (JPA / EclipseLink) session to use for query (optional)
     * @param pageNumber the number of pages
     * @param pageSize the size of page
     * 
     * @return a list of all test result summaries for the given project for all
     *         available project version
     * @throws PMException in case of any DB error.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final List<ITestResultSummaryPO> getTestResultSummaries(
            IProjectPO proj, EntityManager se,
            int pageNumber, int pageSize) throws PMException {
        List<ITestResultSummaryPO> ltrs = null;
        EntityManager s = null;
        try {
            s = se != null ? se : Persistor.instance().openSession();
            
            CriteriaBuilder builder = s.getCriteriaBuilder();
            CriteriaQuery cQuery = builder.createQuery();
            Root from = cQuery.from(PoMaker.getTestResultSummaryClass());
            cQuery.select(from).where(
                    builder.equal(
                        from.get(PROPNAME_PROJECT_GUID), 
                        proj.getGuid()));
            
            Query query = s.createQuery(cQuery);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            
            ltrs = query.getResultList();
        } catch (PersistenceException e) {
            OperationCanceledUtil.checkForOperationCanceled(e);
            PersistenceManager.handleDBExceptionForAnySession(null, e, s);
        } finally {
            Persistor.instance().dropSession(s);
        }
        return ltrs;
    }

    /**
     * @param proj currently project
     * @param se entity manager if there is one
     * @return the number of test results
     * @throws PMException
     */
    @SuppressWarnings("unchecked")
    public static final long countOfTestResultSummaries(
            IProjectPO proj, EntityManager se) throws PMException {
        long countOfTestResultSummaries = 0;
        EntityManager s = null;
        try {
            s = se != null ? se : Persistor.instance().openSession();
            
            CriteriaBuilder builder = s.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery();
            Root from = query.from(PoMaker.getTestResultSummaryClass());
            query.select(builder.count(from)).where(
                    builder.equal(
                            from.get(PROPNAME_PROJECT_GUID), 
                            proj.getGuid()));
    
            Number count = (Number)s.createQuery(query)
                    .getSingleResult();
            
            countOfTestResultSummaries = count.longValue(); 
            
        } catch (PersistenceException e) {
            OperationCanceledUtil.checkForOperationCanceled(e);
            PersistenceManager.handleDBExceptionForAnySession(null, e, s);
        } finally {
            Persistor.instance().dropSession(s);
        }
        return countOfTestResultSummaries;
    }
    
    /**
     * store the testresult summary of test run in database
     * @param summary the testresult summary to store
     */
    public static void storeTestResultSummaryInDB(
        ITestResultSummaryPO summary) {
        final EntityManager sess = Persistor.instance().openSession();
        try {            
            final EntityTransaction tx = 
                Persistor.instance().getTransaction(sess);
            sess.persist(summary);
            Persistor.instance().commitTransaction(sess, tx);
        } catch (PMException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Persistor.instance().dropSession(sess);
        }
    }
    
    /**
     * store the all of testresult summaries of test run in database
     * @param summaries the testresult summaries to store
     */
    public static void storeTestResultSummariesInDB(
        List<ITestResultSummaryPO> summaries) {
        final EntityManager sess = Persistor.instance().openSession();
        try {            
            final EntityTransaction tx = 
                Persistor.instance().getTransaction(sess);
            for (ITestResultSummaryPO summary : summaries) {
                sess.persist(summary);
            }
            Persistor.instance().commitTransaction(sess, tx);
        } catch (PMException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Persistor.instance().dropSession(sess);
        }
    }
    
    /**
     * saving collected monitoring data into DB. Instead of sess.persist() 
     * sess.merge() is called.
     * @param summary the testresult summary to store
     * @return the new managed summary instance
     */
    public static final ITestResultSummaryPO mergeTestResultSummaryInDB(
        ITestResultSummaryPO summary) {
        final EntityManager sess = Persistor.instance().openSession();
        try {            
            final EntityTransaction tx = 
                Persistor.instance().getTransaction(sess);
            ITestResultSummaryPO msummary = sess.merge(summary);
            Persistor.instance().commitTransaction(sess, tx);
            return msummary;
        } catch (PMException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Persistor.instance().dropSession(sess);
        }
    }
    

    /**
     * Checks whether the given Test Result Summary already exists in the 
     * currently connected database. This opens a session in order
     * to perform the check, and closes the session immediately thereafter.
     * 
     * @param summary The Test Result Summary to check.
     * @return <code>true</code> if <code>summary</code> already exists in the
     *         currently connected database. Otherwise <code>false</code>.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final boolean doesTestResultSummaryExist(
            ITestResultSummaryPO summary) {

        final EntityManager sess = Persistor.instance().openSession();
        try {            
            
            CriteriaBuilder builder = sess.getCriteriaBuilder();
            CriteriaQuery guidDuplicateQuery = builder.createQuery();
            Root guidDuplicateFrom = 
                guidDuplicateQuery.from(PoMaker.getTestResultSummaryClass());
            guidDuplicateQuery.select(builder.count(guidDuplicateFrom)).where(
                    builder.equal(
                            guidDuplicateFrom.get(PROPNAME_GUID), 
                            summary.getInternalGuid()));

            Number count = (Number)sess.createQuery(guidDuplicateQuery)
                    .getSingleResult();
            if (count.longValue() > 0) {
                return true;
            }

            CriteriaQuery duplicateQuery = builder.createQuery();
            Root duplicateFrom = 
                duplicateQuery.from(PoMaker.getTestResultSummaryClass());
            duplicateQuery.select(builder.count(duplicateFrom)).where(
                    builder.and(
                        builder.equal(
                            duplicateFrom.get(PROPNAME_TESTSUITE_DATE), 
                            summary.getTestsuiteDate()),
                        builder.equal(
                            duplicateFrom.get(PROPNAME_AUT_HOSTNAME), 
                            summary.getAutHostname()),
                        builder.equal(
                            duplicateFrom.get(PROPNAME_EXECUTED_TESTSTEPS), 
                            summary.getTestsuiteExecutedTeststeps()),
                        builder.equal(
                            duplicateFrom.get(PROPNAME_AUT_AGENT_NAME), 
                            summary.getAutAgentName())));

            count = (Number)sess.createQuery(duplicateQuery).getSingleResult();
            if (count.longValue() > 0) {
                log.error("Duplicate Test Result Summary (GUID="  //$NON-NLS-1$
                        + summary.getInternalGuid() 
                        + ") will not be imported."); //$NON-NLS-1$
                return true;
            }

            return false;
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(sess);
        }
    }

    /**
     * load metadata from database
     * 
     * @param startTime
     *            the test suite start time for summaries to be included
     * @return list of metadata objects
     */
    @SuppressWarnings({ "unchecked" })
    public static final List<ITestResultSummaryPO> findAllTestResultSummaries(
        Date startTime)
        throws JBException {
        EntityManager session = null;
        if (Persistor.instance() == null) {
            return null;
        }
        try {
            session = Persistor.instance().openSession();
            Query query = session
                    .createQuery("select s from TestResultSummaryPO as s " + //$NON-NLS-1$
                            "where s.testsuiteDate > :startTime"); //$NON-NLS-1$
            query.setParameter("startTime", startTime); //$NON-NLS-1$
            List<ITestResultSummaryPO> metaList = query.getResultList();
            return metaList;
        } catch (PersistenceException e) {
            log.error(Messages.PersistenceLoadFailed, e);
            throw new JBException(e.getMessage(),
                    MessageIDs.E_PERSISTENCE_LOAD_FAILED);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(session);
        }
    }
    
    /**
     * get set of test result summary ids older than amount of days
     * 
     * @param cleanDate
     *            Date
     * @param projGUID
     *            the project guid
     * @param majorVersion
     *            the project major version
     * @param minorVersion
     *            the project minor version
     * @param microVersion
     *            the project major version number
     * @param versionQualifier
     *            the project version qualifier
     * @return set of test result summary ids
     */
    @SuppressWarnings("unchecked")
    public static final Set<Long> findTestResultSummariesIfHasTestResultsByDate(
            Date cleanDate, String projGUID, Integer majorVersion,
            Integer minorVersion, Integer microVersion, String versionQualifier)
                    throws JBException {
        EntityManager session = null;
        if (Persistor.instance() == null) {
            log.error("Error while finding test result summaries. Persistor is null."); //$NON-NLS-1$
            return new HashSet<Long>();
        }
        try {
            session = Persistor.instance().openSession();

            StringBuilder queryString = new StringBuilder(
                    "select r.internalTestResultSummaryID from TestResultPO as r " + //$NON-NLS-1$
                            "where r.internalTestResultSummaryID in " + //$NON-NLS-1$
                            "(select s.id from TestResultSummaryPO as s " + //$NON-NLS-1$
                            "where s.testsuiteDate < :cleanDate " + //$NON-NLS-1$
                            "and s.internalProjectGuid = :projGUID " //$NON-NLS-1$
            );
            addVersionQueryString(PROPNAME_PROJECT_MAJOR_VERSION,
                    majorVersion, queryString);
            addVersionQueryString(PROPNAME_PROJECT_MINOR_VERSION,
                    minorVersion, queryString);
            addVersionQueryString(PROPNAME_PROJECT_MICRO_VERSION,
                    microVersion, queryString);
            addVersionQueryString(PROPNAME_PROJECT_VERSION_QUALIFIER,
                    versionQualifier, queryString);
            queryString.append(")"); //$NON-NLS-1$

            Query query = session.createQuery(queryString.toString());
            query.setParameter("cleanDate", cleanDate); //$NON-NLS-1$
            query.setParameter("projGUID", projGUID); //$NON-NLS-1$
            checkAndBindValue(PROPNAME_PROJECT_MAJOR_VERSION,
                    majorVersion, query);
            checkAndBindValue(PROPNAME_PROJECT_MINOR_VERSION,
                    minorVersion, query);
            checkAndBindValue(PROPNAME_PROJECT_MICRO_VERSION,
                    microVersion, query);
            checkAndBindValue(PROPNAME_PROJECT_VERSION_QUALIFIER,
                    versionQualifier, query);
            List<Long> metaList = query.getResultList();
            Set<Long> idSet = new HashSet<Long>(metaList);
            return idSet;
        } catch (PersistenceException e) {
            log.error(Messages.PersistenceLoadFailed, e);
            throw new JBException(e.getMessage(),
                    MessageIDs.E_PERSISTENCE_LOAD_FAILED);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(session);
        }
    }

    /**
     * get set of test result summary ids, (independently from having test
     * results or not) older than amount of days
     * 
     * @param cleanDate
     *            Date
     * @param projGUID
     *            the project guid, if not set, all projects will be searched
     * @param versionNrs
     *            the project versionNrs, if not set, all versions will be
     *            searched
     * @return set of test result summary ids
     */
    @SuppressWarnings("unchecked")
    public static final Set<Long> findTestResultSummariesByDate(Date cleanDate,
            String projGUID, ProjectVersion versionNrs) throws JBException {
        EntityManager session = null;
        if (Persistor.instance() == null) {
            log.error(
                    "Error while finding test result summaries. Persistor is null."); //$NON-NLS-1$
            return new HashSet<Long>();
        }
        try {
            session = Persistor.instance().openSession();

            StringBuilder queryString = new StringBuilder(
                    "select s.id from TestResultSummaryPO as s " + //$NON-NLS-1$
                            "where s.testsuiteDate < :cleanDate " + //$NON-NLS-1$
                            (projGUID != null
                                    ? "and s.internalProjectGuid = :projGUID " //$NON-NLS-1$
                                    : "") //$NON-NLS-1$
            );
            if (versionNrs != null) {
                addVersionQueryString(PROPNAME_PROJECT_MAJOR_VERSION,
                        versionNrs.getMajorNumber(), queryString);
                addVersionQueryString(PROPNAME_PROJECT_MINOR_VERSION,
                        versionNrs.getMinorNumber(), queryString);
                addVersionQueryString(PROPNAME_PROJECT_MICRO_VERSION,
                        versionNrs.getMicroNumber(), queryString);
                addVersionQueryString(PROPNAME_PROJECT_VERSION_QUALIFIER,
                        versionNrs.getVersionQualifier(), queryString);
            }

            Query query = session.createQuery(queryString.toString());
            query.setParameter("cleanDate", cleanDate); //$NON-NLS-1$
            if (projGUID != null) {
                query.setParameter("projGUID", projGUID); //$NON-NLS-1$
            }
            if (versionNrs != null) {
                checkAndBindValue(PROPNAME_PROJECT_MAJOR_VERSION,
                        versionNrs.getMajorNumber(), query);
                checkAndBindValue(PROPNAME_PROJECT_MINOR_VERSION,
                        versionNrs.getMinorNumber(), query);
                checkAndBindValue(PROPNAME_PROJECT_MICRO_VERSION,
                        versionNrs.getMicroNumber(), query);
                checkAndBindValue(PROPNAME_PROJECT_VERSION_QUALIFIER,
                        versionNrs.getVersionQualifier(), query);
            }
            List<Long> metaList = query.getResultList();
            Set<Long> idSet = new HashSet<Long>(metaList);
            return idSet;
        } catch (PersistenceException e) {
            log.error(Messages.PersistenceLoadFailed, e);
            throw new JBException(e.getMessage(),
                    MessageIDs.E_PERSISTENCE_LOAD_FAILED);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(session);
        }
    }

    /**
     * delete testruns
     * @param resultIDs array of test result ids 
     */
    public static final void deleteTestruns(Long[] resultIDs) {
        if (Persistor.instance() == null || resultIDs.length == 0) {
            return;
        }
        
        for (Long resultID : resultIDs) {
            deleteTestrun(resultID);
        }
        
    }
    
    /**
     * delete testrun
     * @param resultID id of test result
     */
    public static final void deleteTestrun(Long resultID) {
        if (Persistor.instance() == null) {
            return;
        }
        final EntityManager session = Persistor.instance().openSession();
        try {
            final EntityTransaction tx = Persistor.instance()
                    .getTransaction(session);
            
            TestResultPM.executeDeleteTestresultOfSummary(session,
                    resultID);

            Query querySummary = session
                .createQuery("select s from TestResultSummaryPO as s where s.id = :id"); //$NON-NLS-1$
            querySummary.setParameter("id", resultID); //$NON-NLS-1$

            try {
                ITestResultSummaryPO meta = 
                    (ITestResultSummaryPO)querySummary.getSingleResult();
                session.remove(meta);
            } catch (NoResultException nre) {
                // No result found. Nothing to delete.
            }
            Persistor.instance().commitTransaction(session, tx);
        } catch (PMException e) {
            log.error("Database exception while deleting testresults", e); //$NON-NLS-1$
            throw new JBFatalException(Messages.DeleteTestrunMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.DeleteTestrunMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Persistor.instance().dropSession(session);
        }
    }
    
    /**
     * delete testruns by project guid, minor, major version
     * @param guid project guid
     * @param version the project version which testruns should be deleted
     * @param deleteOnlyDetails true, if only testrun details will
     *          be deleted, summaries will not be deleted
     */
    @SuppressWarnings("unchecked")
    public static final void deleteTestrunsByProject(String guid,
            ProjectVersion version, boolean deleteOnlyDetails) {
        if (Persistor.instance() == null) {
            return;
        }
        final EntityManager session = Persistor.instance().openSession();
        try {
            final EntityTransaction tx = Persistor.instance()
                    .getTransaction(session);

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder
                    .append("select s from TestResultSummaryPO as s where s.") //$NON-NLS-1$
                    .append(PROPNAME_PROJECT_GUID).append(" = :guid"); //$NON-NLS-1$
            if (version != null) {
                addVersionQueryString(PROPNAME_PROJECT_MAJOR_VERSION,
                        version.getMajorNumber(), queryBuilder);
                addVersionQueryString(PROPNAME_PROJECT_MINOR_VERSION,
                        version.getMinorNumber(), queryBuilder);
                addVersionQueryString(PROPNAME_PROJECT_MICRO_VERSION,
                        version.getMicroNumber(), queryBuilder);
                addVersionQueryString(PROPNAME_PROJECT_VERSION_QUALIFIER,
                        version.getVersionQualifier(), queryBuilder);
            }

            Query querySummary = session.createQuery(queryBuilder.toString());
            querySummary.setParameter("guid", guid); //$NON-NLS-1$
            if (version != null) {
                checkAndBindValue(PROPNAME_PROJECT_MAJOR_VERSION,
                        version.getMajorNumber(), querySummary);
                checkAndBindValue(PROPNAME_PROJECT_MINOR_VERSION,
                        version.getMinorNumber(), querySummary);
                checkAndBindValue(PROPNAME_PROJECT_MICRO_VERSION,
                        version.getMicroNumber(), querySummary);
                checkAndBindValue(PROPNAME_PROJECT_VERSION_QUALIFIER,
                        version.getVersionQualifier(), querySummary);
            }
            List<ITestResultSummaryPO> summaryList = querySummary
                    .getResultList();

            for (ITestResultSummaryPO summary : summaryList) {
                TestResultPM.executeDeleteTestresultOfSummary(
                        session, summary.getId());
                if (!deleteOnlyDetails) {
                    session.remove(summary);
                }
            }
            Persistor.instance().commitTransaction(session, tx);
        } catch (PMException e) {
            log.error("Database exception while deleting testresults", e); //$NON-NLS-1$
            throw new JBFatalException(Messages.DeleteTestrunFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.DeleteTestrunFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Persistor.instance().dropSession(session);
        }
    }

    /**
     * Cheks if the property has a value and binds the the 
     * specified propertyname the value if its not null
     * @param propertyname name of the property
     * @param propertyvalue the property object
     * @param query the query
     */
    private static void checkAndBindValue(String propertyname,
            Object propertyvalue, Query query) {
        if (propertyvalue != null) {
            query.setParameter(propertyname, propertyvalue);
        }
    }

    /**
     * Adds the version selection part to the query string
     * @param propertyname the name of the property
     * @param propertyvalue the value of the property, only to check if it should be a null check
     * @param queryBuilder the query string
     */
    private static void addVersionQueryString(String propertyname,
            Object propertyvalue, StringBuilder queryBuilder) {
        queryBuilder.append(" and s."); //$NON-NLS-1$
        if (propertyvalue != null) {
            queryBuilder.append(propertyname).append(" = :")//$NON-NLS-1$ 
                .append(propertyname);     
        } else {
            queryBuilder.append(propertyname).append(" IS NULL"); //$NON-NLS-1$
        }
    }
    
    /**
     * delete all testresult summaries
     */
    public static final void deleteAllTestresultSummaries() {
        if (Persistor.instance() == null) {
            return;
        }
        final EntityManager session = Persistor.instance().openSession();
        try {
            final EntityTransaction tx = Persistor.instance()
                    .getTransaction(session);
            Query query = session.createQuery("delete from TestResultSummaryPO as s"); //$NON-NLS-1$
            query.executeUpdate();
            Persistor.instance().commitTransaction(session, tx);
        } catch (PMException e) {
            throw new JBFatalException(Messages.DeleteAllTestrunSummariesFailed,
                    e, MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.DeleteAllTestrunSummariesFailed,
                    e, MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Persistor.instance().dropSession(session);
        }
    }

    /**
     * Delete test-result summaries older than the given days
     * 
     * @param days
     *            days
     * @param projGUID
     *            the project guid
     * @param versionNrs
     *            the project version numbers
     * @param keepTRSummaries
     *            if true, test-result-summaries will not be deleted
     */
    public static final void cleanTestResultSummaries(int days, String projGUID,
            ProjectVersion versionNrs, boolean keepTRSummaries) {
        Date cleanDate = DateUtils.addDays(new Date(), days * -1);

        final EntityManager session = Persistor.instance().openSession();

        try {
            final EntityTransaction tx = Persistor.instance()
                    .getTransaction(session);

            Set<Long> summaries = TestResultSummaryPM
                    .findTestResultSummariesByDate(cleanDate, projGUID,
                            versionNrs);
            for (Long summaryId : summaries) {
                TestResultPM.executeDeleteTestresultOfSummary(session,
                        summaryId);

                if (!keepTRSummaries) {
                    Query querySummary = session.createQuery(
                            "select s from TestResultSummaryPO as s where s.id = :id"); //$NON-NLS-1$
                    querySummary.setParameter("id", summaryId); //$NON-NLS-1$

                    try {
                        ITestResultSummaryPO meta = (ITestResultSummaryPO) 
                                querySummary.getSingleResult();
                        session.remove(meta);
                    } catch (NoResultException nre) {
                        // No result found. Nothing to delete.
                    }
                }
            }
            Persistor.instance().commitTransaction(session, tx);
        } catch (PMException e) {
            log.error("Database exception while deleting testresults", e); //$NON-NLS-1$
            throw new JBFatalException(Messages.DeleteTestrunMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.DeleteTestrunMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } catch (JBException e) {
            throw new JBFatalException(Messages.DeletingTestresultsFailed, e,
                    MessageIDs.E_DELETE_TESTRESULT);
        } finally {
            Persistor.instance().dropSession(session);
        }
    }
}
