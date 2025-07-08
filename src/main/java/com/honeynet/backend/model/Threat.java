//package com.honeynet.backend.model;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import java.util.List;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//
//@Entity
//public class Threat {
//
//    @Id
//    private String id;
//    private String name;
//
//    private String description;
//    private String author_name;
//    private String modified;
//    private String created;
//    private int revision;
//    private String tlp;
//
//    @JsonProperty("public")
//    private int publicVisibility;
//
//    private String adversary;
//
//    private List<Object> indicators;
//    private List<Object> tags;
//
//
//
//
//}
