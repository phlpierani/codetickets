package br.com.alura.demo;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
public class ImportacaoJobConfig {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public Job job(Step passoInicial, JobRepository jobRepository) {
        return new JobBuilder("geracao-tickets", jobRepository )
                .start(passoInicial)
                .incrementer(new RunIdIncrementer())
                .build();
        // O RunIdIncrementer é usado para garantir que cada execução do job tenha um ID único,
        // permitindo que o mesmo job seja executado várias vezes sem conflitos de ID.
    }

    @Bean
    public Step passoInicial(ItemReader<Importacao> reader,
                             ItemWriter<Importacao> writer,
                             JobRepository jobRepository) {

        return new StepBuilder("passo-inicial", jobRepository)
                .<Importacao, Importacao>chunk(100) // Define o tamanho do chunk para processamento em lotes
                .reader(reader) // Define o leitor de itens, que é responsável por ler os dados de origem (por exemplo, um arquivo CSV ou um banco de dados)
                .writer(writer) // Define o escritor de itens, que é responsável por escrever os dados processados para o destino (por exemplo, um banco de dados)
                .transactionManager(transactionManager) // Define o gerenciador de transações para garantir que as operações de leitura e escrita sejam realizadas
                .build();
    }

    @Bean
    public ItemReader<Importacao> reader() {
        return new FlatFileItemReaderBuilder<Importacao>()
                .name("leitura-csv") // Define o nome do leitor de itens, que pode ser usado para fins de identificação e depuração
                .resource(new FileSystemResource("files/dados.csv")) // Especifica o recurso de entrada, que neste caso é um arquivo CSV localizado no sistema de arquivos
                .comments("--") // Define o prefixo para linhas de comentário no arquivo CSV, que serão ignoradas durante a leitura
                .delimited() // Especifica que o arquivo é delimitado (por exemplo,
                .names("cpf", "cliente", "nascimento", "evento", "data", "tipoIngresso", "valor") // Define os nomes das colunas no arquivo CSV, que serão mapeados para os campos da classe Importacao
                .targetType(Importacao.class) // Especifica a classe de destino para o mapeamento dos dados lidos do arquivo CSV, permitindo que os dados sejam convertidos em objetos do tipo Importacao
                .build();
    }

    @Bean
    public ItemWriter<Importacao> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Importacao>()
                .dataSource(dataSource)  // Especifica a fonte de dados (DataSource) que será usada para se conectar ao banco de dados e executar as operações de escrita
                .sql( // Define a consulta SQL que será usada para inserir os dados processados no banco de dados.
                        "INSERT INTO importacao (id, cpf, cliente, evento, data, tipo_ingresso, valor, hora_importacao) VALUES" +
                                "(:id, :cpf, :cliente, :evento, :data, :tipo_ingresso, :valor, " + LocalDateTime.now() + ")"

                )
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>()) // Especifica o provedor de parâmetros SQL que será usado para mapear os campos dos objetos Importacao para os parâmetros nomeados na consulta SQL.
                // O BeanPropertyItemSqlParameterSourceProvider é uma implementação que mapeia os campos dos objetos para os parâmetros com base nos nomes dos campos.
                .build();
    }
}

