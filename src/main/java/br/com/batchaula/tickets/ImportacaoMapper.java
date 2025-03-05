package br.com.batchaula.tickets;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImportacaoMapper implements FieldSetMapper<Importacao> {
    // Define o formato para datas sem hora
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // Define o formato para datas com hora
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Importacao mapFieldSet(FieldSet fieldSet) throws BindException {
        // Cria uma nova instância de Importacao
        Importacao importacao = new Importacao();
        // Lê o campo "cpf" do FieldSet e define no objeto Importacao
        importacao.setCpf(fieldSet.readString("cpf"));
        // Lê o campo "cliente" do FieldSet e define no objeto Importacao
        importacao.setCliente(fieldSet.readString("cliente"));
        // Lê o campo "nascimento" do FieldSet, parseia para LocalDate e define no objeto Importacao
        importacao.setNascimento(LocalDate.parse(fieldSet.readString("nascimento"), dateFormatter));
        // Lê o campo "evento" do FieldSet e define no objeto Importacao
        importacao.setEvento(fieldSet.readString("evento"));
        // Lê o campo "data" do FieldSet, parseia para LocalDate e define no objeto Importacao
        importacao.setData(LocalDate.parse(fieldSet.readString("data"), dateFormatter));
        // Lê o campo "tipoIngresso" do FieldSet e define no objeto Importacao
        importacao.setTipoIngresso(fieldSet.readString("tipoIngresso"));
        // Lê o campo "valor" do FieldSet, parseia para double e define no objeto Importacao
        importacao.setValor(fieldSet.readDouble("valor"));
        // Define a hora atual como hora de importação
        importacao.setHoraImportacao(LocalDateTime.now());
        // Retorna o objeto Importacao populado
        return importacao;
    }
}
