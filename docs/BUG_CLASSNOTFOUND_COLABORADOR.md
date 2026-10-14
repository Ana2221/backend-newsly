# Bug — ClassNotFoundException: ColaboradorRequestDTO

## Erro

```
java.lang.NoClassDefFoundError: ColaboradorRequestDTO
java.lang.ClassNotFoundException: ColaboradorRequestDTO
```

Ocorre na inicialização do Spring ao tentar criar o bean `colaboradorController`.

---

## Causa

O Maven está usando arquivos `.class` desatualizados na pasta `target/`.
Alguma compilação anterior gerou um `.class` que referencia `ColaboradorRequestDTO`
sem o pacote completo (`br.nite.newsly.dto.request`).

O arquivo fonte `ColaboradorRequestDTO.java` está correto — o problema é cache de compilação.

---

## Solução

Limpar o `target/` e recompilar do zero.

### No terminal do projeto

```bash
mvn clean compile
```

O `clean` remove a pasta `target/` inteira antes de compilar.

### Se ainda falhar

```bash
mvn clean compile -U
```

O `-U` força o Maven a re-baixar as dependências caso haja inconsistência no repositório local.

### Se o erro persistir

Deletar manualmente a pasta `target/` e compilar:

**Windows (PowerShell):**
```powershell
Remove-Item -Recurse -Force target
mvn compile
```

---

## Prevenção

Sempre usar `mvn clean` antes de `mvn spring-boot:run` após alterações estruturais
(mudança de pacote, renomeação de classes, adição de novos DTOs).

Ou configurar o IntelliJ/VS Code para usar `mvn clean spring-boot:run` em vez de `mvn spring-boot:run`.
