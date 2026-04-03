public class ContaPoupanca extends ContaBancaria {

    private double taxaJuros;                      // Taxa de juros mensal aplicada à conta poupança

    public ContaPoupanca(Cliente titular, double saldoInicial, double taxaJuros) {
        super(titular, saldoInicial);
        if (taxaJuros < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");         // Validação para garantir que a taxa de juros fornecida seja um valor positivo, lançando uma exceção caso contrário
        }
        this.taxaJuros = taxaJuros;
    }

    public ContaPoupanca(Cliente titular, double saldoInicial) {                // Construtor adicional que permite criar uma conta poupança com uma taxa de juros padrão de 5% caso o usuário não forneça uma taxa personalizada
        this(titular, saldoInicial, 0.05);
    }

    public double getTaxaJuros() {         
        return taxaJuros;
    }

    public void setTaxaJuros(double taxaJuros) {
        if (taxaJuros < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");         // Validação para garantir que a taxa de juros fornecida seja um valor positivo, lançando uma exceção caso contrário
        }
        this.taxaJuros = taxaJuros;
    }

    @Override
    public void calcularJuros() {           // Método para calcular e aplicar os juros à conta poupança, calculando o valor dos juros com base no saldo atual e na taxa de juros, e depositando esse valor na conta
        double ganho = getSaldo() * taxaJuros;
        depositar(ganho);
        System.out.println("Juros de " + (taxaJuros * 100) + "% aplicados. Valor de juros: R$ " + String.format("%.2f", ganho));
    }

    @Override
    public void calcularTaxa() {
        System.out.println("Conta Poupança não possui taxa operacional de manutenção.");
    }
}
