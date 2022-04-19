package com.mycompany.myapp.repository;

import static com.mycompany.myapp.domain.Product.TYPE_NAME;
import static com.mycompany.myapp.repository.JHipsterCouchbaseRepository.pageableStatement;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.mycompany.myapp.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JHipsterCouchbaseRepository<Product, String>, CouchbaseSearchRepository<Product, String> {
    String SELECT =
        "SELECT meta(b).id as __id, meta(b).cas as __cas, b.*" +
        ", ARRAY OBJECT_ADD(item, 'id', meta(item).id) FOR item IN `categories` END AS `categories`" +
        " FROM #{#n1ql.bucket} b";

    String JOIN = " LEFT NEST `category` `categories` ON KEYS b.`categories`";

    String WHERE = " WHERE b.type = '" + TYPE_NAME + "'";

    default Page<Product> findAll(Pageable pageable) {
        return new PageImpl<>(findAll(pageableStatement(pageable, "b")), pageable, count());
    }

    @Query(SELECT + JOIN + WHERE + " #{[0]}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Product> findAll(String pageableStatement);

    @Query(SELECT + JOIN + WHERE)
    List<Product> findAll();

    @Query(SELECT + " USE KEYS $1" + JOIN)
    Optional<Product> findById(String id);

    @Query(SELECT + JOIN + WHERE + " AND " + SEARCH_CONDITION)
    List<Product> search(String queryString);

    default Page<Product> search(String queryString, Pageable pageable) {
        return new PageImpl<>(search(queryString, pageableStatement(pageable, "b")));
    }

    @ScanConsistency(query = QueryScanConsistency.NOT_BOUNDED)
    @Query(SELECT + JOIN + WHERE + " AND " + SEARCH_CONDITION + " #{[1]}")
    List<Product> search(String queryString, String pageableStatement);
}
