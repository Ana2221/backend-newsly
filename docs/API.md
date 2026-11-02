# API Newsly — Mapeamento de Rotas

> Base URL: `https://backend-newsly.onrender.com`
> Content-Type: `application/json`

---

## Padrão de Erros

Todas as rotas retornam erros neste formato:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Usuário não encontrado com id: 1",
  "timestamp": "2026-08-17T12:00:00"
}
```

Erros de validação incluem o campo `errors`:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação nos campos enviados",
  "timestamp": "2026-08-17T12:00:00",
  "errors": [
    { "field": "nome", "message": "Nome é obrigatório" }
  ]
}
```

| Código | Significado |
|--------|-------------|
| `200` | OK |
| `201` | Criado com sucesso |
| `204` | Sem conteúdo (exclusão) |
| `400` | Dados inválidos |
| `404` | Recurso não encontrado |
| `409` | Conflito de negócio |
| `401` | Autenticação ausente ou inválida |
| `403` | Usuário autenticado sem permissão para a operação |
| `500` | Erro interno |

---

## Autenticação e autorização

Todas as rotas exigem um JWT Bearer, exceto `POST /usuarios`, `POST /usuarios/login` e `GET /uploads/**`.
Após o login, envie o token nas chamadas protegidas:

Para orientações de armazenamento do token, ciclo de sessão, tratamento de `401`/`403` e matriz de acesso por perfil, consulte [SEGURANCA_FRONTEND.md](./SEGURANCA_FRONTEND.md).

```http
Authorization: Bearer {accessToken}
```

