package net.javaguides.springboot.web.controller;

import net.javaguides.springboot.web.model.WikimediaData;
import net.javaguides.springboot.web.service.WikimediaDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wikimedia")
@CrossOrigin(origins = "*")
public class WikimediaDataController {

    private final WikimediaDataService service;

    public WikimediaDataController(WikimediaDataService service) {
        this.service = service;
    }

    @GetMapping
    public List<net.javaguides.springboot.web.model.WikimediaData> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WikimediaData> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WikimediaData> create(@RequestBody WikimediaData data) {
        return ResponseEntity.ok(service.save(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WikimediaData> update(@PathVariable Long id, @RequestBody WikimediaData data) {
        return ResponseEntity.ok(service.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
