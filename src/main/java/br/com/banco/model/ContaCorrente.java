package br.com.banco.model;

import java.math.BigDecimal;

public class ContaCorrente extends ContaBancaria {
    private static final BigDecimal TAXA_MANTENCAO = new BigDecimal("10.00");
    private static final BigDecimal TAXA_SAQUE = new BigDecimal("2.00");
    private boolean taxaAplicada = false;

    public ContaCorrente() {}

    public ContaCorrente(Cliente titular, BigDecimal saldoInicial, String numeroConta, String agencia) {
        super(titular, saldoInicial, numeroConta, agencia);
    }

    @Override
    public void calcularJuros() {}

    @Override
    public void calcularTaxa() {
        if (!taxaAplicada) {
            if (getSaldo().compareTo(TAXA_MANTENCAO) >= 0) {
                super.sacar(TAXA_MANTENCAO);
                taxaAplicada = true;
            }
        }
    }

    @Override
    public void sacar(BigDecimal valor) {
        super.sacar(valor.add(TAXA_SAQUE));
    }
}
