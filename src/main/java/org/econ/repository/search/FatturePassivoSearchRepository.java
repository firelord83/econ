package org.econ.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.econ.domain.FatturePassivo;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link FatturePassivo} entity.
 */
public interface FatturePassivoSearchRepository
    extends ElasticsearchRepository<FatturePassivo, Long>, FatturePassivoSearchRepositoryInternal {}

interface FatturePassivoSearchRepositoryInternal {
    Stream<FatturePassivo> search(String query);
}

class FatturePassivoSearchRepositoryInternalImpl implements FatturePassivoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    FatturePassivoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<FatturePassivo> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, FatturePassivo.class).map(SearchHit::getContent).stream();
    }
}
