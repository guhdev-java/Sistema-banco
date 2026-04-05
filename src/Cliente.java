public class Cliente {

    private String nome;
    private String cpf;
    

public Cliente(String nome, String cpf) {      
    this.nome = nome;
    setCpf(cpf);

}
public void setCpf(String cpf) {      
    cpf = cpf.replaceAll("\\D", ""); 

    if(cpf.length() == 11) {
        this.cpf = cpf;
    } else {
        throw new IllegalArgumentException("CPF inválido."); 
    }
}
public String getCpf() {            
    return cpf;
}
public String getCpfFormatado() {          
    if (cpf == null || cpf.length() != 11) {
        return "CPF inválido.";
    }
    return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
}
public String getNome() {                 
    return nome;
}  
public void setNome(String nome) {      
    this.nome = nome;
}
}



