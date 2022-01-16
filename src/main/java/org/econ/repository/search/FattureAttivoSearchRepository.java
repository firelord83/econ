package org.econ.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.econ.domain.FattureAttivo;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link FattureAttivo} entity.
 */
public interface FattureAttivoSearchRepository
    extends ElasticsearchRepository<FattureAttivo, Long>, FattureAttivoSearchRepositoryInternal {}

interface FattureAttivoSearchRepositoryInternal {
    Stream<FattureAttivo> search(String query);
}

class FattureAttivoSearchRepositoryInternalImpl implements FattureAttivoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    FattureAttivoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<FattureAttivo> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, FattureAttivo.class).map(SearchHit::getContent).stream();
    }
}
