package com.yyws.capstone_server.repository;

import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModelRepository extends JpaRepository<Model,Long> {

    @Query(nativeQuery = true, value = "select * from Model")
    List<Model> findAll();
}
