package com.migration.sap.repository;

import com.migration.sap.entity.MaterialStock;
import com.migration.sap.entity.MaterialStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for material stock queries.
 * Contains 4 native query methods matching the 4 SQL branches in ZMAT_REPORT.PROG fetch_data (lines 59-172).
 */
@Repository
public interface MaterialStockRepository extends JpaRepository<MaterialStock, MaterialStockId> {

    /**
     * byPlant=true, plant=null: All plants, grouped by MATNR+WERKS.
     * Maps to ZMAT_REPORT.PROG lines 69-83.
     */
    @Query(value = "SELECT m.matnr, mt.maktx, m.mtart, m.meins, ms.werks, COALESCE(SUM(ms.labst), 0) AS labst " +
            "FROM material m " +
            "LEFT JOIN material_text mt ON mt.matnr = m.matnr AND mt.spras = :lang " +
            "LEFT JOIN material_stock ms ON ms.matnr = m.matnr " +
            "WHERE (:matnrList IS NULL OR m.matnr IN (:matnrList)) " +
            "AND (:mtartList IS NULL OR m.mtart IN (:mtartList)) " +
            "GROUP BY m.matnr, mt.maktx, m.mtart, m.meins, ms.werks", nativeQuery = true)
    List<Object[]> findByPlantAllPlants(@Param("lang") String lang,
                                        @Param("matnrList") List<String> matnrList,
                                        @Param("mtartList") List<String> mtartList);

    /**
     * byPlant=true, plant specified: Specific plant, grouped by MATNR.
     * Maps to ZMAT_REPORT.PROG lines 84-100.
     */
    @Query(value = "SELECT m.matnr, mt.maktx, m.mtart, m.meins, :plant AS werks, COALESCE(SUM(ms.labst), 0) AS labst " +
            "FROM material m " +
            "LEFT JOIN material_text mt ON mt.matnr = m.matnr AND mt.spras = :lang " +
            "LEFT JOIN material_stock ms ON ms.matnr = m.matnr AND ms.werks = :plant " +
            "WHERE (:matnrList IS NULL OR m.matnr IN (:matnrList)) " +
            "AND (:mtartList IS NULL OR m.mtart IN (:mtartList)) " +
            "GROUP BY m.matnr, mt.maktx, m.mtart, m.meins", nativeQuery = true)
    List<Object[]> findByPlantSpecific(@Param("lang") String lang,
                                       @Param("plant") String plant,
                                       @Param("matnrList") List<String> matnrList,
                                       @Param("mtartList") List<String> mtartList);

    /**
     * byPlant=false, plant=null: Aggregated across all plants, grouped by MATNR only.
     * Maps to ZMAT_REPORT.PROG lines 114-129.
     */
    @Query(value = "SELECT m.matnr, mt.maktx, m.mtart, m.meins, COALESCE(SUM(ms.labst), 0) AS labst " +
            "FROM material m " +
            "LEFT JOIN material_text mt ON mt.matnr = m.matnr AND mt.spras = :lang " +
            "LEFT JOIN material_stock ms ON ms.matnr = m.matnr " +
            "WHERE (:matnrList IS NULL OR m.matnr IN (:matnrList)) " +
            "AND (:mtartList IS NULL OR m.mtart IN (:mtartList)) " +
            "GROUP BY m.matnr, mt.maktx, m.mtart, m.meins", nativeQuery = true)
    List<Object[]> findAggregatedAllPlants(@Param("lang") String lang,
                                            @Param("matnrList") List<String> matnrList,
                                            @Param("mtartList") List<String> mtartList);

    /**
     * byPlant=false, plant specified: Aggregated within a specific plant.
     * Maps to ZMAT_REPORT.PROG lines 130-145.
     */
    @Query(value = "SELECT m.matnr, mt.maktx, m.mtart, m.meins, COALESCE(SUM(ms.labst), 0) AS labst " +
            "FROM material m " +
            "LEFT JOIN material_text mt ON mt.matnr = m.matnr AND mt.spras = :lang " +
            "LEFT JOIN material_stock ms ON ms.matnr = m.matnr AND ms.werks = :plant " +
            "WHERE (:matnrList IS NULL OR m.matnr IN (:matnrList)) " +
            "AND (:mtartList IS NULL OR m.mtart IN (:mtartList)) " +
            "GROUP BY m.matnr, mt.maktx, m.mtart, m.meins", nativeQuery = true)
    List<Object[]> findAggregatedSpecificPlant(@Param("lang") String lang,
                                                @Param("plant") String plant,
                                                @Param("matnrList") List<String> matnrList,
                                                @Param("mtartList") List<String> mtartList);
}
