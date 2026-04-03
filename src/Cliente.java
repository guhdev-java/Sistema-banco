public class Cliente {

    private String nome;
    private String cpf;
    

public Cliente(String nome, String cpf) {      // Construtor para criar um cliente, recebendo o nome e o CPF como parâmetros, e realizando a validação do CPF para garantir que seja um número válido, lançando uma exceção em caso de CPF inválido, e inicializando os atributos do cliente com os valores fornecidos
    this.nome = nome;
    setCpf(cpf);

}
public void setCpf(String cpf) {      // Método para definir o CPF do cliente, realizando a validação para garantir que o CPF seja um número válido, removendo quaisquer caracteres não numéricos e verificando se o CPF possui exatamente 11 dígitos, lançando uma exceção em caso de CPF inválido
    cpf = cpf.replaceAll("\\D", ""); 

    if(cpf.length() == 11) {
        this.cpf = cpf;
    } else {
        throw new IllegalArgumentException("CPF inválido.");
    }
}
public String getCpf() {            // Método para obter o CPF do cliente, retornando o valor do CPF armazenado no objeto Cliente, permitindo que outras partes do programa acessem o CPF do cliente quando necessário
    return cpf;
}
public String getCpfFormatado() {          // Método para obter o CPF formatado do cliente, verificando se o CPF é válido e formatando-o no padrão brasileiro "XXX.XXX.XXX-XX" para facilitar a leitura e apresentação do CPF em interfaces de usuário ou relatórios, retornando uma string formatada ou uma mensagem de CPF inválido caso o CPF não seja válido
    if (cpf == null || cpf.length() != 11) {
        return "CPF inválido.";
    }
    return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
}
public String getNome() {                 // Método para obter o nome do cliente, retornando o valor do nome armazenado no objeto Cliente, permitindo que outras partes do programa acessem o nome do cliente quando necessário
    return nome;
}  
public void setNome(String nome) {      // Método para definir o nome do cliente, recebendo um valor de nome como parâmetro e atribuindo-o ao atributo nome do objeto Cliente, permitindo que o nome do cliente seja atualizado ou modificado conforme necessário
    this.nome = nome;
}
}



