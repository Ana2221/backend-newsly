# Feature — Solicitação de Publicação de Notícia

## Contexto

Esta feature permite que um **Responsável** solicite a publicação de uma notícia para um **Gestor** específico.
O Gestor pode aceitar ou recusar. O Responsável pode cancelar enquanto a solicitação estiver pendente.

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
- O `Responsavel` escolhe um `Gestor` específico ao enviar
- Não é possível enviar solicitação duplicada: mesma `noticiaId` + mesmo `gestorId` com status `PENDENTE`
- Somente o `Gestor` pode aceitar ou recusar
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
  "gestorId": 2,
  "noticiaId": 3
}
```

| Campo | Tipo | Obrigatório |
|-------|------|-------------|
| `responsavelId` | `Long` | ✅ |
| `gestorId` | `Long` | ✅ |
| `noticiaId` | `Long` | ✅ |

**Response `201`:**
```json
{
  "id": 1,
  "responsavelId": 1,
  "responsavelNome": "Maria Costa",
  "gestorId": 2,
  "gestorNome": "Carlos Lima",
  "noticiaId": 3,
  "noticiaTitulo": "Nova parceria firmada",
  "status": "PENDENTE",
  "criadaEm": "2026-08-17T10:00:00"
}
```

**Erros:**
- `404` — Responsável, Gestor ou Notícia não encontrados
- `409` — Já existe solicitação `PENDENTE` para esta notícia com este gestor

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

### Listar Pendentes do Gestor
```
GET /solicitacoes/gestor/{gestorId}/pendentes
```

Retorna somente as solicitações com status `PENDENTE` de um gestor.
Use este endpoint para exibir a fila de aprovações do gestor.

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
| `noticiaId` | `Long` | ✅ | ID da notícia |
| `noticiaTitulo` | `String` | ✅ | Título da notícia |
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
