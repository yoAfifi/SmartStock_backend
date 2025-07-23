package com.mwaf.customerservice.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "customers")
//@Audited
@Getter
@Setter
public class Customer extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    private String address;
}