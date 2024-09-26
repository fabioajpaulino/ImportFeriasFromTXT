package br.com.tresclicksrh.bencorp_integrations.dto;

import java.time.LocalDate;

public class ColaboradorVacationDto {
    private String codigo; //ok
    private String nome; //ok
    private int id;
    private LocalDate dataAdmissao; //ok
    private LocalDate dataVencimentoFerias;
    private boolean periodoVencido; //ok
    private int qtdAvosDeFeriasDoPeriodoAquisitivo; //ok
    private LocalDate inicioPeriodoAquisitivo; //ok
    private LocalDate fimPeriodoAquisitivo; //ok
    private boolean abonoPecuniario;
    private boolean adiantamento13Salario;
    private int qtdDiasRestantes; //ok
    private int qtdDiasGozados; //ok
    private LocalDate dataLimiteParaGozo; //ok
    private int qtdFaltasNoPeriodo;
    private int company_id;
    private int created_by_id;
    private int targetUserId;

    public int getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getCreated_by_id() {
        return created_by_id;
    }

    public void setCreated_by_id(int created_by_id) {
        this.created_by_id = created_by_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQtdFaltasNoPeriodo() {
        return qtdFaltasNoPeriodo;
    }

    public void setQtdFaltasNoPeriodo(int qtdFaltasNoPeriodo) {
        this.qtdFaltasNoPeriodo = qtdFaltasNoPeriodo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public LocalDate getDataVencimentoFerias() {
        return dataVencimentoFerias;
    }

    public void setDataVencimentoFerias(LocalDate dataVencimentoFerias) {
        this.dataVencimentoFerias = dataVencimentoFerias;
    }

    public boolean isPeriodoVencido() {
        return periodoVencido;
    }

    public void setPeriodoVencido(int pVenc) {
        this.periodoVencido= pVenc == 1;
    }

    public int getQtdAvosDeFeriasDoPeriodoAquisitivo() {
        return qtdAvosDeFeriasDoPeriodoAquisitivo;
    }

    public void setQtdAvosDeFeriasDoPeriodoAquisitivo(int qtdAvosPeriodoAtual) {
        this.qtdAvosDeFeriasDoPeriodoAquisitivo = qtdAvosPeriodoAtual;
    }

    public LocalDate getInicioPeriodoAquisitivo() {
        return inicioPeriodoAquisitivo;
    }

    public void setInicioPeriodoAquisitivo(LocalDate inicioPeriodoAquisitivo) {
        this.inicioPeriodoAquisitivo = inicioPeriodoAquisitivo;
    }

    public LocalDate getFimPeriodoAquisitivo() {
        return fimPeriodoAquisitivo;
    }

    public void setFimPeriodoAquisitivo(LocalDate fimPeriodoAquisitivo) {
        this.fimPeriodoAquisitivo = fimPeriodoAquisitivo;
    }

    public boolean isAbonoPecuniario() {
        return abonoPecuniario;
    }

    public void setAbonoPecuniario(boolean abonoPecuniario) {
        this.abonoPecuniario = abonoPecuniario;
    }

    public boolean isAdiantamento13Salario() {
        return adiantamento13Salario;
    }

    public void setAdiantamento13Salario(boolean adiantamento13Salario) {
        this.adiantamento13Salario = adiantamento13Salario;
    }

    public int getQtdDiasRestantes() {
        return qtdDiasRestantes;
    }

    public void setQtdDiasRestantes(int qtdDiasRestantes) {
        this.qtdDiasRestantes = qtdDiasRestantes;
    }

    public int getQtdDiasGozados() {
        return qtdDiasGozados;
    }

    public void setQtdDiasGozados(int qtdDiasGozados) {
        this.qtdDiasGozados = qtdDiasGozados;
    }

    public LocalDate getDataLimiteParaGozo() {
        return dataLimiteParaGozo;
    }

    public void setDataLimiteParaGozo(LocalDate dataLimiteParaGozo) {
        this.dataLimiteParaGozo = dataLimiteParaGozo;
    }
}
