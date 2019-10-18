/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jubula.client.core.model.INodePO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author BREDEX GmbH
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,  
        include = JsonTypeInfo.As.PROPERTY,  
        property = "type") 
@JsonSubTypes({  
    @Type(value = CategoryDTO.class, name = "cat"),
    @Type(value = CommentDTO.class, name = "com"),
    @Type(value = ExecCategoryDTO.class, name = "ecat"),
    @Type(value = ParameterDTO.class, name = "par"),
    @Type(value = ProjectDTO.class, name = "pro"),
    @Type(value = TestSuiteDTO.class, name = "ts"),
    @Type(value = RefTestSuiteDTO.class, name = "rts"),
    @Type(value = TestJobDTO.class, name = "tj"),
    @Type(value = ConditionDTO.class, name = "cod")})
public class NodeDTO {
    
    /** children */
    private List<NodeDTO> m_nodes = new ArrayList<NodeDTO>();
    
    /** */
    private String m_name, m_comment, m_uuid, m_taskId, m_description;
    /** */
    private boolean m_generated, m_active, m_isJunitSuite;
    /** */
    private SortedMap<Long, String> m_trackedModifications =
            new TreeMap<Long, String>();
    
    /** needed because JSON mapping */
    public NodeDTO() { }
    
    /**
     * @param node 
     */
    public NodeDTO(INodePO node) {
        this.setName(node.getName());
        this.setComment(node.getComment());
        this.setDescription(node.getDescription());
        this.setUuid(node.getGuid());
        this.setGenerated(node.isGenerated());
        this.setActive(node.isActive());
        this.setTaskId(node.getTaskId());
        this.setTrackedModifications(node.getTrackedChanges());
        this.setJunitSuite(node.isJUnitTestSuite());
    }

    /**
     * @return name
     */
    @JsonProperty("name")
    public String getName() {
        return m_name;
    }

    /**
     * @param name 
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * @return comment
     */
    @JsonProperty("comment")
    public String getComment() {
        return m_comment;
    }

    /**
     * @param comment 
     */
    public void setComment(String comment) {
        this.m_comment = comment;
    }

    /**
     * @return uuid
     */
    @JsonProperty("uuid")
    public String getUuid() {
        return m_uuid;
    }

    /**
     * @param uuid 
     */
    public void setUuid(String uuid) {
        this.m_uuid = uuid;
    }

    /**
     * @return generated
     */
    @JsonProperty("generated")
    public boolean getGenerated() {
        return m_generated;
    }
    
    /**
     * @param generated 
     */
    public void setGenerated(boolean generated) {
        this.m_generated = generated;
    }

    /**
     * @return active
     */
    @JsonProperty("active")
    public boolean isActive() {
        return m_active;
    }

    /**
     * @param active 
     */
    public void setActive(boolean active) {
        this.m_active = active;
    }

    /**
     * @return taskId
     */
    @JsonProperty("taskId")
    public String getTaskId() {
        return m_taskId;
    }

    /**
     * @param taskId 
     */
    public void setTaskId(String taskId) {
        this.m_taskId = taskId;
    }

    /**
     * @return trackedModifications
     */
    @JsonProperty("trackedModifications")
    public SortedMap<Long, String> getTrackedModifications() {
        return m_trackedModifications;
    }
    
    /**
     * @param key 
     * @param value 
     */
    public void putTrackedModification(Long key, String value) {
        this.m_trackedModifications.put(key, value);
    }

    /**
     * @param map 
     */
    public void setTrackedModifications(SortedMap<Long, String> map) {
        this.m_trackedModifications = map;
    }

    /**
     * @return description
     */
    @JsonProperty("description")
    public String getDescription() {
        return m_description;
    }

    /**
     * @param description 
     */
    public void setDescription(String description) {
        this.m_description = description;
    }
    
    /**
     * @return nodes
     */
    @JsonProperty("nodes")
    public List<NodeDTO> getNodes() {
        return m_nodes;
    }
    
    /**
     * @param node 
     */
    public void addNode(NodeDTO node) {
        this.m_nodes.add(node);
    }

    /**
     * @return boolean
     */
    @JsonProperty("isJunitSuite")
    public boolean isJunitSuite() {
        return m_isJunitSuite;
    }

    /**
     * @param isJunitSuite boolean value
     */
    public void setJunitSuite(boolean isJunitSuite) {
        m_isJunitSuite = isJunitSuite;
    }
}
