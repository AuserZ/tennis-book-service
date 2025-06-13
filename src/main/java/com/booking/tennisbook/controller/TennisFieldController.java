package com.booking.tennisbook.controller;

import com.booking.tennisbook.model.TennisField;
import com.booking.tennisbook.repository.TennisFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tennis-fields")
@RequiredArgsConstructor
public class TennisFieldController {

    private final TennisFieldRepository tennisFieldRepository;

    @PostMapping
    public ResponseEntity<TennisField> createTennisField(@RequestBody TennisField tennisField) {
        TennisField savedField = tennisFieldRepository.save(tennisField);
        return ResponseEntity.ok(savedField);
    }

    @GetMapping
    public ResponseEntity<List<TennisField>> getAllTennisFields() {
        List<TennisField> fields = tennisFieldRepository.findAll();
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TennisField> getTennisField(@PathVariable Long id) {
        return tennisFieldRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 