package br.nite.newsly.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.*;

class SolicitacaoSchemaMigrationTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final SolicitacaoSchemaMigration migration = new SolicitacaoSchemaMigration(jdbcTemplate);

    @Test
    void liberaGestorENoticiaIdNulosEmBancoExistente() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString())).thenReturn("NO");

        migration.run(new DefaultApplicationArguments(new String[0]));

        verify(jdbcTemplate).execute("ALTER TABLE solicitacao MODIFY COLUMN gestor_id BIGINT NULL");
        verify(jdbcTemplate).execute("ALTER TABLE solicitacao MODIFY COLUMN noticia_id BIGINT NULL");
    }

    @Test
    void naoAlteraColunasJaConfiguradasComoNulas() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString())).thenReturn("YES");

        migration.run(new DefaultApplicationArguments(new String[0]));

        verify(jdbcTemplate, never()).execute(anyString());
    }
}
