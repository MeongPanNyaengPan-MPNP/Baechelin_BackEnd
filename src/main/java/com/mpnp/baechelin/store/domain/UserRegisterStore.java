package com.mpnp.baechelin.store.domain;

import com.mpnp.baechelin.util.TimeStamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class UserRegisterStore extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    

}
