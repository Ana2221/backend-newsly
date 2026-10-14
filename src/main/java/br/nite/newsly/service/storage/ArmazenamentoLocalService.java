package br.nite.newsly.service.storage;

import br.nite.newsly.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implementação local do ArmazenamentoService.
 *
 * Salva arquivos na pasta "uploads" relativa ao diretório de trabalho
 * da aplicação (user.dir). Em desenvolvimento isso aponta para a raiz
 * do projeto; em produção aponta para o diretório onde o JAR é executado.
 *
 * Estrutura gerada em disco:
 *   {user.dir}/uploads/noticias/{uuid}.jpg
 *   {user.dir}/uploads/logos/{uuid}.png
 *
 * Caminho salvo no banco (relativo, para servir via HTTP):
 *   /uploads/noticias/{uuid}.jpg
 *   /uploads/logos/{uuid}.png
 */
@Service
public class ArmazenamentoLocalService implements ArmazenamentoService {

    private static final Logger log = LoggerFactory.getLogger(ArmazenamentoLocalService.class);

    @Value("${storage.local.path:uploads}")
    private String pastaBase;

    /**
     * Resolve o caminho absoluto da pasta base relativo ao diretório de trabalho.
     * user.dir = pasta onde a JVM foi iniciada (raiz do projeto em dev, pasta do JAR em prod).
     */
    private Path resolverBase() {
        return Paths.get(System.getProperty("user.dir"), pastaBase);
    }

    @Override
    public String salvar(MultipartFile arquivo, String subpasta) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo não pode ser vazio.");
        }

        String extensao = obterExtensao(arquivo.getOriginalFilename());
        String nomeArquivo = UUID.randomUUID() + "." + extensao;
        String caminhoRelativo = "/uploads/" + subpasta + "/" + nomeArquivo;

        try {
            Path destino = resolverBase().resolve(subpasta).resolve(nomeArquivo);
            Files.createDirectories(destino.getParent());
            arquivo.transferTo(destino.toFile());
            log.info("[STORAGE] Arquivo salvo em: {}", destino.toAbsolutePath());
            return caminhoRelativo;
        } catch (IOException e) {
            log.error("[STORAGE] Erro ao salvar arquivo: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    @Override
    public void remover(String caminhoRelativo) {
        if (caminhoRelativo == null || caminhoRelativo.isBlank()) {
            return;
        }
        try {
            // /uploads/noticias/uuid.jpg → uploads/noticias/uuid.jpg
            String semBarra = caminhoRelativo.replaceFirst("^/uploads/", "");
            Path arquivo = resolverBase().resolve(semBarra);
            boolean removido = Files.deleteIfExists(arquivo);
            if (removido) {
                log.info("[STORAGE] Arquivo removido: {}", arquivo.toAbsolutePath());
            }
        } catch (IOException e) {
            log.warn("[STORAGE] Não foi possível remover {}: {}", caminhoRelativo, e.getMessage());
        }
    }

    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) {
            return "jpg";
        }
        return nomeOriginal.substring(nomeOriginal.lastIndexOf('.') + 1).toLowerCase();
    }
}
