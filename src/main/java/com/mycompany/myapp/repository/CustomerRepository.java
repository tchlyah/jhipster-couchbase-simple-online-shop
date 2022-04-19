package com.mycompany.myapp.repository;

import static com.mycompany.myapp.domain.Customer.TYPE_NAME;
import static com.mycompany.myapp.repository.JHipsterCouchbaseRepository.pageableStatement;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.mycompany.myapp.domain.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends JHipsterCouchbaseRepository<Customer, String>, CouchbaseSearchRepository<Customer, String> {
    String SELECT =
        "SELECT meta(b).id as __id, meta(b).cas as __cas, b.*" +
        ", ARRAY OBJECT_ADD(item, 'id', meta(item).id) FOR item IN `addresses` END AS `addresses`" +
        " FROM #{#n1ql.bucket} b";

    String JOIN = " LEFT NEST `address` `addresses` ON KEYS b.`addresses`";

    String WHERE = " WHERE b.type = '" + TYPE_NAME + "'";

    default Page<Customer> findAll(Pageable pageable) {
        return new PageImpl<>(findAll(pageableStatement(pageable, "b")), pageable, count());
    }

    @Query(SELECT + JOIN + WHERE + " #{[0]}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Customer> findAll(String pageableStatement);

    @Query(SELECT + JOIN + WHERE)
    List<Customer> findAll();

    @Query(SELECT + " USE KEYS $1" + JOIN)
    Optional<Customer> findById(String id);

    @Query(SELECT + JOIN + WHERE + " AND " + SEARCH_CONDITION)
    List<Customer> search(String queryString);

    default Page<Customer> search(String queryString, Pageable pageable) {
        return new PageImpl<>(search(queryString, pageableStatement(pageable, "b")));
    }

    @ScanConsistency(query = QueryScanConsistency.NOT_BOUNDED)
    @Query(SELECT + JOIN + WHERE + " AND " + SEARCH_CONDITION + " #{[1]}")
    List<Customer> search(String queryString, String pageableStatement);
}
