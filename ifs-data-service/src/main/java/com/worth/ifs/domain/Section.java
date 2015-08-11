package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Section defines database relations and a model to use client side and server side.
 */

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @OneToMany(mappedBy="section")
    private List<Question> questions;

    @ManyToOne()
    @JoinColumn(name="parentSectionId", referencedColumnName="id")
    private Section parentSection;

    @OneToMany(mappedBy="parentSection")
    private Set<Section> childSections;

    private String name;

    public Section(long id, Competition competition, List<Question> questions, String name, Section parentSection) {
        this.id = id;
        this.competition = competition;
        this.questions = questions;
        this.name = name;
        this.parentSection = parentSection;
    }

    public Section () {

    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Long getId() {
        return id;
    }


    public Section getParentSection() {
        return parentSection;
    }

    public Set<Section> getChildSections() {
        return childSections;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public void setChildSections(Set<Section> childSections) {
        this.childSections = childSections;
    }

    public void setParentSection(Section parentSection) {
        this.parentSection = parentSection;
    }
}
