package com.example.report.repository;

import com.example.report.model.CellPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CellPhoneRepository extends JpaRepository<CellPhone, Long> {

}
