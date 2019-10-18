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
package org.eclipse.jubula.rc.common.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;


/**
 * @created Nov 30, 2006
 */
public abstract class FindComponentBP {
    /** the logger */
    private static final AutServerLogger LOG = 
        new AutServerLogger(FindComponentBP.class);

    /** the factor how strong path equivalenz will increase total equivalenz */
    private double m_pathFactor = Constants.PATH_FACTOR;
    /** the factor how strong context equivalenz will increase total equivalenz */
    private double m_contextFactor = Constants.CONTEXT_FACTOR;
    /** the threshold value, when a component will be selected as equivalents */
    private double m_thresholdValue = Constants.THRESHOLD_VALUE;
    /** the factor how strong name equivalenz will increase total equivalenz */
    private double m_nameFactor = Constants.NAME_FACTOR;
    /** the current AUT hierarchy */
    private AUTHierarchy m_hierarchy;
    
    /**
     * Search for the component in the AUT with the given
     * <code>componentIdentifier</code>.
     * 
     * @param componentIdentifier the identifier created in object mapping mode
     * @param hierarchy the current AUT hierarchy
     * @throws IllegalArgumentException if the given identifier is null or <br>
     *             the hierarchy is not valid: empty or containing null elements
     * @return the instance of the component of the AUT 
     */
    public Object findComponent(
            final IComponentIdentifier componentIdentifier, 
            final AUTHierarchy hierarchy)
        throws IllegalArgumentException {
        
        m_hierarchy = hierarchy;
        AUTServerConfiguration serverConfig = 
                AUTServerConfiguration.getInstance();
        org.eclipse.jubula.tools.Profile p = componentIdentifier.getProfile();
        if (p != null && p instanceof Profile) {
            setProfile((Profile) p);
        } else {
            setProfile(serverConfig.getProfile());
        }
        // Fuzzy logic parameter
        List<String> hierarchyNames = null;
        // parameter check
        Validate.notNull(componentIdentifier, "The component identifier must not be null."); //$NON-NLS-1$
        hierarchyNames = componentIdentifier.getHierarchyNames();
        Validate.noNullElements(hierarchyNames,
                "The component identifier contains  no hierarchy information."); //$NON-NLS-1$
        Iterator<HierarchyContainer> allComponents = 
            new ArrayList<HierarchyContainer>(m_hierarchy.getHierarchyMap()
                .values()).iterator();
        HierarchyContainer bestMatch = null;
        double bestMatchPercentage = 0;
        double equivalence = 0;
        String suppClassName = componentIdentifier.getSupportedClassName();
        final AUTServerConfiguration autServerConf = AUTServerConfiguration
            .getInstance();
        int numberOfOtherMatchingComponents = 0;
        while (allComponents.hasNext()) {
            HierarchyContainer current = allComponents.next();
            Object currComp = current.getCompID().getComponent();
            // check class compatibility first
            if (isAvailable(currComp) 
                && isSupportedComponent(currComp) && ((suppClassName != null
                && (checkTestableClass(autServerConf, suppClassName, currComp)))
                || componentIdentifier.getComponentName().equals(getCompName(
                        currComp)))
            ) {
                equivalence = computeEquivalence(componentIdentifier, current);
                if (meetsThreshold(equivalence)) {
                    numberOfOtherMatchingComponents++;
                }
                if (equivalence > bestMatchPercentage) {
                    bestMatch = current;
                    bestMatchPercentage = equivalence;
                } else if (equivalence == bestMatchPercentage
                        && hierarchy.isInActiveWindow(currComp)) {
                    bestMatch = current;
                }
            }
        }
        Object technicalComponent = null;
        if (bestMatch != null 
                && meetsThreshold(bestMatchPercentage)) {
            technicalComponent = bestMatch.getCompID().getComponent();
        }
        componentIdentifier.setMatchPercentage(
                bestMatchPercentage);
        componentIdentifier.setNumberOfOtherMatchingComponents(
                numberOfOtherMatchingComponents);
        return technicalComponent;
    }
    
    
    /**
     * @param currComp the current component
     * @return false if component is available.
     */
    protected abstract boolean isAvailable(Object currComp);


