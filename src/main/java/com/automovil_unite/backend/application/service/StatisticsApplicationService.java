package com.automovil_unite.backend.application.service;

import com.automovil_unite.backend.application.dto.StatisticsDto;
import com.automovil_unite.backend.application.dto.VehicleDto;
import com.automovil_unite.backend.application.mapper.VehicleMapper;
import com.automovil_unite.backend.domain.model.Vehicle;
import com.automovil_unite.backend.domain.service.RentalService;
import com.automovil_unite.backend.domain.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsApplicationService {
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final VehicleMapper vehicleMapper;

    @Transactional(readOnly = true)
    public StatisticsDto getHomeStatistics() {
        // Obtener los vehículos más alquilados
        List<Vehicle> topRentedVehicles = vehicleService.findTopRented(5);
        List<VehicleDto> topRentedVehiclesDto = topRentedVehicles.stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
        
        // Obtener las marcas más populares
        Map<String, Integer> topBrands = new HashMap<>();
        topRentedVehicles.forEach(vehicle -> {
            String brand = vehicle.getBrand();
            topBrands.put(brand, topBrands.getOrDefault(brand, 0) + 1);
        });
        
        return StatisticsDto.builder()
                .topRentedVehicles(topRentedVehiclesDto)
                .topBrands(topBrands)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getVehicleRentalsByBrand() {
        List<Vehicle> vehicles = vehicleService.findAll();
        Map<String, Integer> rentalsByBrand = new HashMap<>();
        
        vehicles.forEach(vehicle -> {
            String brand = vehicle.getBrand();
            int rentCount = vehicle.getRentCount();
            rentalsByBrand.put(brand, rentalsByBrand.getOrDefault(brand, 0) + rentCount);
        });
        
        return rentalsByBrand;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getVehicleRentalsByMonth() {
        // Esta es una implementación simplificada
        // En una implementación real, se obtendría esta información de la base de datos
        // con una consulta específica por mes
        
        Map<String, Integer> rentalsByMonth = new HashMap<>();
        rentalsByMonth.put("Enero", 45);
        rentalsByMonth.put("Febrero", 52);
        rentalsByMonth.put("Marzo", 38);
        rentalsByMonth.put("Abril", 65);
        rentalsByMonth.put("Mayo", 72);
        rentalsByMonth.put("Junio", 58);
        rentalsByMonth.put("Julio", 85);
        rentalsByMonth.put("Agosto", 92);
        rentalsByMonth.put("Septiembre", 78);
        rentalsByMonth.put("Octubre", 63);
        rentalsByMonth.put("Noviembre", 55);
        rentalsByMonth.put("Diciembre", 70);
        
        return rentalsByMonth;
    }

    @Transactional(readOnly = true)
    public Map<String, Double> getUserRatingStatistics() {
        // Esta es una implementación simplificada
        // En una implementación real, se obtendría esta información de la base de datos
        
        Map<String, Double> ratingStatistics = new HashMap<>();
        ratingStatistics.put("averageOwnerRating", 4.2);
        ratingStatistics.put("averageTenantRating", 4.5);
        ratingStatistics.put("averageVehicleRating", 4.3);
        
        return ratingStatistics;
    }
}