# Feature — Solicitação de Publicação de Notícia

## Contexto

Esta feature permite que um **Responsável** envie os dados completos de uma notícia para a fila compartilhada dos **Gestores**.
A notícia só é criada após aprovação. Qualquer gestor pode aceitar ou recusar. O Responsável pode cancelar enquanto a solicitação estiver pendente.

---

## Fluxo de Estados

```
PENDENTE ──► ACEITA
         ──► RECUSADA  (Gestor pode informar motivo)
         ──► CANCELADA (Responsável cancela)
```

Somente solicitações com status `PENDENTE` podem ser modificadas.

---

## Regras de Negócio

- Quem envia é sempre um `Responsavel`
- A solicitação vai para a fila compartilhada; o responsável não escolhe nem informa um gestor
- A submissão guarda título, conteúdo, data e empresa na solicitação; antes do aceite, não existe registro de notícia
- Somente o `Gestor` pode aceitar ou recusar
- Aceitar cria e publica a notícia
- Recusar ou cancelar encerra a solicitação sem criar a notícia
- Somente o `Responsavel` pode cancelar
- Motivo da recusa é opcional

---

## Endpoints

### Enviar Solicitação
```
POST /solicitacoes
```

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

| Campo | Tipo | Obrigatório |
|-------|------|-------------|
| `responsavelId` | `Long` | ✅ |
| `titulo` | `String` | ✅ |
| `conteudo` | `String` | ✅ |
| `publicadaEm` | `LocalDateTime` | ✅ |
| `empresaId` | `Long` | ✅ |

**Response `201`:**
```json
{
  "id": 1,
  "responsavelId": 1,
  "responsavelNome": "Maria Costa",
  "gestorId": null,
  "gestorNome": null,
  "noticiaId": null,
  "noticiaTitulo": "Nova parceria firmada",
  "noticiaConteudo": "A empresa firmou uma nova parceria...",
  "noticiaPublicadaEm": "2026-08-17T10:00:00",
  "empresaId": 1,
  "empresaNome": "Nite Jr",
  "status": "PENDENTE",
  "criadaEm": "2026-08-17T10:00:00"
}
```

**Erros:**
- `404` — Responsável, Gestor ou Empresa não encontrados

Após o aceite, `noticiaId` será preenchido com a notícia criada. Se recusada ou cancelada, `noticiaId` permanece `null` e nenhuma notícia é criada.
O `gestorId` não é enviado na submissão; ele é preenchido com o gestor que aprovar ou recusar.

---

### Aceitar Solicitação (Gestor)
```
PATCH /solicitacoes/{id}/aceitar
```

Sem body.

**Response `200`:**
```json
{
  "id": 1,
  "status": "ACEITA",
  "respondidaEm": "2026-08-17T11:00:00"
}
```

**Erros:**
- `404` — Solicitação não encontrada
- `409` — Solicitação não está com status `PENDENTE`

---

### Recusar Solicitação (Gestor)
```
PATCH /solicitacoes/{id}/recusar
```

**Request Body (opcional):**
```json
{
  "motivoRecusa": "Conteúdo fora do padrão da empresa."
}
```

**Response `200`:**
```json
{
  "id": 1,
  "status": "RECUSADA",
  "respondidaEm": "2026-08-17T11:00:00",
  "motivoRecusa": "Conteúdo fora do padrão da empresa."
}
```

**Erros:**
- `404` — Solicitação não encontrada
- `409` — Solicitação não está com status `PENDENTE`

---

### Cancelar Solicitação (Responsável)
```
PATCH /solicitacoes/{id}/cancelar
```

Sem body.

**Response `200`:**
```json
{
  "id": 1,
  "status": "CANCELADA",
  "respondidaEm": "2026-08-17T11:30:00"
}
```

**Erros:**
- `404` — Solicitação não encontrada
- `409` — Solicitação não está com status `PENDENTE`

---

### Buscar por ID
```
GET /solicitacoes/{id}
```

**Response `200`:** `SolicitacaoResponseDTO` / `404`

---

### Listar Todas
```
GET /solicitacoes
```

**Response `200`:** `List<SolicitacaoResponseDTO>`

---

### Listar por Responsável
```
GET /solicitacoes/responsavel/{responsavelId}
```

Retorna todas as solicitações enviadas por um responsável (qualquer status).

**Response `200`:** `List<SolicitacaoResponseDTO>`

---

### Listar por Gestor
```
GET /solicitacoes/gestor/{gestorId}
```

Retorna todas as solicitações recebidas por um gestor (qualquer status).

**Response `200`:** `List<SolicitacaoResponseDTO>`

---

### Listar Fila de Pendências
```
GET /solicitacoes/gestor/{gestorId}/pendentes
```

Retorna todas as solicitações com status `PENDENTE`, independentemente do gestor indicado no envio.
O `gestorId` da rota deve ser o ID do gestor autenticado (ou a chamada deve ser feita por um administrador).
Qualquer gestor pode aprovar ou recusar uma solicitação da fila.

**Response `200`:** `List<SolicitacaoResponseDTO>`

---

## SolicitacaoResponseDTO — Campos Completos

| Campo | Tipo | Sempre presente | Descrição |
|-------|------|-----------------|-----------|
| `id` | `Long` | ✅ | ID da solicitação |
| `responsavelId` | `Long` | ✅ | ID do responsável |
| `responsavelNome` | `String` | ✅ | Nome do responsável |
| `gestorId` | `Long` | ✅ | ID do gestor |
| `gestorNome` | `String` | ✅ | Nome do gestor |
| `noticiaId` | `Long` | ❌ | ID da notícia; preenchido somente após aprovação |
| `noticiaTitulo` | `String` | ✅ | Título submetido |
| `noticiaConteudo` | `String` | ✅ | Conteúdo submetido |
| `noticiaPublicadaEm` | `LocalDateTime` | ✅ | Data de publicação submetida |
| `empresaId` | `Long` | ✅ | Empresa da notícia |
| `empresaNome` | `String` | ✅ | Nome da empresa |
| `status` | `StatusSolicitacao` | ✅ | `PENDENTE`, `ACEITA`, `RECUSADA`, `CANCELADA` |
| `criadaEm` | `LocalDateTime` | ✅ | Data/hora do envio |
| `respondidaEm` | `LocalDateTime` | ❌ | Preenchido após aceitar, recusar ou cancelar |
| `motivoRecusa` | `String` | ❌ | Preenchido somente quando `RECUSADA` |

> Campos com `❌` são omitidos do JSON quando `null` (`@JsonInclude(NON_NULL)`).

---

## Resumo das Rotas

| Método | Rota | Descrição | Status |
|--------|------|-----------|--------|
| `POST` | `/solicitacoes` | Enviar solicitação | `201` |
| `PATCH` | `/solicitacoes/{id}/aceitar` | Gestor aceita | `200` |
| `PATCH` | `/solicitacoes/{id}/recusar` | Gestor recusa | `200` |
| `PATCH` | `/solicitacoes/{id}/cancelar` | Responsável cancela | `200` |
| `GET` | `/solicitacoes` | Lista todas | `200` |
| `GET` | `/solicitacoes/{id}` | Busca por ID | `200` |
| `GET` | `/solicitacoes/responsavel/{responsavelId}` | Por responsável | `200` |
| `GET` | `/solicitacoes/gestor/{gestorId}` | Por gestor | `200` |
| `GET` | `/solicitacoes/gestor/{gestorId}/pendentes` | Pendentes do gestor | `200` |
