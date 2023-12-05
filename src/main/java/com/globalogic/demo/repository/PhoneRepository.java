package com.globalogic.demo.repository;

import com.globalogic.demo.entities.Phone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PhoneRepository extends CrudRepository<Phone, String> {

    @Query(value = "SELECT p FROM Phone p LEFT JOIN p.user u ON u.id = :id")
    List<Phone> findByUserId(@Param("id") UUID id);

}
