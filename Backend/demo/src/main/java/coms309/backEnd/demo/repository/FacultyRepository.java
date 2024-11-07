package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    @Query("SELECT f FROM Faculty f WHERE f.user.netId = :netId")
    Optional<Faculty> findByUserNetId(@Param("netId") String netId);}
