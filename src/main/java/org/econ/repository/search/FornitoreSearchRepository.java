package org.econ.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.econ.domain.Fornitore;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Fornitore} entity.
 */
public interface FornitoreSearchRepository extends ElasticsearchRepository<Fornitore, Long>, FornitoreSearchRepositoryInternal {}

interface FornitoreSearchRepositoryInternal {
    Stream<Fornitore> search(String query);
}

class FornitoreSearchRepositoryInternalImpl implements FornitoreSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    FornitoreSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Fornitore> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Fornitore.class).map(SearchHit::getContent).stream();
    }
}
