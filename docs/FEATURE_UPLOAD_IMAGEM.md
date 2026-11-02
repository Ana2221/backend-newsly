# Feature — Upload de Imagem

## Contexto

Permite fazer upload da imagem de uma notícia e da logo de uma empresa.
O armazenamento atual é local (disco), mas a arquitetura usa uma interface
que permite trocar para S3, GCS ou outro provedor sem alterar os services.
Os arquivos são servidos diretamente pelo Spring como recursos estáticos.

---

## Arquitetura

```
Controller
    ↓ MultipartFile
Service
    ↓
ArmazenamentoService (interface)
    ↓
ArmazenamentoLocalService (implementação atual)
    ↓ salva em disco em {user.dir}/uploads/
retorna caminho relativo → salvo na entidade

Spring MVC (StaticResourceConfiguration)
    ↓ mapeia /uploads/** → {user.dir}/uploads/
Frontend acessa: GET /uploads/noticias/uuid.jpg
```

Para trocar de armazenamento no futuro:
1. Crie nova classe que implemente `ArmazenamentoService`
2. Desative `@Service` do `ArmazenamentoLocalService`
3. Desative ou remova `StaticResourceConfiguration`

---

## Configuração

No `application.properties`:

```properties
storage.local.path=uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

Estrutura gerada em disco:
```
{user.dir}/
└── uploads/
    ├── noticias/
    │   └── {uuid}.jpg
    └── logos/
        └── {uuid}.png
```

---

## Como o frontend renderiza as imagens

O Spring serve a pasta `uploads` como recurso estático via `StaticResourceConfiguration`.
Basta usar o caminho retornado pela API diretamente como `src`:

```html
<img src="https://backend-newsly.onrender.com/uploads/noticias/uuid.jpg" />
```

Ou em React/Vue:
```jsx
const BASE_URL = "https://backend-newsly.onrender.com";
<img src={`${BASE_URL}${noticia.imagemUrl}`} />
<img src={`${BASE_URL}${empresa.logoUrl}`} />
```

O campo `imagemUrl` / `logoUrl` retornado pela API já vem no formato `/uploads/...`,
então basta concatenar com a base URL.

---

## Endpoints

### Upload de imagem da notícia
```
PATCH /noticias/{id}/imagem
Content-Type: multipart/form-data
```

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `Long` (path) | ID da notícia |
| `arquivo` | `MultipartFile` (form-data) | Arquivo de imagem |

**Response `200` — NoticiaResponseDTO:**
```json
{
  "id": 1,
  "titulo": "Nova parceria firmada",
  "conteudo": "...",
  "publicadaEm": "2026-08-17T10:00:00",
  "imagemUrl": "/uploads/noticias/a1b2c3d4-uuid.jpg",
  "empresaId": 1,
  "empresaNome": "Nite Jr"
}
```

Para renderizar:
```jsx
<img src={`https://backend-newsly.onrender.com${noticia.imagemUrl}`} />
```

**Erros:**
- `404` — Notícia não encontrada
- `409` — Arquivo vazio ou inválido
- `500` — Erro ao salvar no disco

---

### Upload de logo da empresa
```
PATCH /empresas/{id}/logo
Content-Type: multipart/form-data
```

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `Long` (path) | ID da empresa |
| `arquivo` | `MultipartFile` (form-data) | Arquivo de imagem |

**Response `200` — EmpresaJrResponseDTO:**
```json
{
  "id": 1,
  "nome": "Nite Jr",
  "cnpj": "12.345.678/0001-90",
  "logoUrl": "/uploads/logos/b2c3d4e5-uuid.png",
  ...
}
```

Para renderizar:
```jsx
<img src={`https://backend-newsly.onrender.com${empresa.logoUrl}`} />
```

**Erros:**
- `404` — Empresa não encontrada
- `409` — Arquivo vazio ou inválido
- `500` — Erro ao salvar no disco

---

## Comportamento ao re-fazer upload

Ao enviar um novo arquivo para uma notícia ou empresa que já possui imagem,
o arquivo anterior é **removido automaticamente** antes de salvar o novo.

---

## Como consumir no frontend

```javascript
const formData = new FormData();
formData.append('arquivo', file);

// Upload imagem de notícia
fetch('https://backend-newsly.onrender.com/noticias/1/imagem', {
  method: 'PATCH',
  body: formData
});

// Upload logo de empresa
fetch('https://backend-newsly.onrender.com/empresas/1/logo', {
  method: 'PATCH',
  body: formData
});
```

---

## Arquivos criados/modificados

| Arquivo | Tipo |
|---------|------|
| `configuration/StaticResourceConfiguration.java` | Novo — serve /uploads/** como estático |
| `service/storage/ArmazenamentoService.java` | Interface |
| `service/storage/ArmazenamentoLocalService.java` | Implementação local |
| `entity/Noticia.java` | Campo `imagemUrl` |
| `entity/EmpresaJr.java` | Campo `logoUrl` |
| `dto/response/NoticiaResponseDTO.java` | Campo `imagemUrl` |
| `dto/response/EmpresaJrResponseDTO.java` | Campo `logoUrl` |
| `service/NoticiaService.java` | Método `uploadImagem()` |
| `service/EmpresaJrService.java` | Método `uploadLogo()` |
| `controller/NoticiaController.java` | `PATCH /{id}/imagem` |
| `controller/EmpresaJrController.java` | `PATCH /{id}/logo` |
| `application.properties` | Configs de multipart e storage |
