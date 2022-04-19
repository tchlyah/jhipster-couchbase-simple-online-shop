package com.mycompany.myapp.repository;

import static com.mycompany.myapp.domain.Address.TYPE_NAME;
import static com.mycompany.myapp.repository.JHipsterCouchbaseRepository.pageableStatement;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.mycompany.myapp.domain.Address;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Address entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends JHipsterCouchbaseRepository<Address, String>, CouchbaseSearchRepository<Address, String> {
    String SELECT =
        "SELECT meta(b).id as __id, meta(b).cas as __cas, b.*" +
        ", OBJECT_ADD(`customer`, 'id', meta(`customer`).id) AS `customer`" +
        " FROM #{#n1ql.bucket} b";

    String JOIN = " LEFT JOIN `customer` `customer` ON KEYS b.`customer`";

    String WHERE = " WHERE b.type = '" + TYPE_NAME + "'";

    default Page<Address> findAll(Pageable pageable) {
        return new PageImpl<>(findAll(pageableStatement(pageable, "b")), pageable, count());
    }

    @Query(SELECT + JOIN + WHERE + " #{[0]}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Address> findAll(String pageableStatement);

    @Query(SELECT + JOIN + WHERE)
    List<Address> findAll();

    @Query(SELECT + " USE KEYS $1" + JOIN)
    Optional<Address> findById(String id);

    @Query(SELECT + JOIN + WHERE + " AND " + SEARCH_CONDITION)
    List<Address> search(String queryString);

    default Page<Address> search(String queryString, Pageable pageable) {
        return new PageImpl<>(search(queryString, pageableStatement(pageable, "b")));
    }

    @ScanConsistency(query = QueryScanConsistency.NOT_BOUNDED)
    @Query(SELECT + JOIN + WHERE + " AND " + SEARCH_CONDITION + " #{[1]}")
    List<Address> search(String queryString, String pageableStatement);
}
