package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe Usuario - Representa um utilizador do sistema.
 *
 * CONCEITOS DE POO APLICADOS:
 * - Encapsulamento: atributos privados com getters/setters
 * - Composição: um Usuário tem uma lista de Tarefas (relação HAS-A)
 * - Streams e Lambdas: filtragem funcional de tarefas
 */
public class Usuario {

    // =============================================
    // ATRIBUTOS PRIVADOS (Encapsulamento)
    // =============================================
    private String id;
    private String nome;
    private String email;
    private String cargo;
    private List<String> tarefasIds; // IDs das tarefas atribuídas

    // =============================================
    // CONSTRUTORES
    // =============================================

    public Usuario(String nome, String email, String cargo) {
        this.id = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.tarefasIds = new ArrayList<>();
    }

    public Usuario() {
        this.id = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.tarefasIds = new ArrayList<>();
    }

    // =============================================
    // MÉTODOS DE COMPORTAMENTO
    // =============================================

    /**
     * Atribui uma tarefa ao usuário.
     * Adiciona o ID da tarefa à lista e atribui o email do usuário na tarefa.
     */
    public void atribuirTarefa(Tarefa tarefa) {
        if (!tarefasIds.contains(tarefa.getId())) {
            tarefasIds.add(tarefa.getId());
            tarefa.atribuirUsuario(this.email);
        }
    }

    /**
     * Remove uma tarefa da lista do usuário.
     */
    public void removerTarefa(String tarefaId) {
        tarefasIds.remove(tarefaId);
    }

    /**
     * Marca uma tarefa como concluída (acessa via lista global de tarefas).
     */
    public void marcarTarefaConcluida(Tarefa tarefa) {
        if (tarefasIds.contains(tarefa.getId())) {
            tarefa.alterarStatus(Tarefa.Status.CONCLUIDA);
        }
    }

    /**
     * Retorna a quantidade de tarefas atribuídas ao usuário.
     */
    public int getTotalTarefas() {
        return tarefasIds.size();
    }

    /**
     * Verifica se o email é válido (validação simples).
     */
    public static boolean emailValido(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    // =============================================
    // GETTERS E SETTERS
    // =============================================
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public List<String> getTarefasIds() { return tarefasIds; }
    public void setTarefasIds(List<String> tarefasIds) { this.tarefasIds = tarefasIds; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %d tarefas", nome, email, tarefasIds.size());
    }
}
