package br.com.banco.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

class ContaPoupancaTest {

    private Cliente cliente;
    private ContaPoupanca conta;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("João", "52998224725", "1234");
        conta = new ContaPoupanca(cliente, new BigDecimal("1000.00"), new BigDecimal("0.05"), "0001-1", "0001");
    }

    @Test
    void deveCriarContaPoupancaComTaxaDeJuros() {
        assertEquals(new BigDecimal("0.05"), conta.getTaxaJuros());
        assertEquals(new BigDecimal("1000.00"), conta.getSaldo());
    }

    @Test
    void deveAplicarJuros() {
        conta.calcularJuros();
        assertEquals(new BigDecimal("1050.00"), conta.getSaldo());
    }

    @Test
    void deveRejeitarTaxaDeJurosNegativa() {
        assertThrows(IllegalArgumentException.class, () ->
            new ContaPoupanca(cliente, BigDecimal.ZERO, new BigDecimal("-0.01"), "0001-1", "0001"));
    }

    @Test
    void devePermitirTaxaDeJurosZero() {
        ContaPoupanca cp = new ContaPoupanca(cliente, BigDecimal.ZERO, BigDecimal.ZERO, "0001-1", "0001");
        cp.calcularJuros();
        assertEquals(BigDecimal.ZERO, cp.getSaldo());
    }

    @Test
    void naoDeveCobrarTaxaOperacional() {
        assertDoesNotThrow(() -> conta.calcularTaxa());
    }

    @Test
    void deveSacarSemTaxa() {
        conta.sacar(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("900.00"), conta.getSaldo());
    }

    @Test
    void deveTransferirSemTaxa() {
        ContaCorrente corrente = new ContaCorrente(cliente, new BigDecimal("500.00"), "0001-2", "0001");
        conta.transferir(new BigDecimal("300.00"), corrente);
        assertEquals(new BigDecimal("700.00"), conta.getSaldo());
        assertEquals(new BigDecimal("800.00"), corrente.getSaldo());
    }
}
