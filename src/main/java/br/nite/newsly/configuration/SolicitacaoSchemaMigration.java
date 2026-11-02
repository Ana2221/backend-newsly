package br.nite.newsly.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SolicitacaoSchemaMigration implements ApplicationRunner {

    private static final List<String> NULLABLE_COLUMNS = List.of("gestor_id", "noticia_id");

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        for (String column : NULLABLE_COLUMNS) {
            makeNullableIfNeeded(column);
        }
    }

    private void makeNullableIfNeeded(String column) {
        String nullable = jdbcTemplate.queryForObject("""
                SELECT IS_NULLABLE
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'solicitacao'
                  AND COLUMN_NAME = ?
                """, String.class, column);

        if ("NO".equalsIgnoreCase(nullable)) {
            jdbcTemplate.execute("ALTER TABLE solicitacao MODIFY COLUMN " + column + " BIGINT NULL");
        }
    }
}
