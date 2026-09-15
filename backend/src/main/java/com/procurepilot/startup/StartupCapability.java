package com.procurepilot.startup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "startup_capabilities")
public class StartupCapability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String name;

    private String proficiencyLevel = "ADVANCED"; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    @Column(columnDefinition = "TEXT")
    private String description;

    public StartupCapability() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Startup getStartup() { return startup; }
    public void setStartup(Startup startup) { this.startup = startup; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(String proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
