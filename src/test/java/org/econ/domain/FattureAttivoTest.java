package org.econ.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.econ.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FattureAttivoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FattureAttivo.class);
        FattureAttivo fattureAttivo1 = new FattureAttivo();
        fattureAttivo1.setId(1L);
        FattureAttivo fattureAttivo2 = new FattureAttivo();
        fattureAttivo2.setId(fattureAttivo1.getId());
        assertThat(fattureAttivo1).isEqualTo(fattureAttivo2);
        fattureAttivo2.setId(2L);
        assertThat(fattureAttivo1).isNotEqualTo(fattureAttivo2);
        fattureAttivo1.setId(null);
        assertThat(fattureAttivo1).isNotEqualTo(fattureAttivo2);
    }
}
