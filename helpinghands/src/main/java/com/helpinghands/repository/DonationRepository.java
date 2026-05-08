package com.helpinghands.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.helpinghands.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByChildId(Long childId);

    @Query("SELECT SUM(d.amount) FROM Donation d")
    Long sumAllDonations();
    
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.child.id = :childId")
    Long sumByChildId(@Param("childId") Long childId);
    
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.child.id = :childId")
    Long getTotalDonationByChildId(Long childId);
}