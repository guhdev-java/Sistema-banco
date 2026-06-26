package br.com.banco.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

class ContaBancariaTest {

    private Cliente cliente;
    private ContaBancaria conta;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("João", "52998224725", "1234");
        conta = new ContaBancaria(cliente, new BigDecimal("500.00"), "0001-1", "0001") {
            @Override
            public void calcularJuros() {}
            @Override
            public void calcularTaxa() {}
        };
    }

    @Test
    void deveCriarContaComSaldoInicial() {
        assertEquals(new BigDecimal("500.00"), conta.getSaldo());
        assertEquals("0001-1", conta.getNumeroConta());
        assertEquals("0001", conta.getAgencia());
        assertEquals(cliente.getId(), conta.getTitularId());
    }

    @Test
    void deveRejeitarSaldoInicialNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
            new ContaBancaria(cliente, new BigDecimal("-100.00"), "0001-1", "0001") {
                @Override public void calcularJuros() {}
                @Override public void calcularTaxa() {}
            });
    }

    @Test
    void deveRejeitarTitularNulo() {
        assertThrows(NullPointerException.class, () ->
            new ContaBancaria(null, BigDecimal.ZERO, "0001-1", "0001") {
                @Override public void calcularJuros() {}
                @Override public void calcularTaxa() {}
            });
    }

    @Test
    void deveSacar() {
        conta.sacar(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("400.00"), conta.getSaldo());
    }

    @Test
    void deveRejeitarSaqueValorAcimaDoSaldo() {
        assertThrows(IllegalArgumentException.class, () ->
            conta.sacar(new BigDecimal("600.00")));
        assertEquals(new BigDecimal("500.00"), conta.getSaldo());
    }

    @Test
    void deveRejeitarSaqueValorZero() {
        assertThrows(IllegalArgumentException.class, () ->
            conta.sacar(BigDecimal.ZERO));
    }

    @Test
    void deveRejeitarSaqueValorNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
            conta.sacar(new BigDecimal("-50.00")));
    }

    @Test
    void deveDepositar() {
        conta.depositar(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("600.00"), conta.getSaldo());
    }

    @Test
    void deveRejeitarDepositoValorZero() {
        assertThrows(IllegalArgumentException.class, () ->
            conta.depositar(BigDecimal.ZERO));
    }

    @Test
    void deveTransferir() {
        ContaBancaria destino = new ContaBancaria(cliente, BigDecimal.ZERO, "0001-2", "0001") {
            @Override public void calcularJuros() {}
            @Override public void calcularTaxa() {}
        };
        conta.transferir(new BigDecimal("200.00"), destino);
        assertEquals(new BigDecimal("300.00"), conta.getSaldo());
        assertEquals(new BigDecimal("200.00"), destino.getSaldo());
    }

    @Test
    void deveRejeitarTransferenciaSaldoInsuficiente() {
        ContaBancaria destino = new ContaBancaria(cliente, BigDecimal.ZERO, "0001-2", "0001") {
            @Override public void calcularJuros() {}
            @Override public void calcularTaxa() {}
        };
        BigDecimal saldoOriginal = conta.getSaldo();
        assertThrows(IllegalArgumentException.class, () ->
            conta.transferir(new BigDecimal("600.00"), destino));
        assertEquals(saldoOriginal, conta.getSaldo());
        assertEquals(BigDecimal.ZERO, destino.getSaldo());
    }

    @Test
    void deveRejeitarTransferenciaParaMesmaConta() {
        assertThrows(IllegalArgumentException.class, () ->
            conta.transferir(new BigDecimal("100.00"), conta));
    }

    @Test
    void deveRejeitarTransferenciaDestinoNulo() {
        assertThrows(NullPointerException.class, () ->
            conta.transferir(new BigDecimal("100.00"), null));
    }

    @Test
    void deveRejeitarTransferenciaValorZero() {
        ContaBancaria destino = new ContaBancaria(cliente, BigDecimal.ZERO, "0001-2", "0001") {
            @Override public void calcularJuros() {}
            @Override public void calcularTaxa() {}
        };
        assertThrows(IllegalArgumentException.class, () ->
            conta.transferir(BigDecimal.ZERO, destino));
    }
}
