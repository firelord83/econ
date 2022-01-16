package org.econ.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link FatturePassivoSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class FatturePassivoSearchRepositoryMockConfiguration {

    @MockBean
    private FatturePassivoSearchRepository mockFatturePassivoSearchRepository;
}
