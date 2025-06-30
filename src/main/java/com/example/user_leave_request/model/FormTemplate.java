package com.example.user_leave_request.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FormTemplate {
    @Id
    @GeneratedValue
    private Long id;
    private String formType;
    private String title;
    @Lob
    private String schemaJson;
}

