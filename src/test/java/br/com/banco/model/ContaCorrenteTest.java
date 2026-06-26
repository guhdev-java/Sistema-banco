package br.com.banco.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

class ContaCorrenteTest {

    private Cliente cliente;
    private ContaCorrente conta;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Maria", "52998224725", "1234");
        conta = new ContaCorrente(cliente, new BigDecimal("1000.00"), "0001-1", "0001");
    }

    @Test
    void deveSacarComTaxa() {
        conta.sacar(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("898.00"), conta.getSaldo());
    }

    @Test
    void deveRejeitarSaqueSeSaldoForInsuficienteIncluindoTaxa() {
        assertThrows(IllegalArgumentException.class, () ->
            conta.sacar(new BigDecimal("999.00")));
    }

    @Test
    void deveAplicarTaxaManutencaoUmaVez() {
        conta.calcularTaxa();
        assertEquals(new BigDecimal("990.00"), conta.getSaldo());
        conta.calcularTaxa();
        assertEquals(new BigDecimal("990.00"), conta.getSaldo());
    }

    @Test
    void deveTransferirComTaxa() {
        ContaPoupanca poupanca = new ContaPoupanca(cliente, new BigDecimal("500.00"), new BigDecimal("0.05"), "0001-2", "0001");
        conta.transferir(new BigDecimal("200.00"), poupanca);
        assertEquals(new BigDecimal("798.00"), conta.getSaldo());
        assertEquals(new BigDecimal("700.00"), poupanca.getSaldo());
    }

    @Test
    void deveRejeitarTransferenciaSeSaldoInsuficienteIncluindoTaxa() {
        ContaPoupanca poupanca = new ContaPoupanca(cliente, BigDecimal.ZERO, new BigDecimal("0.05"), "0001-2", "0001");
        BigDecimal saldoOriginal = conta.getSaldo();
        assertThrows(IllegalArgumentException.class, () ->
            conta.transferir(new BigDecimal("999.00"), poupanca));
        assertEquals(saldoOriginal, conta.getSaldo());
    }
}
