package com.study.internal.entity;

import com.study.internal.plugins.Updatable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Table(name = "employer")

@ToString
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Employer extends Core implements Updatable<Employer> {

    public interface ExtendedView extends View{}
    public interface SimpleView extends View{}

    @Column(name = "budget", nullable = false)
    private BigDecimal budget = BigDecimal.valueOf(0);

    @Column(name = "earnings_percentage")
    private int earningsPercentage;

    @Column(name = "participation_percentage")
    private int participationPercentage;

}
