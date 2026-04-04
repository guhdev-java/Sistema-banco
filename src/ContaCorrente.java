import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ContaCorrente extends ContaBancaria {

    private static final BigDecimal TAXA_MANTENCAO = BigDecimal.valueOf(10.0);         
    private boolean taxaAplicada = false; // Flag para aplicar a taxa apenas uma vez por sessão

    public ContaCorrente(Cliente titular, BigDecimal saldoInicial) {
        super(titular, saldoInicial);
    }

    @Override
    public void calcularJuros() {                        
        System.out.println("Conta Corrente não tem juros.");
    }

    @Override
    public void calcularTaxa() {      
        if (!taxaAplicada) {
            if (getSaldo().compareTo(TAXA_MANTENCAO) >= 0) { 
                super.sacar(TAXA_MANTENCAO);
                NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                System.out.println("Taxa operacional de " + formatoMoeda.format(TAXA_MANTENCAO) + " aplicada.");
                taxaAplicada = true;
            } else {
                System.out.println("Saldo insuficiente para aplicar taxa de manutenção.");
            }
        } else {
            System.out.println("Taxa operacional já aplicada nesta sessão.");
        }
    }
}
