package com.incubyte.backend.repository;

import com.incubyte.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query(value = "SELECT * FROM vehicles WHERE " +
           "(CAST(:query AS TEXT) IS NULL OR LOWER(make) LIKE LOWER('%' || CAST(:query AS TEXT) || '%') OR " +
           " LOWER(model) LIKE LOWER('%' || CAST(:query AS TEXT) || '%') OR " +
           " LOWER(category) LIKE LOWER('%' || CAST(:query AS TEXT) || '%')) AND " +
           "(CAST(:make AS TEXT) IS NULL OR LOWER(make) LIKE LOWER('%' || CAST(:make AS TEXT) || '%')) AND " +
           "(CAST(:model AS TEXT) IS NULL OR LOWER(model) LIKE LOWER('%' || CAST(:model AS TEXT) || '%')) AND " +
           "(CAST(:category AS TEXT) IS NULL OR LOWER(category) LIKE LOWER('%' || CAST(:category AS TEXT) || '%')) AND " +
           "(CAST(:minPrice AS NUMERIC) IS NULL OR price >= CAST(:minPrice AS NUMERIC)) AND " +
           "(CAST(:maxPrice AS NUMERIC) IS NULL OR price <= CAST(:maxPrice AS NUMERIC))",
           nativeQuery = true)
    List<Vehicle> searchVehicles(
        @Param("query") String query,
        @Param("make") String make,
        @Param("model") String model,
        @Param("category") String category,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );
}
