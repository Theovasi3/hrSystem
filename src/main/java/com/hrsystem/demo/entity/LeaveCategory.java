package com.hrsystem.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leave_categories")
@Data
@Builder
@AllArgsConstructor
public class LeaveCategory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String categoryName;


    public LeaveCategory(){}

    public LeaveCategory(String categoryName) {
        this.categoryName = categoryName;
    }

}
