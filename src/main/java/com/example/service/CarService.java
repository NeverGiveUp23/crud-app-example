package com.example.service;

import com.example.dto.CarDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CarService {

    void save(CarDTO carDTO);

    List<CarDTO> findAll();

    CarDTO findById(Long id);

    void delete(Long id);
}
