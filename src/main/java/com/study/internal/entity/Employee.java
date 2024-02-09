package com.study.internal.entity;

import com.study.internal.plugins.Updatable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Table(name = "employee")

@ToString
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Employee extends Core implements Updatable<Employee> {

    public interface ExtendedView extends View{}
    public interface SimpleView extends View{}

    @Enumerated(EnumType.STRING)
    private Role role = Role.ATTENDANT;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary; // initial salary depends on shop category

    @JoinColumn(name = "store_id")
    @ManyToOne
    private Store store;

    public enum Role{
        ADMIN, //register of products and increase 30% on salary
        ATTENDANT
    }
}
