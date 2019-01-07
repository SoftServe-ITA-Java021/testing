package com.kh021j.travelwithpleasurehub.propertyrent.repository;

import com.kh021j.travelwithpleasurehub.propertyrent.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {

    Optional<List<Property>> findByPriceLessThanEqual(Integer price);

    Optional<List<Property>> findByLocality(String locality);

    Optional<List<Property>> findByAddress(String address);

    Optional<List<Property>> findByLocalityAndAddress(String locality, String address);

    @Query(value = "SELECT distinct property.id, title, description, locality, address," +
            " user_id, property_type_id, price" +
            " FROM property" +
            " LEFT JOIN property_availability ON property.id = property_availability.property_id" +
            " WHERE property_availability.id IS NULL" +
            " or property.id not in(select property_id from property_availability where" +
            " booked_since  between ?1 and ?2 or booked_until between ?1 and ?2 or" +
            " (booked_since < ?1 and  booked_until > ?2))",
            nativeQuery = true)
    Optional<List<Property>> findByAvailabilityInPeriod(LocalDate start, LocalDate end);

    @Query(value = "SELECT distinct property.id, title, description, locality, address," +
            " user_id, property_type_id, price" +
            " FROM property" +
            " LEFT JOIN property_availability ON property.id = property_availability.property_id" +
            " WHERE property_availability.id IS NULL and locality = ?3" +
            " or property.id not in(select property_id from property_availability where" +
            " booked_since  between ?1 and ?2 or booked_until between ?1 and ?2 or" +
            " (booked_since < ?1 and  booked_until > ?2)) and locality = ?3",
            nativeQuery = true)
    Optional<List<Property>> findByAvailabilityInPeriodAndLocality(LocalDate start, LocalDate end, String locality);

    @Query(value = "SELECT distinct property.id, title, description, locality, address," +
            " user_id, property_type_id, price" +
            " FROM property" +
            " LEFT JOIN property_availability ON property.id = property_availability.property_id" +
            " WHERE property_availability.id IS NULL" +
            " or property.id not in(select property_id from property_availability where" +
            " booked_since  between ?1 and ?2 or booked_until between ?1 and ?2 or" +
            " (booked_since < ?1 and  booked_until > ?2)) order by price ?3",
            nativeQuery = true)
    Optional<List<Property>> findByAvailabilityInPeriodAndSort(LocalDate start, LocalDate end, String order);

    Optional<List<Property>> findAllByOrderByPriceAsc();

    Optional<List<Property>> findAllByOrderByPriceDesc();

}