package com.ventimetriquadriconsulting.restaurant.restaurant.configuration.opening_configuration.entity;


import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "opening_configuration")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OpeningConfiguration {


    @Id
    @SequenceGenerator(
            name = "open_conf_id",
            sequenceName = "open_conf_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "open_conf_id"
    )
    @Column(
            name = "open_conf_id",
            updatable = false
    )
    private Long openConfId;



    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;


}
