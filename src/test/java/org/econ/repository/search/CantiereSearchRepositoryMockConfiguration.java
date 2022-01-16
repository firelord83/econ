package org.econ.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CantiereSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CantiereSearchRepositoryMockConfiguration {

    @MockBean
    private CantiereSearchRepository mockCantiereSearchRepository;
}
