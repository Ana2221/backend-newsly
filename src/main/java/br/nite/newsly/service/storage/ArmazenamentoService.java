package br.nite.newsly.service.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface de armazenamento de arquivos.
 * Atualmente implementada com armazenamento local.
 * Futuramente pode ser substituída por S3, GCS ou outro provedor
 * sem alterar os services que a utilizam.
 */
public interface ArmazenamentoService {

    /**
     * Armazena o arquivo e retorna o caminho relativo para acesso.
     *
     * @param arquivo  arquivo recebido via multipart
     * @param subpasta subpasta de destino (ex: "noticias", "logos")
     * @return caminho relativo do arquivo salvo (ex: /uploads/noticias/uuid.jpg)
     */
    String salvar(MultipartFile arquivo, String subpasta);

    /**
     * Remove um arquivo pelo caminho informado.
     * Usado para substituir imagens antigas ao fazer novo upload.
     *
     * @param caminho caminho relativo do arquivo a remover
     */
    void remover(String caminho);
}
