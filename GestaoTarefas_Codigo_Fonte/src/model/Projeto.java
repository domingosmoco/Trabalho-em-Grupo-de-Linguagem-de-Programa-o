package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Projeto - Representa um projeto (coleção de tarefas).
 *
 * CONCEITOS DE POO APLICADOS:
 * - Encapsulamento: atributos privados
 * - Composição: Projeto contém Tarefas (relação HAS-A)
 * - Agregação: tarefas podem existir sem o projeto
 */
public class Projeto {

    // =============================================
    // ENUM: Estado do projeto
    // =============================================
    public enum EstadoProjeto {
        PLANEAMENTO("Planeamento"),
        ATIVO("Ativo"),
        PAUSADO("Pausado"),
        CONCLUIDO("Concluído"),
        CANCELADO("Cancelado");

        private final String descricao;

        EstadoProjeto(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    // =============================================
    // ATRIBUTOS PRIVADOS
    // =============================================
    private String id;
    private String nome;
    private String descricao;
    private EstadoProjeto estado;
    private String dataCriacao;
    private String dataFimPrevista;
    private List<String> tarefasIds;  // IDs das tarefas do projeto
    private String gerenteId;         // ID do usuário gerente

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // =============================================
    // CONSTRUTORES
    // =============================================

    public Projeto(String nome, String descricao) {
        this.id = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.nome = nome;
        this.descricao = descricao;
        this.estado = EstadoProjeto.PLANEAMENTO;
        this.dataCriacao = LocalDateTime.now().format(FORMATTER);
        this.tarefasIds = new ArrayList<>();
    }

    public Projeto() {
        this.id = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.estado = EstadoProjeto.PLANEAMENTO;
        this.dataCriacao = LocalDateTime.now().format(FORMATTER);
        this.tarefasIds = new ArrayList<>();
    }

    // =============================================
    // MÉTODOS DE COMPORTAMENTO
    // =============================================

    /** Adiciona uma tarefa ao projeto */
    public void adicionarTarefa(Tarefa tarefa) {
        if (!tarefasIds.contains(tarefa.getId())) {
            tarefasIds.add(tarefa.getId());
            tarefa.setProjetoId(this.id);
        }
    }

    /** Remove uma tarefa do projeto */
    public void removerTarefa(String tarefaId) {
        tarefasIds.remove(tarefaId);
    }

    /** Define o estado do projeto */
    public void alterarEstado(EstadoProjeto novoEstado) {
        this.estado = novoEstado;
    }

    /** Calcula o percentual de conclusão do projeto */
    public double calcularProgresso(List<Tarefa> todasTarefas) {
        if (tarefasIds.isEmpty()) return 0.0;

        long concluidas = todasTarefas.stream()
                .filter(t -> tarefasIds.contains(t.getId()))
                .filter(Tarefa::estaConcluida)
                .count();

        return (double) concluidas / tarefasIds.size() * 100.0;
    }

    /** Retorna o total de tarefas no projeto */
    public int getTotalTarefas() {
        return tarefasIds.size();
    }

    // =============================================
    // GETTERS E SETTERS
    // =============================================
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public EstadoProjeto getEstado() { return estado; }
    public void setEstado(EstadoProjeto estado) { this.estado = estado; }

    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getDataFimPrevista() { return dataFimPrevista; }
    public void setDataFimPrevista(String dataFimPrevista) { this.dataFimPrevista = dataFimPrevista; }

    public List<String> getTarefasIds() { return tarefasIds; }
    public void setTarefasIds(List<String> tarefasIds) { this.tarefasIds = tarefasIds; }

    public String getGerenteId() { return gerenteId; }
    public void setGerenteId(String gerenteId) { this.gerenteId = gerenteId; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d tarefas)", id, nome, estado.getDescricao(), tarefasIds.size());
    }
}
