package com.example.report.repository;

import com.example.report.model.CellPhoneUsageByMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellPhoneUsageByMonthRepository extends JpaRepository<CellPhoneUsageByMonth, Long> {
    @Query(value = "FROM CellPhoneUsageByMonth WHERE employeeId = ?1")
    List<CellPhoneUsageByMonth> findDataByEmpId(long empId);
}
