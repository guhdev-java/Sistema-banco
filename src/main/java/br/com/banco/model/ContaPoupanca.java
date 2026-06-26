package br.com.banco.model;

import java.math.BigDecimal;

public class ContaPoupanca extends ContaBancaria {
    private BigDecimal taxaJuros;

    public ContaPoupanca() {}

    public ContaPoupanca(Cliente titular, BigDecimal saldoInicial, BigDecimal taxaJuros, String numeroConta, String agencia) {
        super(titular, saldoInicial, numeroConta, agencia);
        if (taxaJuros.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");
        }
        this.taxaJuros = taxaJuros;
    }

    public BigDecimal getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(BigDecimal taxaJuros) {
        if (taxaJuros.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");
        }
        this.taxaJuros = taxaJuros;
    }

    @Override
    public void calcularJuros() {
        BigDecimal ganho = getSaldo().multiply(taxaJuros).setScale(2, java.math.RoundingMode.HALF_EVEN);
        if (ganho.compareTo(BigDecimal.ZERO) > 0) {
            depositar(ganho);
        }
    }

    @Override
    public void calcularTaxa() {}
}
