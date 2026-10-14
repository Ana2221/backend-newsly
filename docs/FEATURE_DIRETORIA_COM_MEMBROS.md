# Feature — Criação de Diretoria com Membros

## O que mudou no backend

### Arquivo novo — `DiretorDiretoriaRequestDTO`

Novo DTO interno que representa um membro a ser adicionado na diretoria no momento da criação.

```java
{
  colaboradorId: Long,  // obrigatório
  funcaoId: Long,       // obrigatório
  permissoes: []        // opcional — "LEITOR" ou "ESCRITOR"
}
```

---

### Modificado — `DiretoriaRequestDTO`

Adicionado campo `diretores` opcional.

**Antes:**
```json
{
  "criadaEm": "2024-01-01",
  "empresaId": 1
}
```

**Depois:**
```json
{
  "criadaEm": "2024-01-01",
  "empresaId": 1,
  "diretores": [
    {
      "colaboradorId": 5,
      "funcaoId": 1,
      "permissoes": ["ESCRITOR"]
    }
  ]
}
```

O campo `diretores` é **opcional**. Se não for enviado, a diretoria é criada sem membros — comportamento anterior mantido.

---

### Modificado — `DiretoriaService.criar()`

Após salvar a diretoria, agora itera a lista de `diretores` e cria cada `Diretor` automaticamente.

Validações feitas internamente:
- `404` se o colaborador não existir
- `404` se a função não existir
- `409` se o colaborador já for diretor em outra diretoria

---

## O que NÃO mudou

- O endpoint continua sendo `POST /diretorias`
- O response continua sendo `DiretoriaResponseDTO`
- O `PUT /diretorias/{id}` não aceita a lista de diretores — para adicionar membros depois use `POST /diretores`
- Todas as outras rotas de diretoria permanecem iguais

---

## Response — sem alteração

O `DiretoriaResponseDTO` já retornava a lista de diretores. Agora ela vem preenchida logo na criação:

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
    }
  ]
}
```

---

## O que o frontend precisa alterar

### Formulário de criação de diretoria

Adicionar um campo (ou seção) para a lista de membros antes de submeter o POST.

**Antes — body enviado:**
```json
{
  "criadaEm": "2024-01-01",
  "empresaId": 1
}
```

**Depois — body com membros:**
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

---

### Dados necessários para montar o formulário

O frontend precisa buscar previamente:

**Colaboradores disponíveis:**
```
GET /colaboradores
```
Usar `id` e `nome` para montar o select de colaborador.

**Funções disponíveis:**
```
GET /funcoes
```
Usar `id` e `nome` para montar o select de função/cargo.

**Permissões disponíveis (fixas, não precisam de request):**
```
["LEITOR", "ESCRITOR"]
```

---

### Exemplo de estrutura do formulário

```
┌─────────────────────────────────────┐
│ Criar Diretoria                     │
├─────────────────────────────────────┤
│ Data de criação: [____-__-__]       │
│ Empresa: [select empresa]           │
├─────────────────────────────────────┤
│ Membros (opcional)                  │
│                                     │
│ + Adicionar membro                  │
│   ├─ Colaborador: [select]          │
│   ├─ Função: [select]               │
│   └─ Permissões: [LEITOR] [ESCRITOR]│
│                                     │
│ + Adicionar membro                  │
│   └─ ...                            │
├─────────────────────────────────────┤
│ [Criar]                             │
└─────────────────────────────────────┘
```

---

### Erros a tratar no frontend

| Código | Mensagem do backend | Como exibir |
|--------|--------------------|----|
| `404` | `Colaborador não encontrado com id: X` | Erro no campo do membro |
| `404` | `Função não encontrada com id: X` | Erro no campo do membro |
| `409` | `O colaborador com id X já é diretor.` | Erro no campo do membro |
| `400` | `ID do colaborador é obrigatório` | Validação inline |
| `400` | `ID da função é obrigatório` | Validação inline |

---

## Fluxo completo (se quiser adicionar membros depois)

Se o frontend não quiser enviar membros no momento da criação, ainda é possível adicioná-los depois:

```
1. POST /diretorias          → cria a diretoria sem membros
2. POST /diretores           → adiciona um membro por vez
   {
     "colaboradorId": 5,
     "funcaoId": 1,
     "diretoriaId": 1,        ← ID retornado no passo 1
     "permissoes": ["ESCRITOR"]
   }
```

---

## Resumo dos arquivos alterados no backend

| Arquivo | Tipo |
|---------|------|
| `dto/request/DiretorDiretoriaRequestDTO.java` | **Novo** |
| `dto/request/DiretoriaRequestDTO.java` | Modificado — campo `diretores` adicionado |
| `service/DiretoriaService.java` | Modificado — criação em cascata de diretores |
