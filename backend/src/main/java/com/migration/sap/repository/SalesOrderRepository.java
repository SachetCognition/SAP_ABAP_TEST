package com.migration.sap.repository;

import com.migration.sap.entity.SalesOrderItem;
import com.migration.sap.entity.SalesOrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for sales order queries.
 * Matches ZFM_GET_MAT_SO_DETAILS..FUGR.txt lines 47-68.
 */
@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrderItem, SalesOrderItemId> {

    /**
     * All orders for a material (no vbeln filter).
     * Maps to ZFM_GET_MAT_SO_DETAILS lines 47-55.
     */
    @Query(value = "SELECT a.vbeln, a.posnr, b.auart, a.matnr, " +
            "c.maktx, d.meins, a.kwmeng, a.vrkme, a.pstyv, " +
            "b.vkorg, b.vtweg, b.spart, b.erdat, b.ernam " +
            "FROM sales_order_item a " +
            "INNER JOIN sales_order_header b ON b.vbeln = a.vbeln " +
            "LEFT JOIN material_text c ON c.matnr = a.matnr AND c.spras = :lang " +
            "LEFT JOIN material d ON d.matnr = a.matnr " +
            "WHERE a.matnr = :matnr", nativeQuery = true)
    List<Object[]> findByMaterial(@Param("matnr") String matnr, @Param("lang") String lang);

    /**
     * Orders filtered by both matnr and vbeln.
     * Maps to ZFM_GET_MAT_SO_DETAILS lines 57-67.
     */
    @Query(value = "SELECT a.vbeln, a.posnr, b.auart, a.matnr, " +
            "c.maktx, d.meins, a.kwmeng, a.vrkme, a.pstyv, " +
            "b.vkorg, b.vtweg, b.spart, b.erdat, b.ernam " +
            "FROM sales_order_item a " +
            "INNER JOIN sales_order_header b ON b.vbeln = a.vbeln " +
            "LEFT JOIN material_text c ON c.matnr = a.matnr AND c.spras = :lang " +
            "LEFT JOIN material d ON d.matnr = a.matnr " +
            "WHERE a.matnr = :matnr AND a.vbeln = :vbeln", nativeQuery = true)
    List<Object[]> findByMaterialAndOrder(@Param("matnr") String matnr,
                                           @Param("vbeln") String vbeln,
                                           @Param("lang") String lang);
}
