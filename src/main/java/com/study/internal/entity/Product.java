package com.study.internal.entity;

import com.study.internal.plugins.Updatable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "product")

@ToString
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class Product extends Core implements Updatable<Product> {

    public interface ExtendedView extends Core.View {}
    public interface SimpleView extends Core.View {}

    @Enumerated(EnumType.STRING)
    private Category category; // categorys set which shop product will be allocated

    @Column(name = "price", nullable = false)
    private BigDecimal price = BigDecimal.valueOf(0);

    @Column(name = "discount", nullable = false)
    private int discountPercentage = 0; // monday to thursday has variable discount

    @JoinColumn(name = "store_id")
    @ManyToOne
    private Store store;

    public enum Category{
        FOOD,
        MEDICINES,
        CLOTHES
    }

    public void applyDiscount(int discountPercentage){
        BigDecimal multiplier = BigDecimal.valueOf(1.0 - (discountPercentage / 100.0));
        this.setPrice(this.price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
    }
 //   public void removeDiscount(){
 //       if(this.getDiscountPercentage() == 0) return;
    //       BigDecimal multiplier = BigDecimal.valueOf(1.0 - (this.getDiscountPercentage() / 100.0));
    //       this.setPrice(this.price.divide(multiplier, 2, RoundingMode.HALF_UP));
    //       this.setDiscountPercentage(0);
    //  }

    public void setDiscountPercentage(int discountPercentage){
        this.discountPercentage = discountPercentage;
        this.applyDiscount(discountPercentage);
    }

}
