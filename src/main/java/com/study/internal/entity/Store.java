package com.study.internal.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.study.internal.plugins.Updatable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "store")

@ToString
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Store extends Core implements Updatable<Store> {

    public interface ExtendedView extends View{}
    public interface SimpleView extends View{}

    @ManyToOne
    @JsonView(ExtendedView.class)
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @JsonView(SimpleView.class)
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Employee> employees = new HashSet<>();

    @JsonView(SimpleView.class)
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    @Column(name = "budget", nullable = false)
    private BigDecimal budget = BigDecimal.valueOf(0);

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    private Category category;

    private enum Category{
        FOOD,
        MEDICINES,
        CLOTHES
    }

}

