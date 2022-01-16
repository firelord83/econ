package org.econ.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.econ.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FornitoreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Fornitore.class);
        Fornitore fornitore1 = new Fornitore();
        fornitore1.setId(1L);
        Fornitore fornitore2 = new Fornitore();
        fornitore2.setId(fornitore1.getId());
        assertThat(fornitore1).isEqualTo(fornitore2);
        fornitore2.setId(2L);
        assertThat(fornitore1).isNotEqualTo(fornitore2);
        fornitore1.setId(null);
        assertThat(fornitore1).isNotEqualTo(fornitore2);
    }
}
