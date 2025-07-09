package net.javaguides.springboot.web.service;

import net.javaguides.springboot.web.model.WikimediaData;
import net.javaguides.springboot.web.repository.WikimediaDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WikimediaDataService {

    private final WikimediaDataRepository repository;

    public WikimediaDataService(WikimediaDataRepository repository) {
        this.repository = repository;
    }

    public List<WikimediaData> findAll() {
        return repository.findAll();
    }

    public Optional<WikimediaData> findById(Long id) {
        return repository.findById(id);
    }

    public WikimediaData save(WikimediaData data) {
        return repository.save(data);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public WikimediaData update(Long id, WikimediaData updatedData) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setWikiEventData(updatedData.getWikiEventData());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Not found: " + id));
    }
}
