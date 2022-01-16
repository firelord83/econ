package org.econ.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.econ.domain.Cliente;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Cliente} entity.
 */
public interface ClienteSearchRepository extends ElasticsearchRepository<Cliente, Long>, ClienteSearchRepositoryInternal {}

interface ClienteSearchRepositoryInternal {
    Stream<Cliente> search(String query);
}

class ClienteSearchRepositoryInternalImpl implements ClienteSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ClienteSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Cliente> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Cliente.class).map(SearchHit::getContent).stream();
    }
}
