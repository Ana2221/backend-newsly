# Rotas — Diretoria

> Base URL: `http://localhost:8080`
> Todas as rotas usam `Content-Type: application/json`

---

## DTOs utilizados

### Request — `DiretoriaRequestDTO`

Usado em `POST /diretorias` e `PUT /diretorias/{id}`.

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `criadaEm` | `LocalDate` (`YYYY-MM-DD`) | ✅ | Data de criação da diretoria |
| `empresaId` | `Long` | ✅ | ID da empresa à qual a diretoria pertence |
| `diretores` | `List<DiretorDiretoriaRequestDTO>` | ❌ | Membros a serem criados junto com a diretoria |

> O campo `diretores` é **opcional**. Se não enviado, a diretoria é criada sem membros.
> No `PUT` o campo `diretores` é **ignorado** — use `POST /diretores` para adicionar membros depois.

---

### Request — `DiretorDiretoriaRequestDTO`

Usado dentro de `DiretoriaRequestDTO.diretores[]` ao criar uma diretoria com membros.

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `colaboradorId` | `Long` | ✅ | ID do colaborador que será diretor |
| `funcaoId` | `Long` | ✅ | ID da função/cargo do diretor |
| `permissoes` | `List<Permissao>` | ❌ | `"LEITOR"` e/ou `"ESCRITOR"` |

---

### Response — `DiretoriaResponseDTO`

Retornado em todas as rotas de diretoria.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | `Long` | ID da diretoria |
| `criadaEm` | `LocalDate` | Data de criação |
| `empresaId` | `Long` | ID da empresa |
| `empresaNome` | `String` | Nome da empresa |
| `diretores` | `List<DiretorResumoResponseDTO>` | Lista de membros da diretoria |

---

### Response — `DiretorResumoResponseDTO`

Usado dentro de `DiretoriaResponseDTO.diretores[]`.
Versão resumida do diretor — não inclui `diretoria` para evitar recursão circular.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | `Long` | ID do diretor (mesmo ID do colaborador) |
| `colaboradorId` | `Long` | ID do colaborador |
| `colaboradorNome` | `String` | Nome do colaborador |
| `funcao` | `FuncaoResponseDTO` | Função/cargo do diretor |
| `permissoes` | `List<Permissao>` | `LEITOR`, `ESCRITOR` |

---

### Response — `FuncaoResponseDTO`

Usado dentro de `DiretorResumoResponseDTO.funcao`.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | `Long` | ID da função |
| `nome` | `String` | Nome da função (ex: "Presidente") |
| `descricao` | `String` | Descrição da função |

---

## Rotas

### POST `/diretorias`
Cria uma nova diretoria. Opcionalmente cria os diretores no mesmo request.

**Request — sem membros:**
```json
{
  "criadaEm": "2024-01-01",
  "empresaId": 1
}
```

**Request — com membros:**
```json
{
  "criadaEm": "2024-01-01",
  "empresaId": 1,
  "diretores": [
    {
      "colaboradorId": 5,
      "funcaoId": 1,
      "permissoes": ["ESCRITOR"]
    },
    {
      "colaboradorId": 6,
      "funcaoId": 2,
      "permissoes": ["LEITOR"]
    }
  ]
}
```

**Response `201`:**
```json
{
  "id": 1,
  "criadaEm": "2024-01-01",
  "empresaId": 1,
  "empresaNome": "Nite Jr",
  "diretores": [
    {
      "id": 5,
      "colaboradorId": 5,
      "colaboradorNome": "Ana Silva",
      "funcao": {
        "id": 1,
        "nome": "Presidente",
        "descricao": "Responsável pela gestão geral"
      },
      "permissoes": ["ESCRITOR"]
    },
    {
      "id": 6,
      "colaboradorId": 6,
      "colaboradorNome": "Carlos Lima",
      "funcao": {
        "id": 2,
        "nome": "Diretor Financeiro",
        "descricao": "Responsável pelo financeiro"
      },
      "permissoes": ["LEITOR"]
    }
  ]
}
```

**Erros:**
| Código | Situação |
|--------|----------|
| `400` | `criadaEm` ou `empresaId` ausentes |
| `400` | `colaboradorId` ou `funcaoId` ausentes em um membro |
| `404` | Empresa não encontrada |
| `404` | Colaborador não encontrado |
| `404` | Função não encontrada |
| `409` | Colaborador já é diretor em outra diretoria |

---

### GET `/diretorias`
Lista todas as diretorias.

**Response `200`:** `List<DiretoriaResponseDTO>`

```json
[
  {
    "id": 1,
    "criadaEm": "2024-01-01",
    "empresaId": 1,
    "empresaNome": "Nite Jr",
    "diretores": [ ... ]
  }
]
```

---

### GET `/diretorias/{id}`
Busca uma diretoria pelo ID.

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `Long` (path) | ID da diretoria |

**Response `200`:** `DiretoriaResponseDTO`
**Response `404`:** Diretoria não encontrada

---

### PUT `/diretorias/{id}`
Atualiza `criadaEm` e/ou `empresaId` de uma diretoria existente.

> O campo `diretores` é **ignorado** nesta rota.
> Para adicionar membros use `POST /diretores`.

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `Long` (path) | ID da diretoria |

**Request:**
```json
{
  "criadaEm": "2024-06-01",
  "empresaId": 1
}
```

**Response `200`:** `DiretoriaResponseDTO`
**Response `404`:** Diretoria não encontrada

---

### DELETE `/diretorias/{id}`
Remove uma diretoria e todos os seus diretores (cascade).

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `Long` (path) | ID da diretoria |

**Response `204`:** Removido com sucesso
**Response `404`:** Diretoria não encontrada

---

## Fluxo completo de dados

```
DiretoriaRequestDTO
    ├── criadaEm         → LocalDate
    ├── empresaId        → busca EmpresaJr no banco
    └── diretores[]
            ├── colaboradorId → busca Colaborador no banco
            ├── funcaoId      → busca Funcao no banco
            └── permissoes    → List<Permissao>

DiretoriaResponseDTO
    ├── id
    ├── criadaEm
    ├── empresaId
    ├── empresaNome
    └── diretores[]  (DiretorResumoResponseDTO)
            ├── id
            ├── colaboradorId
            ├── colaboradorNome
            ├── funcao  (FuncaoResponseDTO)
            │       ├── id
            │       ├── nome
            │       └── descricao
            └── permissoes
```

---

## Dados necessários antes de criar uma diretoria

O frontend precisa buscar previamente:

```
GET /empresas       → para escolher a empresa (usa id e nome)
GET /colaboradores  → para escolher os membros (usa id e nome)
GET /funcoes        → para escolher os cargos (usa id e nome)
```

Permissões disponíveis (fixas):
```json
["LEITOR", "ESCRITOR"]
```
