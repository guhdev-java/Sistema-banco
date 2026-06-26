package br.com.banco.model;

import br.com.banco.util.CpfValidator;
import org.mindrot.jbcrypt.BCrypt;
import java.util.UUID;

public class Cliente {
    private String id;
    private String nome;
    private String cpf;
    private String senhaHash;

    public Cliente() {}

    public Cliente(String nome, String cpf, String senha) {
        this.id = UUID.randomUUID().toString();
        setNome(nome);
        setSenha(senha);
        setCpf(cpf);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public final void setCpf(String cpf) {
        this.cpf = CpfValidator.validar(cpf);
    }

    public String getCpfFormatado() {
        return CpfValidator.formatar(cpf);
    }

    public String getSenha() {
        return senhaHash;
    }

    public final void setSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia.");
        }
        this.senhaHash = BCrypt.hashpw(senha.trim(), BCrypt.gensalt());
    }

    public boolean verificarSenha(String senha) {
        return senhaHash != null && BCrypt.checkpw(senha, senhaHash);
    }
}
