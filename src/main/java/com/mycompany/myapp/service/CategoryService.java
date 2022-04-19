package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Category;
import com.mycompany.myapp.repository.CategoryRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Category}.
 */
@Service
public class CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Save a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    public Category save(Category category) {
        log.debug("Request to save Category : {}", category);
        return categoryRepository.save(category);
    }

    /**
     * Update a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    public Category update(Category category) {
        log.debug("Request to save Category : {}", category);
        return categoryRepository.save(category);
    }

    /**
     * Partially update a category.
     *
     * @param category the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Category> partialUpdate(Category category) {
        log.debug("Request to partially update Category : {}", category);

        return categoryRepository
            .findById(category.getId())
            .map(existingCategory -> {
                if (category.getDescription() != null) {
                    existingCategory.setDescription(category.getDescription());
                }
                if (category.getSortOrder() != null) {
                    existingCategory.setSortOrder(category.getSortOrder());
                }
                if (category.getDateAdded() != null) {
                    existingCategory.setDateAdded(category.getDateAdded());
                }
                if (category.getDateModified() != null) {
                    existingCategory.setDateModified(category.getDateModified());
                }
                if (category.getStatus() != null) {
                    existingCategory.setStatus(category.getStatus());
                }

                return existingCategory;
            })
            .map(categoryRepository::save);
    }

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Category> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll(pageable);
    }

    /**
     * Get one category by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Category> findOne(String id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id);
    }

    /**
     * Delete the category by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.deleteById(id);
    }

    /**
     * Search for the category corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Category> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Categories for query {}", query);
        return categoryRepository.search(query, pageable);
    }
}
