package com.example.pixel.repository;

import com.example.pixel.model.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    Optional<PhoneData> findByPhone(String phone);

    List<PhoneData> findByUserIdIn(List<Long> userIds);
}
