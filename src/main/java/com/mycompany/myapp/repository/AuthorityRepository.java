package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Authority;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the {@link Authority} entity.
 */
@Repository
public interface AuthorityRepository extends JHipsterCouchbaseRepository<Authority, String> {}
