package com.mycompany.myapp.domain;

import static com.mycompany.myapp.config.Constants.ID_DELIMITER;
import static com.mycompany.myapp.domain.Category.TYPE_NAME;
import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.CategoryStatus;
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
 * A Category.
 */
@Document
@TypeAlias(TYPE_NAME)
@Collection(TYPE_NAME)
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_NAME = "category";

    @Id
    @GeneratedValue(strategy = UNIQUE, delimiter = ID_DELIMITER)
    private String id;

    @NotNull
    @Field
    private String description;

    @Field
    private Integer sortOrder;

    @Field
    private LocalDate dateAdded;

    @Field
    private LocalDate dateModified;

    @Field
    private CategoryStatus status;

    @JsonIgnoreProperties(value = { "parent", "products" }, allowSetters = true)
    private Category parent;

    @JsonIgnoreProperties(value = { "categories" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    @JsonIgnore
    @Field("parent")
    private String parentId;

    @JsonIgnore
    @Field("products")
    private Set<String> productIds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Category id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Category description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public Category sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDate getDateAdded() {
        return this.dateAdded;
    }

    public Category dateAdded(LocalDate dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDate getDateModified() {
        return this.dateModified;
    }

    public Category dateModified(LocalDate dateModified) {
        this.setDateModified(dateModified);
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public CategoryStatus getStatus() {
        return this.status;
    }

    public Category status(CategoryStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CategoryStatus status) {
        this.status = status;
    }

    public Category getParent() {
        return this.parent;
    }

    public void setParent(Category category) {
        this.parent = category;
        this.parentId = category != null ? category.getId() : null;
    }

    public Category parent(Category category) {
        this.setParent(category);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.removeCategory(this));
        }
        if (products != null) {
            products.forEach(i -> i.addCategory(this));
        }
        this.products = products;
        this.productIds = products.stream().map(Product::getId).collect(Collectors.toSet());
    }

    public Category products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public Category addProduct(Product product) {
        this.products.add(product);
        this.productIds.add(product.getId());
        product.getCategories().add(this);
        return this;
    }

    public Category removeProduct(Product product) {
        this.products.remove(product);
        this.productIds.remove(product.getId());
        product.getCategories().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", dateAdded='" + getDateAdded() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
