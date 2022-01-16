package org.econ.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link FattureAttivoSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class FattureAttivoSearchRepositoryMockConfiguration {

    @MockBean
    private FattureAttivoSearchRepository mockFattureAttivoSearchRepository;
}
