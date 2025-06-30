package com.example.user_leave_request.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WorkflowTemplate {
    @Id
    @GeneratedValue
    private Long id;
    private String formType;
    @Lob
    private String statesJson;
    @Lob private String transitionsJson;
    @OneToMany(mappedBy = "workflowTemplate", cascade = CascadeType.ALL)
    private List<Transition> transitions;


    public List<String> getParsedStates() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(statesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
