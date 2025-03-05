package br.com.batchaula.tickets;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
public class ImportacaoJobConfiguration {

    @Autowired
    private PlatformTransactionManager transactionManager;


    @Bean // Para que o motodo seja gerenciado pelo spring
    public Job job(Step passoInicial, // O Job recebe um step para iniciar o processo
                   JobRepository jobRepository // Utilizado para monitorar o estado e status do job
    ) {
        return new JobBuilder("geracao-tiackets", jobRepository) // Definir nome do JOB
                .start(passoInicial) // Definir primeiro step para iniciar o job
                .incrementer(new RunIdIncrementer()) // Para gerar logs, acada execução ocorrerar uma incremetnação e poderemos acompanhar
                .build();
    }

    @Bean
    public Step passoInicial(ItemReader<Importacao> reader, ItemWriter<Importacao> writer, JobRepository jobRepository // Utilizado para monitorar o estado e status do job

    ) {
        return new StepBuilder("passo-inicial", jobRepository) // Definir nome do step
                // Definir quais são os passos;
                // definir tamanho do chunck ou seja, a quantidade que será processada e caso ocorra algum erro ele possa fazer um rollback dessa quantidade de item que estão sendo processados;
                // O transactionManager serve para transacionar o lote de dados processados
                // <Importacao, Importacao> estou defuinindo o que estou lendo um tipo de dados do tipo Importação e o segndo Importacao defini que estou salvando um tipo de dados Importacao
                // FIM DA EXPLICAÇÃO DO CHUNCK
                .<Importacao, Importacao>chunk(200, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Importacao> reader() {
        return new FlatFileItemReaderBuilder<Importacao>()
                // Define o nome do leitor de itens
                .name("leitura-csv")
                // Especifica o recurso de arquivo que será lido
                .resource(new FileSystemResource("file/dados.csv"))
                // Define um prefixo de comentário
                .comments("--")
                // Indica que o arquivo CSV usa delimitadores
                .delimited()
                //Informar qual o delimitador
                .delimiter(";")
                // Define os nomes dos campos que serão lidos do arquivo CSV
                .names("cpf", "cliente", "nascimento", "evento", "data", "tipoIngresso", "valor")
                // Define o FieldSetMapper personalizado que mapeia os campos lidos do arquivo CSV para o objeto Importacao
                .fieldSetMapper(new ImportacaoMapper())
                // Constrói o FlatFileItemReader com as configurações fornecidas
                .build();

    }

    @Bean
    public ItemWriter<Importacao> writer(DataSource dataSource) {
        // Define o método writer como um bean gerenciado pelo Spring
        return new JdbcBatchItemWriterBuilder<Importacao>()
                // Especifica a fonte de dados que será usada pelo escritor
                .dataSource(dataSource)
                // Define a instrução SQL que será usada para inserir os dados no banco de dados
                .sql(
                        "INSERT INTO importacao ( cpf, cliente, evento, data, tipo_ingresso, valor, hora_importacao) VALUES" +
                                " (:cpf, :cliente, :evento, :data, :tipoIngresso, :valor, :horaImportacao)"
                )
                // Define o provedor de parâmetros SQL baseado nas propriedades dos beans
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                // Constrói o JdbcBatchItemWriter com as configurações fornecidas
                .build();
    }

}
