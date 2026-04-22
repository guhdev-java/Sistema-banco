import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public abstract class ContaBancaria implements TaxaOperacional {     
    private static final Locale LOCALE_BR = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
    private BigDecimal saldo;
    private Cliente titular;

    public ContaBancaria(Cliente titular, BigDecimal saldoInicial) {      
        if (titular == null) {
            throw new IllegalArgumentException("Titular não pode ser nulo.");
        }
        if (saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        }
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public Cliente getTitular() {    
        return titular;
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
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(LOCALE_BR);
        System.out.println("Saque de " + formatoMoeda.format(valor) + " realizado com sucesso.");
    }

    public void depositar(BigDecimal valor) {       
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de depósito deve ser maior que 0.");
        }
        saldo = saldo.add(valor);
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(LOCALE_BR);
        System.out.println("Depósito de " + formatoMoeda.format(valor) + " realizado com sucesso.");
    }

    public void depositar(BigDecimal valor, ContaBancaria contaOrigem) {         
        if (contaOrigem == null) {
            throw new IllegalArgumentException("Conta de origem inválida.");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser maior que 0.");
        }

        contaOrigem.sacar(valor);       
        this.saldo = this.saldo.add(valor);
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(LOCALE_BR);
        System.out.println("Transferência de " + formatoMoeda.format(valor) + " recebida de " + contaOrigem.getTitular().getNome() + ".");
    }

    public void consultarSaldo() {       
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(LOCALE_BR);
        System.out.println("Titular: " + titular.getNome() + " - Saldo atual: " + formatoMoeda.format(saldo));
    }

    @Override
    public String toString() {       
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(LOCALE_BR);
        return "ContaBancaria{" +
                "titular=" + titular.getNome() +
                ", cpf=" + titular.getCpfFormatado() +
                ", saldo=" + formatoMoeda.format(saldo) +
                '}';
    }

    public abstract void calcularJuros();    

    @Override
    public abstract void calcularTaxa();

    }       

