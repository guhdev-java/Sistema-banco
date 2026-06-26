package br.com.banco.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ClienteTest {

    @Test
    void deveCriarClienteValido() {
        Cliente cliente = new Cliente("João", "52998224725", "1234");
        assertEquals("João", cliente.getNome());
        assertEquals("52998224725", cliente.getCpf());
        assertEquals("529.982.247-25", cliente.getCpfFormatado());
        assertNotNull(cliente.getId());
        assertTrue(cliente.verificarSenha("1234"));
        assertFalse(cliente.verificarSenha("senha_errada"));
    }

    @Test
    void deveRejeitarNomeVazio() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente("", "52998224725", "1234"));
    }

    @Test
    void deveRejeitarSenhaVazia() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente("João", "52998224725", ""));
    }

    @Test
    void deveRejeitarCpfInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente("João", "11111111111", "1234"));
    }
}
