package com.foodDelivery.project.repository;

import com.foodDelivery.project.domen.model.Warehouse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    @Query(nativeQuery = true, value = "SELECT id FROM postgres.public.warehouse")
    List<Long> findAllId();
}
