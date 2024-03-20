package com.yyws.capstone_server.repository;

import com.yyws.capstone_server.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query(nativeQuery = true, value = "select * from Device")
    List<Device> findAll();

    @Query(nativeQuery = true, value = "select count(*) from Device")
    Integer countDevice();

//    @Override
//    <S extends Device> S save(S entity);
}
