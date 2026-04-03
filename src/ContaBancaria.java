public abstract class ContaBancaria implements TaxaOperacional {     
    private double saldo;
    private Cliente titular;

    public ContaBancaria(Cliente titular, double saldoInicial) {                // Construtor da classe ContaBancaria, que recebe um objeto Cliente como titular e um valor inicial para o saldo da conta, realizando validações para garantir que o titular não seja nulo e que o saldo inicial não seja negativo, lançando exceções apropriadas em caso de dados inválidos
        if (titular == null) {
            throw new IllegalArgumentException("Titular não pode ser nulo.");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        }
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public Cliente getTitular() {    // Método para obter o titular da conta, retornando o objeto Cliente associado à conta bancária
        return titular;
    }

    public double getSaldo() {    // Método para obter o saldo atual da conta, retornando o valor do saldo disponível na conta bancária
        return saldo;
    }

    public void sacar(double valor) {       // Método para realizar um saque na conta, verificando se o valor do saque é positivo e se o saldo é suficiente para cobrir o valor solicitado, lançando exceções em caso de valores inválidos ou saldo insuficiente, e atualizando o saldo da conta após um saque bem-sucedido
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser maior que 0."); 
        }
        if (valor > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente para saque.");
        }
        saldo -= valor;
        System.out.println("Saque de R$ " + valor + " realizado com sucesso.");
    }

    public void depositar(double valor) {       // Método para realizar um depósito na conta, verificando se o valor do depósito é positivo, lançando uma exceção em caso de valor inválido, e atualizando o saldo da conta após um depósito bem-sucedido
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de depósito deve ser maior que 0.");
        }
        saldo += valor;
        System.out.println("Depósito de R$ " + valor + " realizado com sucesso.");
    }

    public void depositar(double valor, ContaBancaria contaOrigem) {         // Método para realizar uma transferência entre contas, verificando se a conta de origem é válida e se o valor da transferência é positivo, lançando exceções em caso de dados inválidos, e atualizando os saldos das contas envolvidas após uma transferência bem-sucedida, além de informar o usuário sobre a transferência realizada
        if (contaOrigem == null) {
            throw new IllegalArgumentException("Conta de origem inválida.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser maior que 0.");
        }

        contaOrigem.sacar(valor);       // Realiza o saque na conta de origem, verificando se o valor é válido e se o saldo é suficiente para cobrir a transferência, lançando exceções em caso de dados inválidos ou saldo insuficiente
        this.saldo += valor;
        System.out.println("Transferência de R$ " + valor + " recebida de " + contaOrigem.getTitular().getNome() + ".");
    }

    public void consultarSaldo() {       // Método para consultar o saldo da conta, exibindo o nome do titular e o saldo atual formatado em reais, permitindo que o usuário tenha uma visão clara do saldo disponível na conta bancária
        System.out.println("Titular: " + titular.getNome() + " - Saldo atual: R$ " + String.format("%.2f", saldo));
    }

    @Override
    public String toString() {       // Método toString para representar a conta bancária como uma string, exibindo o nome do titular, o CPF formatado e o saldo atual da conta, facilitando a visualização das informações da conta de forma clara e organizada
        return "ContaBancaria{" +
                "titular=" + titular.getNome() +
                ", cpf=" + titular.getCpfFormatado() +
                ", saldo=R$ " + String.format("%.2f", saldo) +
                '}';
    }

    public abstract void calcularJuros();    // Método abstrato para calcular juros, que deve ser implementado pelas subclasses de ContaBancaria, permitindo que cada tipo de conta tenha sua própria lógica de cálculo de juros, caso aplicável

    @Override
    public abstract void calcularTaxa();       // Método abstrato para calcular a taxa operacional, que deve ser implementado pelas subclasses de ContaBancaria, permitindo que cada tipo de conta tenha sua própria lógica de cálculo de taxa operacional, caso aplicável
}

