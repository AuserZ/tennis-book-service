package com.booking.tennisbook.controller;

import com.booking.tennisbook.model.Coach;
import com.booking.tennisbook.service.CoachService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {

    private final CoachService coachService;

    public CoachController(CoachService coachService) {
        this.coachService = coachService;
    }

    @PostMapping
    public ResponseEntity<Coach> createCoach(@RequestBody Coach coach) {
        Coach createdCoach = coachService.createCoach(coach);
        return ResponseEntity.ok(createdCoach);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coach> updateCoach(@PathVariable Long id, @RequestBody Coach coach) {
        Coach updatedCoach = coachService.updateCoach(id, coach);
        return ResponseEntity.ok(updatedCoach);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coach> getCoach(@PathVariable Long id) {
        Coach coach = coachService.getCoach(id);
        return ResponseEntity.ok(coach);
    }

    @GetMapping
    public ResponseEntity<List<Coach>> getAllCoaches() {
        List<Coach> coaches = coachService.getAllCoaches();
        return ResponseEntity.ok(coaches);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return ResponseEntity.noContent().build();
    }
} 