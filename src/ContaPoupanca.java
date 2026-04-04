import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ContaPoupanca extends ContaBancaria {

    private BigDecimal taxaJuros;                      

    public ContaPoupanca(Cliente titular, BigDecimal saldoInicial, BigDecimal taxaJuros) {
        super(titular, saldoInicial);
        if (taxaJuros.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");    
        }
        this.taxaJuros = taxaJuros;
    }

    public ContaPoupanca(Cliente titular, BigDecimal saldoInicial) {               
        this(titular, saldoInicial, BigDecimal.valueOf(0.05));
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
        BigDecimal ganho = getSaldo().multiply(taxaJuros);
        depositar(ganho);
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        System.out.println("Juros de " + (taxaJuros.multiply(BigDecimal.valueOf(100)).doubleValue()) + "% aplicados. Valor de juros: " + formatoMoeda.format(ganho));
    }

    @Override
    public void calcularTaxa() {
        System.out.println("Conta Poupança não possui taxa operacional de manutenção.");
    }
}
