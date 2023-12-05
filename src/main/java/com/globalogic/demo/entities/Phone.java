package com.globalogic.demo.entities;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Phone {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private UUID id;

    private long number;

    private int citycode;

    private String contrycode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

}
