package at.rvarnai.taxformula.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxChargeDao extends JpaRepository<TaxChargeEntity, Long> {

    List<TaxChargeEntity> findByYear(Integer year);
}