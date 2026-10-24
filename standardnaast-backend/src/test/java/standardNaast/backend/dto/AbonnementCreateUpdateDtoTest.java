package standardNaast.backend.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class AbonnementCreateUpdateDtoTest {

    private final JsonMapper mapper = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"seasonId\":\"2026-2027\",\"personId\":42,\"bloc\":\"T1\",\"rang\":\"A\",\"place\":\"12\"}",
            "{\"seasonId\":\"2026-2027\",\"personId\":42,\"reduction\":null,\"paye\":null}"
    })
    void defaultsOptionalPaymentFieldsWithStrictJsonDeserialization(String json) {
        var dto = mapper.readValue(json, AbonnementCreateUpdateDto.class);

        assertThat(dto.reduction()).isZero();
        assertThat(dto.paye()).isFalse();
        assertThat(dto.personId()).isEqualTo(42L);
        assertThat(dto.seasonId()).isEqualTo("2026-2027");
    }

    @Test
    void preservesExplicitPaymentFields() {
        var dto = mapper.readValue("""
                {"seasonId":"2026-2027","personId":42,"reduction":25,"paye":true,"acompte":50}
                """, AbonnementCreateUpdateDto.class);

        assertThat(dto.reduction()).isEqualTo(25L);
        assertThat(dto.paye()).isTrue();
        assertThat(dto.acompte()).isEqualTo(50L);
    }
}
