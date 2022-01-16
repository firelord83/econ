package org.econ.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.econ.domain.Cantiere;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Cantiere} entity.
 */
public interface CantiereSearchRepository extends ElasticsearchRepository<Cantiere, Long>, CantiereSearchRepositoryInternal {}

interface CantiereSearchRepositoryInternal {
    Stream<Cantiere> search(String query);
}

class CantiereSearchRepositoryInternalImpl implements CantiereSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    CantiereSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Cantiere> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Cantiere.class).map(SearchHit::getContent).stream();
    }
}
