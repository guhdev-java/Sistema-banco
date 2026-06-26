package br.com.banco.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CpfValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-25", "52998224725", "123.456.789-09", "12345678909"})
    void deveValidarCpfsValidos(String cpf) {
        assertDoesNotThrow(() -> CpfValidator.validar(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123", "123456789012345", "11111111111", "00000000000", "abc.def.ghi-jk"})
    void deveRejeitarCpfsInvalidos(String cpf) {
        assertThrows(IllegalArgumentException.class, () -> CpfValidator.validar(cpf));
    }

    @Test
    void deveRejeitarCpfNulo() {
        assertThrows(IllegalArgumentException.class, () -> CpfValidator.validar(null));
    }

    @Test
    void deveFormatarCpf() {
        assertEquals("529.982.247-25", CpfValidator.formatar("52998224725"));
    }

    @Test
    void deveRetornarOriginalSeTamanhoInvalido() {
        assertEquals("123", CpfValidator.formatar("123"));
    }
}
