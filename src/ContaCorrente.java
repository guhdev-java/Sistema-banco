public class ContaCorrente extends ContaBancaria {

    private static final double TAXA_MANTENCAO = 10.0;         // Valor fixo da taxa operacional de manutenção para contas correntes

    public ContaCorrente(Cliente titular, double saldoInicial) {
        super(titular, saldoInicial);
    }

    @Override
    public void calcularJuros() {                        // Método para calcular juros, que no caso da conta corrente não há aplicação de juros, apenas uma mensagem informativa é exibida
        System.out.println("Conta Corrente não tem juros.");
    }

    @Override
    public void calcularTaxa() {            // Método para calcular e aplicar a taxa operacional de manutenção, verificando se o saldo é suficiente para cobrir a taxa antes de aplicá-la, e informando o usuário sobre a aplicação da taxa ou a insuficiência de saldo
        if (getSaldo() >= TAXA_MANTENCAO) { 
            super.sacar(TAXA_MANTENCAO);
            System.out.println("Taxa operacional de R$ " + TAXA_MANTENCAO + " aplicada.");
        } else {
            System.out.println("Saldo insuficiente para aplicar taxa de manutenção.");
        }
    }
}
