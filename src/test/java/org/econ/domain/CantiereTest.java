package org.econ.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.econ.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CantiereTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cantiere.class);
        Cantiere cantiere1 = new Cantiere();
        cantiere1.setId(1L);
        Cantiere cantiere2 = new Cantiere();
        cantiere2.setId(cantiere1.getId());
        assertThat(cantiere1).isEqualTo(cantiere2);
        cantiere2.setId(2L);
        assertThat(cantiere1).isNotEqualTo(cantiere2);
        cantiere1.setId(null);
        assertThat(cantiere1).isNotEqualTo(cantiere2);
    }
}
