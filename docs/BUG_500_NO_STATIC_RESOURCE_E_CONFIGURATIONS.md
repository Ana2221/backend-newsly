# Bug — 500 No Static Resource + Configurations com campo logo

## Bugs encontrados

### Bug 1 — `500 No static resource .`

**Erro no log:**
```
NoResourceFoundException: No static resource .
[500 INTERNAL SERVER ERROR] Erro inesperado
```

**Causa:**
O frontend (ou alguma ferramenta) estava fazendo `GET /` (rota raiz).
O Spring tentava servir isso como recurso estático e não encontrava.
O `GlobalExceptionHandler` capturava o `NoResourceFoundException` pelo handler genérico
de `Exception`, logando como 500 em vez de 404.

**Correção — `GlobalExceptionHandler.java`:**
Adicionado handler específico para `NoResourceFoundException`:

```java
@ExceptionHandler(NoResourceFoundException.class)
public ResponseEntity<ErrorResponse> handleNoStaticResource(NoResourceFoundException ex) {
    log.warn("[404 NOT FOUND] Recurso estático não encontrado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse.of(404, "Not Found", "Recurso não encontrado."));
}
```

Agora retorna `404` corretamente em vez de poluir o log com falso `500`.

---

### Bug 2 — `Configurations` com campo `logo` binário e `unique = true` no `color`

**Causa:**
A entidade `Configurations` tinha:
- `byte[] logo` com `@Lob` e `@Column(columnDefinition = "LONGBLOB")` — campo binário no banco
- `@Column(name = "color", unique = true)` — `unique` em campo de `@Embeddable` causa comportamento inesperado

O campo `logo` era um resquício de uma abordagem anterior de armazenar imagens diretamente
no banco. A estratégia adotada foi armazenamento em disco via `ArmazenamentoService`,
com o caminho salvo no campo `logoUrl` da entidade `EmpresaJr`.
Manter o `logo` binário gerava colunas desnecessárias e conflito no schema.

**Correção — `Configurations.java`:**

```java
// ANTES
@Embeddable
public class Configurations {
    @Column(name = "color", length = 20, unique = true)
    private String color;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] logo;
}

// DEPOIS
@Embeddable
public class Configurations {
    @Column(name = "color", length = 20)
    private String color;
}
```

---

## Arquivos alterados

| Arquivo | Alteração |
|---------|-----------|
| `exception/GlobalExceptionHandler.java` | Adicionado handler para `NoResourceFoundException` → retorna `404` |
| `entity/Configurations.java` | Removido campo `logo` binário e `unique = true` do `color` |

---

## Impacto no banco

O Hibernate vai tentar remover a coluna `logo` da tabela `empresa_jr` na próxima
inicialização com `ddl-auto=update`. Porém o `update` **não remove colunas** —
apenas adiciona novas. A coluna `logo` permanecerá no banco sem uso.

Se quiser remover manualmente:
```sql
ALTER TABLE empresa_jr DROP COLUMN logo;
ALTER TABLE empresa_jr DROP COLUMN color; -- se unique estava criado como constraint separada
ALTER TABLE empresa_jr ADD COLUMN color VARCHAR(20);
```
