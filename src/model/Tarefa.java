package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe Tarefa - Representa uma tarefa individual no sistema.
 * 
 * CONCEITOS DE POO APLICADOS:
 * - Encapsulamento: atributos privados com getters/setters
 * - Abstração: representa o conceito real de uma tarefa
 */
public class Tarefa {

    // =============================================
    // ENUM: Define os possíveis estados de uma tarefa
    // =============================================
    public enum Status {
        PENDENTE("Pendente"),
        EM_ANDAMENTO("Em Andamento"),
        CONCLUIDA("Concluída"),
        CANCELADA("Cancelada");

        private final String descricao;

        Status(String descricao) {
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
    // ENUM: Prioridade da tarefa
    // =============================================
    public enum Prioridade {
        BAIXA("Baixa"),
        MEDIA("Média"),
        ALTA("Alta"),
        CRITICA("Crítica");

        private final String descricao;

        Prioridade(String descricao) {
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
    // ATRIBUTOS PRIVADOS (Encapsulamento)
    // =============================================
    private String id;
    private String titulo;
    private String descricao;
    private Status status;
    private Prioridade prioridade;
    private String dataCreacao;
    private String dataConclusao;
    private String usuarioAtribuido;
    private String projetoId;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // =============================================
    // CONSTRUTORES
    // =============================================

    /** Construtor completo - usado ao criar nova tarefa */
    public Tarefa(String titulo, String descricao, Prioridade prioridade) {
        this.id = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = Status.PENDENTE; // toda tarefa começa como PENDENTE
        this.prioridade = prioridade;
        this.dataCreacao = LocalDateTime.now().format(FORMATTER);
        this.dataConclusao = null;
        this.usuarioAtribuido = null;
        this.projetoId = null;
    }

    /** Construtor padrão - necessário para deserialização JSON */
    public Tarefa() {
        this.id = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = Status.PENDENTE;
        this.prioridade = Prioridade.MEDIA;
        this.dataCreacao = LocalDateTime.now().format(FORMATTER);
    }

    // =============================================
    // MÉTODOS DE COMPORTAMENTO
    // =============================================

    /**
     * Altera o status da tarefa.
     * Se o novo status for CONCLUIDA, registra a data de conclusão.
     */
    public void alterarStatus(Status novoStatus) {
        this.status = novoStatus;
        if (novoStatus == Status.CONCLUIDA) {
            this.dataConclusao = LocalDateTime.now().format(FORMATTER);
        } else {
            this.dataConclusao = null; // limpa a data se reaberta
        }
    }

    /** Atualiza a descrição da tarefa */
    public void atualizarDescricao(String novaDescricao) {
        this.descricao = novaDescricao;
    }

    /** Atualiza o título da tarefa */
    public void atualizarTitulo(String novoTitulo) {
        this.titulo = novoTitulo;
    }

    /** Atribui um usuário à tarefa e muda status para EM_ANDAMENTO */
    public void atribuirUsuario(String emailUsuario) {
        this.usuarioAtribuido = emailUsuario;
        if (this.status == Status.PENDENTE) {
            this.status = Status.EM_ANDAMENTO;
        }
    }

    /** Verifica se a tarefa está concluída */
    public boolean estaConcluida() {
        return this.status == Status.CONCLUIDA;
    }

    /** Verifica se a tarefa está pendente */
    public boolean estaPendente() {
        return this.status == Status.PENDENTE;
    }

    // =============================================
    // GETTERS E SETTERS (Encapsulamento)
    // =============================================
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }

    public String getDataCreacao() { return dataCreacao; }
    public void setDataCreacao(String dataCreacao) { this.dataCreacao = dataCreacao; }

    public String getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(String dataConclusao) { this.dataConclusao = dataConclusao; }

    public String getUsuarioAtribuido() { return usuarioAtribuido; }
    public void setUsuarioAtribuido(String usuarioAtribuido) { this.usuarioAtribuido = usuarioAtribuido; }

    public String getProjetoId() { return projetoId; }
    public void setProjetoId(String projetoId) { this.projetoId = projetoId; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", id, titulo, status.getDescricao(), prioridade.getDescricao());
    }
}
