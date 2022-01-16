package org.econ.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.econ.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FatturePassivoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FatturePassivo.class);
        FatturePassivo fatturePassivo1 = new FatturePassivo();
        fatturePassivo1.setId(1L);
        FatturePassivo fatturePassivo2 = new FatturePassivo();
        fatturePassivo2.setId(fatturePassivo1.getId());
        assertThat(fatturePassivo1).isEqualTo(fatturePassivo2);
        fatturePassivo2.setId(2L);
        assertThat(fatturePassivo1).isNotEqualTo(fatturePassivo2);
        fatturePassivo1.setId(null);
        assertThat(fatturePassivo1).isNotEqualTo(fatturePassivo2);
    }
}
