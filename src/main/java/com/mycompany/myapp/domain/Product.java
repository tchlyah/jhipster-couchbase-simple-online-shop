package com.mycompany.myapp.domain;

import static com.mycompany.myapp.config.Constants.ID_DELIMITER;
import static com.mycompany.myapp.domain.Product.TYPE_NAME;
import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.repository.Collection;

/**
 * A Product.
 */
@Document
@TypeAlias(TYPE_NAME)
@Collection(TYPE_NAME)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_NAME = "product";

    @Id
    @GeneratedValue(strategy = UNIQUE, delimiter = ID_DELIMITER)
    private String id;

    @NotNull
    @Field
    private String title;

    @Field
    private String keywords;

    @Field
    private String description;

    @Field
    private Integer rating;

    @Field
    private LocalDate dateAdded;

    @Field
    private LocalDate dateModified;

    @JsonIgnoreProperties(value = { "parent", "products" }, allowSetters = true)
    private Set<Category> categories = new HashSet<>();

    @JsonIgnore
    @Field("categories")
    private Set<String> categoryIds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Product id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Product title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public Product keywords(String keywords) {
        this.setKeywords(keywords);
        return this;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Product rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getDateAdded() {
        return this.dateAdded;
    }

    public Product dateAdded(LocalDate dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDate getDateModified() {
        return this.dateModified;
    }

    public Product dateModified(LocalDate dateModified) {
        this.setDateModified(dateModified);
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public Set<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
        this.categoryIds = categories.stream().map(Category::getId).collect(Collectors.toSet());
    }

    public Product categories(Set<Category> categories) {
        this.setCategories(categories);
        return this;
    }

    public Product addCategory(Category category) {
        this.categories.add(category);
        this.categoryIds.add(category.getId());
        category.getProducts().add(this);
        return this;
    }

    public Product removeCategory(Category category) {
        this.categories.remove(category);
        this.categoryIds.remove(category.getId());
        category.getProducts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", keywords='" + getKeywords() + "'" +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            ", dateAdded='" + getDateAdded() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            "}";
    }
}
