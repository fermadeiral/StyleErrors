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
package org.eclipse.jubula.examples.aut.dvdtool.model;

import java.io.Serializable;


/**
 * This is the model for a dvd.
 *
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class Dvd implements Serializable {
    /** constant for fsk 6 */
    public static final int FSK_6  = 0;
    /** constant for fsk 16 */
    public static final int FSK_16 = 1;
    /** constant for fsk 18 */
    public static final int FSK_18 = 2;
    
    /** the title*/
    private String m_title;
    /** the main actor */
    private String m_actor;
    /** the year as Integer*/
    private Integer m_year;
    /** the direction */
    private String m_direction;
    /** flag for limited */
    private Boolean m_limited;

    /** the length in minutes*/
    private int m_length;
    /** flag for 'has bonus' */
    private boolean m_bonus;
    /** the region code*/
    private int m_regionCode;
    /** array with availabe languages */
    private int[] m_languages;

    /** the number of chapters */
    private int m_chapters;
    /** the fsk, see constants */
    private int m_fsk;
    /** the description */
    private String m_description;
    
    /** the category of this Dvd */
    private DvdCategory m_category;
    
    /**
     * the default constructor, initialises all values with defaults, except of
     * the category, it will be null
     */
    public Dvd() {
        this("", //$NON-NLS-1$
             "", //$NON-NLS-1$
             0,
             "", //$NON-NLS-1$
             false, 
             0, 
             false, 
             0, 
             new int[] { 0 }, 
             0, 
             FSK_6, 
             "", //$NON-NLS-1$
             null);
    }

    /**
     * public constructor with parameters for all properties of this model
     * 
     * @param title the title
     * @param actor the main actor
     * @param year the year
     * @param direction the direction
     * @param limited flag for limited
     * @param length the length
     * @param bonus flag for 'has bonus'
     * @param regionCode the region code
     * @param languages the available languages
     * @param chapters the number of chapters
     * @param fsk the fsk, see constants
     * @param description the description
     * @param category the category
     */
    public Dvd(String title, String actor, int year, String direction, 
            boolean limited, int length, boolean bonus, int regionCode, 
            int[] languages, int chapters, int fsk, String description, 
            DvdCategory category) {

        m_title = title;
        m_actor = actor;
        m_year = new Integer(year);
        m_direction = direction;
        m_limited = limited ? Boolean.TRUE : Boolean.FALSE;  // see findBugs
        m_length = length;
        m_bonus = bonus;
        m_regionCode = regionCode;
        int[] langs = languages; // see findBugs
        m_languages = langs; // see findBugs
        m_chapters = chapters;
        m_fsk = fsk;
        m_description = description;
        m_category = category;
    }

    /**
     * @return the main actor
     */
    public String getActor() {
        return m_actor;
    }

    /**
     * sets (changes) the actor
     * @param newActor the new actor
     */
    public void setActor(String newActor) {
        m_actor = newActor;
    }

    /**
     * @return whether the dvd has bonus or not
     */
    public boolean hasBonus() {
        return m_bonus;
    }

    /**
     * sets (changes) the flag for 'has bonus'
     * @param newBonus the new value for 'has bonus'
     */
    public void setBonus(boolean newBonus) {
        m_bonus = newBonus;
    }

    /**
     * @return the number of chapters
     */
    public int getChapters() {
        return m_chapters;
    }

    /**
     * sets (changes) the value for the number of chapters 
     * @param newChapters the new number of chapters
     */
    public void setChapters(int newChapters) {
        m_chapters = newChapters;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * sets (changes) the value for the description 
     * @param newDescription the new description
     */
    public void setDescription(String newDescription) {
        m_description = newDescription;
    }

    /**
     * @return the direction
     */
    public String getDirection() {
        return m_direction;
    }

    /**
     * sets (changes) the value for the direction
     * @param newDirection the new direction
     */
    public void setDirection(String newDirection) {
        m_direction = newDirection;
    }

    /**
     * @return the fsk, see constants
     */
    public int getFsk() {
        return m_fsk;
    }

    /**
     * sets (changes) the value for fsk, see also the constants
     * @param newFsk the new value for fsk
     */
    public void setFsk(int newFsk) {
        m_fsk = newFsk;
    }

    /**
     * @return the available languages
     */
    public int[] getLanguages() {
        int[] languages = m_languages; // see findBugs
        return languages;  // see findBugs
    }

    /**
     * sets (changes) the availaible languages
     * @param newLanguages an array containing the indices for the languages
     */
    public void setLanguages(int[] newLanguages) {
        int[] languages = newLanguages;
        m_languages = languages;
    }

    /**
     * @return the length in minutes
     */
    public int getLength() {
        return m_length;
    }

    /**
     * sets (changes) the value for the length 
     * @param newLength the new length in minutes
     */
    public void setLength(int newLength) {
        m_length = newLength;
    }

    /**
     * @return whether is limited or not
     */
    public Boolean isLimited() {
        return m_limited;
    }

    /**
     * sets (changes) the flag for limited
     * @param newLimited the new value for limited
     */
    public void setLimited(boolean newLimited) {
        m_limited = newLimited ? Boolean.TRUE : Boolean.FALSE; // see findBugs
    }

    /**
     * @return the region code
     */
    public int getRegionCode() {
        return m_regionCode;
    }

    /**
     * sets (changes) the value for the region code
     * @param newRegionCode the new region code
     */
    public void setRegionCode(int newRegionCode) {
        m_regionCode = newRegionCode;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * sets (changes) the title
     * @param newTitle the new title
     */
    public void setTitle(String newTitle) {
        m_title = newTitle;
    }

    /**
     * @return the year
     */
    public Integer getYear() {
        return m_year;
    }

    /**
     * sets (changes) the value for the year 
     * @param newYear the new year
     */
    public void setYear(int newYear) {
        m_year = new Integer(newYear);
    }
    /**
     * @return Returns the category.
     */
    public DvdCategory getCategory() {
        return m_category;
    }
    /**
     * @param category The category to set.
     */
    public void setCategory(DvdCategory category) {
        m_category = category;
    }
}
