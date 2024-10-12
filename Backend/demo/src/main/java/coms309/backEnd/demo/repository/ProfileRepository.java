package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("SELECT p FROM Profile p WHERE p.user.netId = ?1")
    public Optional<Profile> findProfileByNetId(String netId);
}
