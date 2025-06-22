package com.booking.tennisbook.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sql")                     // POST /sql  with raw INSERT in body
public class DevInsertOnlyController {

    @PersistenceContext
    private EntityManager em;               // JPA EntityManager

    private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runInsert(@RequestBody String sql) {

        sql = sql.trim();
        log.info("Executing raw INSERT: {}", sql.replaceAll("\\s+", " "));

        try {
            int affected = em.createNativeQuery(sql).executeUpdate();

            return ResponseEntity.ok(
                    Map.of("rowsAffected", affected,
                            "message", "INSERT executed"));
        } catch (Exception ex) {
            log.error("SQL execution failed", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}


