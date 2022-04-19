package com.mycompany.myapp.repository;

import static com.mycompany.myapp.domain.Category.TYPE_NAME;
import static com.mycompany.myapp.repository.JHipsterCouchbaseRepository.pageableStatement;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.mycompany.myapp.domain.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JHipsterCouchbaseRepository<Category, String>, CouchbaseSearchRepository<Category, String> {
    String SELECT =
        "SELECT meta(b).id as __id, meta(b).cas as __cas, b.*" +
        ", OBJECT_ADD(`parent`, 'id', meta(`parent`).id) AS `parent`" +
        ", ARRAY OBJECT_ADD(item, 'id', meta(item).id) FOR item IN `products` END AS `products`" +
        " FROM #{#n1ql.bucket} b";

    String JOIN = " LEFT JOIN `category` `parent` ON KEYS b.`parent`" + " LEFT NEST `product` `products` ON KEYS b.`products`";

    String WHERE = " WHERE b.type = '" + TYPE_NAME + "'";

    default Page<Category> findAll(Pageable pageable) {
        return new PageImpl<>(findAll(pageableStatement(pageable, "b")), pageable, count());
    }

    @Query(SELECT + JOIN + WHERE + " #{[0]}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Category> findAll(String pageableStatement);

    @Query(SELECT + JOIN + WHERE)
    List<Category> findAll();

    @Query(SELECT + " USE KEYS $1" + JOIN)
    Optional<Category> findById(String id);

    @Query(SELECT + JOIN + WHERE + " AND " + SEARCH_CONDITION)
    List<Category> search(String queryString);

    default Page<Category> search(String queryString, Pageable pageable) {
        return new PageImpl<>(search(queryString, pageableStatement(pageable, "b")));
    }

    @ScanConsistency(query = QueryScanConsistency.NOT_BOUNDED)
    @Query(SELECT + JOIN + WHERE + " AND " + SEARCH_CONDITION + " #{[1]}")
    List<Category> search(String queryString, String pageableStatement);
}
