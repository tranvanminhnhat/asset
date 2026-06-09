package com.minhnhat.demo.common.db.repository;
import com.minhnhat.demo.common.db.entity.AssetCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
@Repository
public interface AssetCategoryRepository extends JpaRepository<AssetCategoryEntity, Integer> {
    Optional<AssetCategoryEntity> findByCode(String code);
    List<AssetCategoryEntity> findAllByIsActive(Boolean isActive);
}
