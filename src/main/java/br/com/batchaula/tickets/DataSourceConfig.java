package br.com.batchaula.tickets;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean // Indica que o método dataSource() é um bean gerenciado pelo Spring
    @ConfigurationProperties(prefix = "spring.datasource") // Especifica que as propriedades prefixadas com "spring.datasource" no arquivo de configuração serão mapeadas para o DataSource
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }


    @Bean // Indica que o método transactionManager() é um bean gerenciado pelo Spring
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") final DataSource dataSource) {
        // O parâmetro dataSource será injetado no método, indicando que este bean deve ser associado ao bean dataSource
        return new DataSourceTransactionManager(dataSource);
        // Cria e retorna uma instância de DataSourceTransactionManager, que gerencia transações para o DataSource fornecido
    }

}