    /**
     * Checks if the given current component class or one of its superclass is testable 
     * with the given suppClassName.
     * @param autServerConf the AUTServerConfiguration
     * @param suppClassName the class name of the supported component 
     * @param currComp the current component to test
     * @return true if testable, false otherwise.
     */
    protected boolean checkTestableClass(
        final AUTServerConfiguration autServerConf, String suppClassName, 
        Object currComp) {
        
        boolean isTestable = false;
        try {
            isTestable = suppClassName.equals(autServerConf.getTestableClass(
                currComp.getClass()).getName());
        } catch (IllegalArgumentException iae) { // NOPMD by zeb on 10.04.07 15:13
            // The class is not supported
            // Do nothing -> isTestable is still false
        }
        Class superClass = currComp.getClass();
        while (!isTestable && superClass != null) {
            superClass = superClass.getSuperclass();
            try {
                isTestable = suppClassName.equals(autServerConf
                        .getTestableClass(superClass).getName());
            } catch (IllegalArgumentException iae) { // NOPMD by zeb on 10.04.07 15:13
                // The class is not supported
                // Do nothing -> isTestable is still false
            }
        }
        return isTestable;
    }
    
    

    /**
     * checks if a component is supported
     * @param component Component
     * @return  boolean
     */
    protected boolean isSupportedComponent(Object component) {
        try {
            AUTServerConfiguration.getInstance().getTestableClass(
                component.getClass());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    
    /**
     * @param profile the profile to set
     */
    private void setProfile(Profile profile) {
        if (profile != null) {
            m_nameFactor = profile.getNameFactor();
            m_pathFactor = profile.getPathFactor();
            m_contextFactor = profile.getContextFactor();
            m_thresholdValue = profile.getThreshold();
        }
    }

    /**
     * computes the Equivalence of 2 components
     * 
     * @param compIdent
     *            ComponentIdentifier
     * @param current
     *            HierarchyContainer
     * @return double indicating the equivalence; THIS METHOD uses a shortcut to
     *         decide continuing the equivalence computing or not - this may
     *         lead to a "guessed" equivalence which is higher as the real
     *         equivalence but still lower than the specified threshold
     *         equivalence.
     */
    private double computeEquivalence(IComponentIdentifier compIdent,
            HierarchyContainer current) {
        double equivalence = 0;
        // name equivalence
        String name1 = compIdent.getComponentName();
        String name2 = current.getName();
        double nameEquivalence = 0;
        if (isGeneratedName(compIdent.getComponentClassName(), compIdent
                .getComponentName())) {
            nameEquivalence = getNameEquivalence(name1, name2);
        } else {
            nameEquivalence = name1.equals(name2) ? 1 : 0;
        }
        /* check for computing shortcut if equivalence may not meet the
         * threshold, although path==1 && context==1 */
        equivalence = equivalence(nameEquivalence, 1, 1);
        if (!meetsThreshold(equivalence)) {
            return equivalence;
        }
        // path equivalence
        double pathEquivalence = getPathEquivalence(compIdent, current);
        /* check for computing shortcut if equivalence may not meet the
         * threshold, although context==1 */
        equivalence = equivalence(nameEquivalence, pathEquivalence, 1);
        if (!meetsThreshold(equivalence)) {
            return equivalence;
        }
        
        // context equivalence
        double contextEquivalence = getContextEquivalence(compIdent, current);
        // calculate total equivalence
        equivalence = equivalence(nameEquivalence, 
                                  pathEquivalence,
                                  contextEquivalence);

        logEquivalence(compIdent, current, equivalence, nameEquivalence,
                pathEquivalence, contextEquivalence);
        return equivalence;
    }
    
    /**
     * @param equivalence the current equivalence
     * @return true if it meet's the threshold, false otherwise
     */
    private boolean meetsThreshold(double equivalence) {
        return (equivalence - m_thresholdValue + 1e-2) > 0;
    }

    /**
     * @param nameE
     *            the current name equivalence
     * @param pathE
     *            the current path equivalence
     * @param contE
     *            the current context equivalence
     * @return the weighted equivalence
     */
    private double equivalence(double nameE, double pathE, double contE) {
        return m_nameFactor * nameE 
             + m_pathFactor * pathE 
             + m_contextFactor * contE;
    }
    
    
    /**
     * log the results from the equivalence computation
     * @param componentIdentifier which component is looked up
     * @param current which GUI component is evaluated
     * @param equivalence computed equivalence
     * @param nameEquivalence   equivalence for name
     * @param pathEquivalence   equivalence for path
     * @param contextEquivalence equivalence for context
     */
    private void logEquivalence(IComponentIdentifier componentIdentifier,
            HierarchyContainer current, double equivalence,
            double nameEquivalence, double pathEquivalence,
            double contextEquivalence) {
        if (LOG.isInfoEnabled()) {
            StringBuffer txt = new StringBuffer(500);
            txt.append("Equivalence values for Identifier "); //$NON-NLS-1$
            txt.append(componentIdentifier.getComponentNameToDisplay());
            txt.append(" , matched against "); //$NON-NLS-1$
            txt.append(current.getName());
            txt.append(" , threshold value: "); //$NON-NLS-1$
            txt.append(m_thresholdValue);
            txt.append("\n"); //$NON-NLS-1$
            txt.append("Equivalence total: "); //$NON-NLS-1$
            txt.append(equivalence);
            txt.append(" name: "); //$NON-NLS-1$
            txt.append(nameEquivalence * m_nameFactor);
            txt.append(" path: "); //$NON-NLS-1$
            txt.append(pathEquivalence * m_pathFactor);
            txt.append(" context: "); //$NON-NLS-1$
            txt.append(contextEquivalence * m_contextFactor);
            final String txtString = txt.toString();
            LOG.info(txtString);
        }
        
    }

    /**
     * computes the Equivalence of 2 names<p>
     * Example :<p>
     *  jButton1 <=> jButton1 = 100.0<p>
     *  jButton1 <=> jButton2 = 87.5<p>
     *  jButton1 <=> jTextField1 = 20.0 <p>
     *  jButton1 <=> jTextField2 = 10.0 <p>
     * @param name1 String
     * @param name2 String
     * @return  percentage as double
     */
    private double getNameEquivalence(String name1, String name2) {
        int diff = StringUtils.getLevenshteinDistance(name1, name2);
        double nameEquivalence = 
            1.0 / Math.max(name1.length(), name2.length()) 
            * (Math.max(name1.length(), name2.length()) - diff);
        return nameEquivalence;
    }
    
    /**
     * @param comp ComponentIdentifier
     * @param hierarchyContainer SwtHierarchyContainer
     * @return percentage as double
     */
    private double getPathEquivalence(IComponentIdentifier comp, 
            HierarchyContainer hierarchyContainer) {
        if ((comp.getHierarchyNames() == null)
                || (comp.getHierarchyNames().size() == 0)) {
            return 0;
        }
        List<String> l1 = comp.getHierarchyNames().subList(
            0, comp.getHierarchyNames().size() - 1);
        List<String> l2 = new ArrayList<String>();
        HierarchyContainer iter = hierarchyContainer.getPrnt();
        while (iter != null) {
            l2.add(0, iter.getName());
            iter = iter.getPrnt();
        }
        double pathEquivalence = 0;
        if (l1.size() == 0 && l2.size() == 0) {
            pathEquivalence = 1;
        } else {
            int diff = getLevenshteinListDistanceImp(l1, l2);
            pathEquivalence = 
                1.0 / Math.max(l1.size(), l2.size()) 
                * (Math.max(l1.size(), l2.size()) - diff);
        }

        return pathEquivalence;
    }
    
    /**
     * @param comp ComponentIdentifier
     * @param hierarchyContainer HierarchyContainer
     * @return percentage as double
     */
    private double getContextEquivalence(IComponentIdentifier comp, 
            HierarchyContainer hierarchyContainer) {
        
        List<String> compNeighbours = comp.getNeighbours();
        List<String> compContext = m_hierarchy.getComponentContext(
                hierarchyContainer.getCompID().getComponent());
        Collections.sort(compNeighbours);
        Collections.sort(compContext);
        double contextEquivalence = 0;
        final int compNeighboursSize = compNeighbours.size();
        final int compContextSize = compContext.size();
        if (compNeighboursSize == 0 && compContextSize == 0) {
            contextEquivalence = 1;
        } else {
            int diff = getLevenshteinListDistanceImp(compNeighbours, 
                compContext);
            contextEquivalence = 
                1.0 / Math.max(compNeighboursSize, compContextSize) 
                * (Math.max(compNeighboursSize, compContextSize) - diff);
        }
        return contextEquivalence;
    }
    
    /**
     * Find the Levenshtein distance between two Lists. Heavily based on the
     * {@link StringUtils#getLevenshteinDistance(String, String) Apache Commons 
     * implementation} for Strings.
     * 
     * @see StringUtils#getLevenshteinDistance(String, String)
     * @param s The first List. Must not be <code>null</code>.
     * @param t The second List. Must not be <code>null</code>.
     * @return result distance
     */
    private int getLevenshteinListDistanceImp (List<String> s, List<String> t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Lists must not be null"); //$NON-NLS-1$
        }
        
        /*
         The difference between this impl. and the previous is that, rather 
         than creating and retaining a matrix of size s.length()+1 by t.length()+1, //$NON-NLS-1$ 
         we maintain two single-dimensional arrays of length s.length()+1.  The first, d, //$NON-NLS-1$
         is the 'current working' distance array that maintains the newest distance cost //$NON-NLS-1$
         counts as we iterate through the characters of String s.  Each time we increment //$NON-NLS-1$
         the index of String t we are comparing, d is copied to p, the second int[].  Doing so //$NON-NLS-1$
         allows us to retain the previous cost counts as required by the algorithm (taking  //$NON-NLS-1$
         the minimum of the cost count to the left, up one, and diagonally up and to the left //$NON-NLS-1$
         of the current cost count being calculated).  (Note that the arrays aren't really  //$NON-NLS-1$
         copied anymore, just switched...this is clearly much better than cloning an array  //$NON-NLS-1$
         or doing a System.arraycopy() each time  through the outer loop.) //$NON-NLS-1$
         
         Effectively, the difference between the two implementations is this one does not //$NON-NLS-1$ 
         cause an out of memory condition when calculating the LD over two very large strings. //$NON-NLS-1$        
         */ 
        final int sSize = s.size();
        final int tSize = t.size();
        if (sSize == 0) {
            return tSize;
        } else if (tSize == 0) {
            return sSize;
        }
        int p[] = new int[sSize + 1]; //'previous' cost array, horizontally
        int d[] = new int[sSize + 1]; // cost array, horizontally
        int swapPAndD[]; //placeholder to assist in swapping p and d
        // indexes into strings s and t
        int sIdx; // iterates through s
        int tIdx; // iterates through t
        Object currCharOfT;
        int cost;
        for (sIdx = 0; sIdx <= sSize; sIdx++) {
            p[sIdx] = sIdx;
        }
        for (tIdx = 1; tIdx <= tSize; tIdx++) {
            currCharOfT = t.get(tIdx - 1);
            d[0] = tIdx;
            for (sIdx = 1; sIdx <= sSize; sIdx++) {
                cost = s.get(sIdx - 1).equals(currCharOfT) ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost              
                d[sIdx] = Math.min(Math.min(d[sIdx - 1] + 1, 
                        p[sIdx] + 1),  p[sIdx - 1] + cost);  
            }
            // copy current distance counts to 'previous row' distance counts
            swapPAndD = p;
            p = d;
            d = swapPAndD;
        } 
        // our last action in the above loop was to switch d and p, so p now 
        // actually has the most recent cost counts
        return p[sSize];
    }

    /**
     * checks if name is a generated Name
     * @param className String
     * @param name String
     * @return boolean
     */
    private boolean isGeneratedName(String className, String name) {
        return (name.indexOf(className) != -1);
    }
    
    /**
     * @param currentComponent the component to get the name for
     * @return the component name of the current component
     */
    protected abstract String getCompName(Object currentComponent);
}