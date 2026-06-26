package br.com.banco.model;

import br.com.banco.util.FormatadorMoeda;
import java.math.BigDecimal;
import java.util.Objects;

public abstract class ContaBancaria implements TaxaOperacional {
    private transient Cliente titular;
    private String titularId;
    private String numeroConta;
    private String agencia;
    protected BigDecimal saldo;

    protected ContaBancaria() {}

    protected ContaBancaria(Cliente titular, BigDecimal saldoInicial, String numeroConta, String agencia) {
        Objects.requireNonNull(titular, "Titular não pode ser nulo.");
        if (saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        }
        this.titular = titular;
        this.titularId = titular.getId();
        this.saldo = saldoInicial;
        this.numeroConta = numeroConta;
        this.agencia = agencia;
    }

    public Cliente getTitular() {
        return titular;
    }

    public void setTitular(Cliente titular) {
        this.titular = titular;
    }

    public String getTitularId() {
        return titularId;
    }

    void setTitularId(String titularId) {
        this.titularId = titularId;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getAgencia() {
        return agencia;
    }

    void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void sacar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser maior que 0.");
        }
        if (valor.compareTo(saldo) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente para saque.");
        }
        saldo = saldo.subtract(valor);
    }

    public void depositar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de depósito deve ser maior que 0.");
        }
        saldo = saldo.add(valor);
    }

    public void transferir(BigDecimal valor, ContaBancaria destino) {
        Objects.requireNonNull(destino, "Conta de destino inválida.");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser maior que 0.");
        }
        if (this.equals(destino)) {
            throw new IllegalArgumentException("Conta de origem e destino devem ser diferentes.");
        }

        BigDecimal saldoAnterior = this.saldo;
        try {
            this.sacar(valor);
            destino.depositar(valor);
        } catch (RuntimeException e) {
            this.saldo = saldoAnterior;
            throw e;
        }
    }

    @Override
    public String toString() {
        return "Conta{" +
                "agencia=" + agencia +
                ", conta=" + numeroConta +
                ", saldo=" + FormatadorMoeda.formatar(saldo) +
                '}';
    }

    public abstract void calcularJuros();
}
