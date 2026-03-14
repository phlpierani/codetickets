package br.com.alura.demo;

import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ImportacaoMapper implements FieldSetMapper<Importacao> {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Importacao mapFieldSet(FieldSet fieldSet) throws BindException {
        Importacao importacao = new Importacao();
        importacao.setCpf(fieldSet.readString("cpf"));
        importacao.setCliente(fieldSet.readString("cliente"));
        importacao.setNascimento(LocalDate.parse(fieldSet.readString("nascimento"), dateFormatter));
        importacao.setEvento(fieldSet.readString("evento"));
        importacao.setData(LocalDate.parse(fieldSet.readString("data"), dateFormatter));
        importacao.setTipoIngresso(fieldSet.readString("tipoIngresso"));
        importacao.setValor(fieldSet.readDouble("valor"));
        return importacao;

    }
}
// O método mapFieldSet é responsável por mapear os campos lidos do arquivo CSV para um objeto Importacao.
//  Ele utiliza os métodos readString e readDouble do FieldSet para ler os valores dos campos, e os métodos parse do LocalDate para converter as strings de data em objetos LocalDate.
//  O resultado é um objeto Importacao preenchido com os dados lidos do arquivo CSV.