O login retorna `200` com `accessToken`, `tokenType` (`Bearer`), `expiresIn` (segundos) e os dados básicos do usuário. Os tokens expiram após 30 minutos por padrão.

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresIn": 1800,
  "usuario": {
    "id": 1,
    "nome": "Ana Silva",
    "email": "ana@email.com",
    "tipo": "RESPONSAVEL",
    "permissoes": ["LEITOR", "ESCRITOR"]
  }
}
```

Política de acesso:

- `LEITOR` pode consultar conteúdo; `ESCRITOR` pode consultar e alterar notícias/comentários.
- `RESPONSAVEL` pode consultar, criar e editar dados da própria EJ, incluindo colaboradores, diretorias, diretores, funções, notícias e comentários. Não pode excluir registros nem consultar/alterar dados de outras EJs.
- Somente `ADMIN` pode criar EJs e gerenciar contas e dados globais.
- Somente `GESTOR` pode aceitar/recusar solicitações.
- `RESPONSAVEL` pode criar e cancelar solicitações próprias; gestores e responsáveis só consultam solicitações associadas a si.
- A criação pública em `POST /usuarios` aceita somente `tipo: "USUARIO"`. Somente `ADMIN` pode criar os demais tipos.

Para o responsável, `GET` de listas retorna somente os registros da EJ vinculada ao seu usuário. IDs de outros registros não autorizam acesso: consultas e alterações por ID também verificam a EJ proprietária. Operações `DELETE` continuam exclusivas de `ADMIN`.

Configure no ambiente do backend:

- `NEWSLY_JWT_SECRET`: segredo com no mínimo 32 bytes; nunca o versione.
- `NEWSLY_BOOTSTRAP_ADMIN_EMAIL` e `NEWSLY_BOOTSTRAP_ADMIN_PASSWORD`: credenciais do administrador inicial; a senha deve conter ao menos 12 caracteres.
- Opcional: `NEWSLY_JWT_EXPIRATION_SECONDS` (padrão `1800`).

O backend cria ou promove a conta configurada como administrador durante a inicialização. A inicialização falha se o segredo JWT ou as credenciais bootstrap não forem configurados.
Senhas novas são armazenadas com hash PBKDF2. Senhas legadas em texto puro são convertidas para hash no primeiro login bem-sucedido.

---

## Servindo Arquivos Estáticos

Imagens enviadas via upload são servidas diretamente pelo Spring em:
```
GET https://backend-newsly.onrender.com/uploads/{subpasta}/{arquivo}
```

Para renderizar no frontend, concatene a base URL com o campo retornado pela API:
```jsx
<img src={`https://backend-newsly.onrender.com${noticia.imagemUrl}`} />
<img src={`https://backend-newsly.onrender.com${empresa.logoUrl}`} />
```

---

## Usuários `/usuarios`

### POST `/usuarios`
Cria um usuário do tipo informado no campo `tipo`.

**Request Body:**
```json
{
  "tipo": "USUARIO",
  "nome": "Ana Silva",
  "senha": "senha123",
  "contato": {
    "telefone": "11999999999",
    "email": "ana@email.com"
  }
}
```

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `tipo` | `TipoUsuario` | ✅ | Cadastro público: `USUARIO`. `ADMIN` pode criar `COLABORADOR`, `GESTOR` e `RESPONSAVEL` |
| `nome` | `String` | ✅ | Máx. 150 caracteres |
| `senha` | `String` | ✅ | Entre 6 e 255 caracteres |
| `contato.telefone` | `String` | ❌ | Máx. 20 caracteres |
| `contato.email` | `String` | ❌ | Formato de e-mail válido |
| `atividade` | `Boolean` | ⚠️ | Obrigatório se `tipo = COLABORADOR` |
| `empresaId` | `Long` | ❌ | Obrigatório para `COLABORADOR` e `RESPONSAVEL` |
| `permissoes` | `List<Permissao>` | ❌ | `LEITOR`, `ESCRITOR`. Usado por `GESTOR` e `RESPONSAVEL` |

**Responses:**
- `201` — `CriarUsuarioResponseDTO` com o campo do tipo criado preenchido
- `403` — tentativa sem privilégios de criar um tipo diferente de `USUARIO`

---

### POST `/usuarios/login`
Autentica o usuário e retorna os dados do tipo real.

**Request Body:**
```json
{
  "email": "ana@email.com",
  "senha": "senha123"
}
```

**Responses:**
- `200` — `LoginResponseDTO` com token Bearer e dados básicos do usuário
- `401` — E-mail ou senha inválidos

---

### GET `/usuarios` — `200` `List<CriarUsuarioResponseDTO>` (ADMIN)
### GET `/usuarios/{id}` — `200` `CriarUsuarioResponseDTO` (ADMIN ou próprio usuário) / `404`
### PUT `/usuarios/{id}` — `200` `CriarUsuarioResponseDTO` (ADMIN) / `404`
### DELETE `/usuarios/{id}` — `204` (ADMIN) / `404`

---

## Colaboradores `/colaboradores`

### POST `/colaboradores`

**Request Body:**
```json
{
  "nome": "Ana Silva",
  "senha": "senha123",
  "contato": { "telefone": "11999999999", "email": "ana@email.com" },
  "atividade": true,
  "empresaId": 1
}
```

**Responses:** `201` — `ColaboradorResponseDTO`

### GET `/colaboradores` — `200` `List<ColaboradorResponseDTO>`
### GET `/colaboradores/{id}` — `200` `ColaboradorResponseDTO` / `404`
### PUT `/colaboradores/{id}` — `200` `ColaboradorResponseDTO` / `404`
### DELETE `/colaboradores/{id}` — `204` / `404`

---

## Gestores `/gestores`

### POST `/gestores`

**Request Body:**
```json
{
  "nome": "Carlos Lima",
  "senha": "senha123",
  "contato": { "telefone": "11999999999", "email": "carlos@email.com" },
  "permissoes": ["LEITOR", "ESCRITOR"]
}
```

**Responses:** `201` — `GestorResponseDTO`

### GET `/gestores` — `200` `List<GestorResponseDTO>`
### GET `/gestores/{id}` — `200` `GestorResponseDTO` / `404`
### PUT `/gestores/{id}` — `200` `GestorResponseDTO` / `404`
### DELETE `/gestores/{id}` — `204` / `404`

---

## Responsáveis `/responsaveis`

### POST `/responsaveis`

**Request Body:**
```json
{
  "nome": "Maria Costa",
  "senha": "senha123",
  "contato": { "telefone": "11999999999", "email": "maria@email.com" },
  "permissoes": ["ESCRITOR"],
  "empresaId": 1
}
```

**Responses:** `201` — `ResponsavelResponseDTO`
> `409` se a empresa já possuir um responsável cadastrado.

### GET `/responsaveis` — `200` `List<ResponsavelResponseDTO>`
### GET `/responsaveis/{id}` — `200` `ResponsavelResponseDTO` / `404`
### PUT `/responsaveis/{id}` — `200` `ResponsavelResponseDTO` / `404`
### DELETE `/responsaveis/{id}` — `204` / `404`

---

## Empresas `/empresas`

### POST `/empresas`

**Request Body:**
```json
{
  "nome": "Nite Jr",
  "cnpj": "12.345.678/0001-90",
  "contato": {
    "email": "contato@nitejr.com.br"
  },
  "responsavelId": 1
}
```

| Campo | Tipo | Obrigatório | Validação |
|-------|------|-------------|-----------|
| `nome` | `String` | ✅ | Máx. 150 caracteres |
| `cnpj` | `String` | ✅ | Obrigatório; fora do perfil `test`, consultado na BrasilAPI |
| `contato.email` | `String` | ✅ | E-mail válido e único entre empresas |
| `responsavelId` | `Long` | Para administrador | Ignorado para responsável autenticado |

**Responses:** `201` — `EmpresaJrResponseDTO`
> `400` se o CNPJ estiver ausente, for inválido na BrasilAPI ou o e-mail estiver ausente.
> `409` se o CNPJ ou e-mail já estiver cadastrado ou o responsável já possuir empresa.
> `503` se a BrasilAPI estiver indisponível.
> No perfil `test`, qualquer CNPJ não vazio é aceito sem consulta externa ou validação de formato.

---

### PATCH `/empresas/{id}/logo`
Upload da logo da empresa.

**Content-Type:** `multipart/form-data`

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `Long` (path) | ID da empresa |
| `arquivo` | `MultipartFile` (form-data) | Arquivo de imagem |

**Response `200`:** `EmpresaJrResponseDTO` com `logoUrl` preenchido.
> Se já existir uma logo, ela é removida automaticamente antes de salvar a nova.
> **Renderizar no frontend:** `<img src={"https://backend-newsly.onrender.com" + empresa.logoUrl} />`

---

### PATCH `/empresas/adicionar-membro/{idmembro}/{idempresa}`
Adiciona um colaborador à empresa.

**Responses:** `200`

---

### GET `/empresas` — `200` `List<EmpresaJrResponseDTO>`
### GET `/empresas/{id}` — `200` `EmpresaJrResponseDTO` / `404`
### PUT `/empresas/{id}` — `200` `EmpresaJrResponseDTO` / `404`
### DELETE `/empresas/{id}` — `204` / `404`

**EmpresaJrResponseDTO:**
```json
{
  "id": 1,
  "nome": "Nite Jr",
  "cnpj": "12.345.678/0001-90",
  "descricao": "...",
  "logoUrl": "/uploads/logos/uuid.png",
  "contato": { ... },
  "configurations": { ... },
  "responsavel": { ... },
  "colaboradores": [ ... ],
  "diretorias": [ ... ],
  "noticias": [ ... ]
}
```

---

## Diretorias `/diretorias`

### POST `/diretorias`

**Request Body:**
```json
{
  "criadaEm": "2024-01-01",
  "empresaId": 1
}
```

**Responses:** `201` — `DiretoriaResponseDTO`

### GET `/diretorias` — `200` `List<DiretoriaResponseDTO>`
### GET `/diretorias/{id}` — `200` `DiretoriaResponseDTO` / `404`
### PUT `/diretorias/{id}` — `200` `DiretoriaResponseDTO` / `404`
### DELETE `/diretorias/{id}` — `204` / `404`

---

## Diretores `/diretores`

### POST `/diretores`

**Request Body:**
```json
{
  "colaboradorId": 5,
  "funcaoId": 1,
  "diretoriaId": 1,
  "permissoes": ["ESCRITOR"]
}
```

**Responses:** `201` — `DiretorResponseDTO`
> `409` se o colaborador já for diretor.

### GET `/diretores` — `200` `List<DiretorResponseDTO>`
### GET `/diretores/{id}` — `200` `DiretorResponseDTO` / `404`
### PUT `/diretores/{id}` — `200` `DiretorResponseDTO` / `404`
### DELETE `/diretores/{id}` — `204` / `404`

---

## Funções `/funcoes`

### POST `/funcoes`

**Request Body:**
```json
{
  "nome": "Presidente",
  "descricao": "Responsável pela gestão geral",
  "empresaId": 1
}
```

Para `RESPONSAVEL`, `empresaId` é sempre definido pelo backend com a EJ do token; o valor enviado no request não permite mover a função para outra empresa. Para `ADMIN`, `empresaId` pode associar a função a uma EJ ou deixá-la global (`null`).

**Responses:** `201` — `FuncaoResponseDTO` com `empresaId`
> `409` se já existir uma função com o mesmo nome na EJ.

### GET `/funcoes` — `200` `List<FuncaoResponseDTO>` (RESPONSAVEL recebe apenas funções da própria EJ)
### GET `/funcoes/{id}` — `200` `FuncaoResponseDTO` (ADMIN ou responsável da EJ) / `404`
### PUT `/funcoes/{id}` — `200` `FuncaoResponseDTO` (ADMIN ou responsável da EJ) / `404`
### DELETE `/funcoes/{id}` — `204` (ADMIN) / `404`

Funções existentes antes da associação por EJ podem continuar com `empresaId: null`. Um ADMIN pode associá-las a uma EJ pelo `PUT /funcoes/{id}`. Funções globais não são listadas nem editáveis pelo responsável.

---

## Notícias `/noticias`

### POST `/noticias`

Esta rota continua disponível para gestores/escritores e administradores. Responsáveis devem usar `POST /solicitacoes`, que cria a pendência sem persistir a notícia.

**Request Body:**
```json
{
  "titulo": "Nova parceria firmada",
  "conteudo": "A empresa firmou uma nova parceria...",
  "publicadaEm": "2026-08-17T10:00:00",
  "empresaId": 1
}
```

**Responses:** `201` — `NoticiaResponseDTO`

---

### PATCH `/noticias/{id}/imagem`
Upload da imagem da notícia.

**Content-Type:** `multipart/form-data`

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `Long` (path) | ID da notícia |
| `arquivo` | `MultipartFile` (form-data) | Arquivo de imagem |

**Response `200`:** `NoticiaResponseDTO` com `imagemUrl` preenchido.
> Se já existir uma imagem, ela é removida automaticamente antes de salvar a nova.
> **Renderizar no frontend:** `<img src={"https://backend-newsly.onrender.com" + noticia.imagemUrl} />`

---

### GET `/noticias` — `200` `List<NoticiaResponseDTO>`
### GET `/noticias/{id}` — `200` `NoticiaResponseDTO` / `404`
### PUT `/noticias/{id}` — `200` `NoticiaResponseDTO` / `404`
### DELETE `/noticias/{id}` — `204` / `404`

**NoticiaResponseDTO:**
```json
{
  "id": 1,
  "titulo": "Nova parceria firmada",
  "conteudo": "...",
  "publicadaEm": "2026-08-17T10:00:00",
  "aprovada": true,
  "imagemUrl": "/uploads/noticias/uuid.jpg",
  "empresaId": 1,
  "empresaNome": "Nite Jr"
}
```

---

## Comentários `/comentarios`

### POST `/comentarios`

**Request Body:**
```json
{
  "conteudo": "Ótima notícia!",
  "publicadoEm": "2026-08-17T11:00:00",
  "noticiaId": 1,
  "comentarioPaiId": null
}
```

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `conteudo` | `String` | ✅ | — |
| `publicadoEm` | `LocalDateTime` | ✅ | — |
| `noticiaId` | `Long` | ✅ | — |
| `comentarioPaiId` | `Long` | ❌ | Informar para criar uma resposta a outro comentário |

**Responses:** `201` — `ComentarioResponseDTO`

### GET `/comentarios` — `200` `List<ComentarioResponseDTO>`
### GET `/comentarios/{id}` — `200` `ComentarioResponseDTO` / `404`
### GET `/comentarios/noticia/{noticiaId}` — `200` `List<ComentarioResponseDTO>` (apenas raiz)
### PUT `/comentarios/{id}` — `200` `ComentarioResponseDTO` / `404`
### DELETE `/comentarios/{id}` — `204` / `404`

---

## Solicitações `/solicitacoes`

### POST `/solicitacoes`
Responsável envia os dados completos da notícia. A API cria somente uma solicitação `PENDENTE`; a notícia ainda não existe e só será criada se um gestor aceitar.

**Request Body:**
```json
{
  "responsavelId": 1,
  "titulo": "Nova parceria firmada",
  "conteudo": "A empresa firmou uma nova parceria...",
  "publicadaEm": "2026-08-17T10:00:00",
  "empresaId": 1
}
```

**Responses:** `201` — `SolicitacaoResponseDTO`
Na resposta pendente, `noticiaId` é `null`; título, conteúdo, data e empresa vêm dos dados submetidos. Após aceitar, `noticiaId` aponta para a notícia recém-criada. Ao recusar ou cancelar, nenhuma notícia é criada.
Não envie `gestorId`: a solicitação entra na fila compartilhada. O gestor que aprovar ou recusar será registrado em `gestorId` na resposta.

### PATCH `/solicitacoes/{id}/aceitar` — Gestor aceita — `200`
### PATCH `/solicitacoes/{id}/recusar` — Gestor recusa (body opcional com `motivoRecusa`) — `200`
### PATCH `/solicitacoes/{id}/cancelar` — Responsável cancela — `200`
### GET `/solicitacoes` — `200` `List<SolicitacaoResponseDTO>`
### GET `/solicitacoes/{id}` — `200` `SolicitacaoResponseDTO` / `404`
### GET `/solicitacoes/responsavel/{responsavelId}` — `200` `List<SolicitacaoResponseDTO>`
### GET `/solicitacoes/gestor/{gestorId}` — `200` `List<SolicitacaoResponseDTO>`
### GET `/solicitacoes/gestor/{gestorId}/pendentes` — Fila compartilhada de pendências para gestores — `200` `List<SolicitacaoResponseDTO>`

Retorna todas as solicitações pendentes, independentemente do gestor escolhido no envio.
Qualquer gestor pode aprovar ou recusar uma solicitação da fila.
O aceite cria a notícia e a torna visível para leitores. Recusar ou cancelar mantém somente a solicitação, sem criar notícia.

---

## Resumo de Todas as Rotas

| Método | Rota | Descrição | Status |
|--------|------|-----------|--------|
| `POST` | `/usuarios` | Cria usuário (qualquer tipo) | `201` |
| `POST` | `/usuarios/login` | Login | `200` |
| `GET` | `/usuarios` | Lista todos os usuários | `200` |
| `GET` | `/usuarios/{id}` | Busca usuário por ID | `200` |
| `PUT` | `/usuarios/{id}` | Atualiza dados base | `200` |
| `DELETE` | `/usuarios/{id}` | Remove usuário | `204` |
| `POST` | `/colaboradores` | Cria colaborador | `201` |
| `GET` | `/colaboradores` | Lista colaboradores | `200` |
| `GET` | `/colaboradores/{id}` | Busca colaborador | `200` |
| `PUT` | `/colaboradores/{id}` | Atualiza colaborador | `200` |
| `DELETE` | `/colaboradores/{id}` | Remove colaborador | `204` |
| `POST` | `/gestores` | Cria gestor | `201` |
| `GET` | `/gestores` | Lista gestores | `200` |
| `GET` | `/gestores/{id}` | Busca gestor | `200` |
| `PUT` | `/gestores/{id}` | Atualiza gestor | `200` |
| `DELETE` | `/gestores/{id}` | Remove gestor | `204` |
| `POST` | `/responsaveis` | Cria responsável | `201` |
| `GET` | `/responsaveis` | Lista responsáveis | `200` |
| `GET` | `/responsaveis/{id}` | Busca responsável | `200` |
| `PUT` | `/responsaveis/{id}` | Atualiza responsável | `200` |
| `DELETE` | `/responsaveis/{id}` | Remove responsável | `204` |
| `POST` | `/empresas` | Cria empresa | `201` |
| `PATCH` | `/empresas/{id}/logo` | Upload logo | `200` |
| `PATCH` | `/empresas/adicionar-membro/{idmembro}/{idempresa}` | Adiciona membro | `200` |
| `GET` | `/empresas` | Lista empresas | `200` |
| `GET` | `/empresas/{id}` | Busca empresa | `200` |
| `PUT` | `/empresas/{id}` | Atualiza empresa | `200` |
| `DELETE` | `/empresas/{id}` | Remove empresa | `204` |
| `POST` | `/diretorias` | Cria diretoria | `201` |
| `GET` | `/diretorias` | Lista diretorias | `200` |
| `GET` | `/diretorias/{id}` | Busca diretoria | `200` |
| `PUT` | `/diretorias/{id}` | Atualiza diretoria | `200` |
| `DELETE` | `/diretorias/{id}` | Remove diretoria | `204` |
| `POST` | `/diretores` | Cria diretor | `201` |
| `GET` | `/diretores` | Lista diretores | `200` |
| `GET` | `/diretores/{id}` | Busca diretor | `200` |
| `PUT` | `/diretores/{id}` | Atualiza diretor | `200` |
| `DELETE` | `/diretores/{id}` | Remove diretor | `204` |
| `POST` | `/funcoes` | Cria função | `201` |
| `GET` | `/funcoes` | Lista funções | `200` |
| `GET` | `/funcoes/{id}` | Busca função | `200` |
| `PUT` | `/funcoes/{id}` | Atualiza função | `200` |
| `DELETE` | `/funcoes/{id}` | Remove função | `204` |
| `POST` | `/noticias` | Cria notícia | `201` |
| `PATCH` | `/noticias/{id}/imagem` | Upload imagem | `200` |
| `GET` | `/noticias` | Lista notícias | `200` |
| `GET` | `/noticias/{id}` | Busca notícia | `200` |
| `PUT` | `/noticias/{id}` | Atualiza notícia | `200` |
| `DELETE` | `/noticias/{id}` | Remove notícia | `204` |
| `POST` | `/comentarios` | Cria comentário | `201` |
| `GET` | `/comentarios` | Lista comentários | `200` |
| `GET` | `/comentarios/{id}` | Busca comentário | `200` |
| `GET` | `/comentarios/noticia/{noticiaId}` | Comentários raiz por notícia | `200` |
| `PUT` | `/comentarios/{id}` | Atualiza comentário | `200` |
| `DELETE` | `/comentarios/{id}` | Remove comentário | `204` |
| `POST` | `/solicitacoes` | Enviar solicitação | `201` |
| `PATCH` | `/solicitacoes/{id}/aceitar` | Gestor aceita | `200` |
| `PATCH` | `/solicitacoes/{id}/recusar` | Gestor recusa | `200` |
| `PATCH` | `/solicitacoes/{id}/cancelar` | Responsável cancela | `200` |
| `GET` | `/solicitacoes` | Lista todas | `200` |
| `GET` | `/solicitacoes/{id}` | Busca por ID | `200` |
| `GET` | `/solicitacoes/responsavel/{responsavelId}` | Por responsável | `200` |
| `GET` | `/solicitacoes/gestor/{gestorId}` | Por gestor | `200` |
| `GET` | `/solicitacoes/gestor/{gestorId}/pendentes` | Pendentes do gestor | `200` |
| `GET` | `/uploads/{subpasta}/{arquivo}` | Serve arquivo estático | `200` |
