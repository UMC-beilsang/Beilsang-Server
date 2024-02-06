package com.BeilsangServer.domain.point.repository;

import com.BeilsangServer.domain.point.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointLogRepository extends JpaRepository<PointLog, Long> {
    List<PointLog> findAllByMember_Id(Long memberId);
}
