# Segurança do backend — guia para o frontend

Este guia descreve como o frontend deve autenticar usuários e consumir as rotas protegidas do Newsly. O backend é a autoridade final de acesso: ocultar botões ou verificar permissões na interface não substitui os controles da API.

## 1. Login e resposta

Faça login em `POST /usuarios/login`, sem token:

```http
POST /usuarios/login
Content-Type: application/json
```

```json
{
  "email": "pessoa@exemplo.com",
  "senha": "senha-informada"
}
```

O sucesso (`200`) retorna:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresIn": 1800,
  "usuario": {
    "id": 12,
    "nome": "Pessoa",
    "email": "pessoa@exemplo.com",
    "tipo": "RESPONSAVEL",
    "permissoes": ["LEITOR", "ESCRITOR"]
  }
}
```

`expiresIn` está em segundos; o padrão atual é 1.800 segundos (30 minutos). Credenciais inexistentes ou incorretas retornam `401`, sem diferenciar se o e-mail existe.

## 2. Envio do token

Inclua o token em todas as chamadas protegidas:

```http
Authorization: Bearer <accessToken>
```

Exemplo com `fetch`:

```js
async function apiRequest(path, { token, headers, ...options } = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...(options.body && !(options.body instanceof FormData)
        ? { "Content-Type": "application/json" }
        : {}),
      ...headers,
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  });

  if (response.status === 204) return undefined;

  const contentType = response.headers.get("content-type") ?? "";
  const body = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const error = new Error(body?.message ?? `Erro HTTP ${response.status}`);
    error.status = response.status;
    error.body = body;
    throw error;
  }

  return body;
}
```

Para chamadas sem autenticação, como login e cadastro público, não é necessário fornecer `token`. Para uploads, envie `FormData` sem definir manualmente `Content-Type`; o navegador precisa incluir o boundary multipart. O upload continua sujeito à autorização da rota.

## 3. Armazenamento e ciclo de vida

- Prefira manter o access token em memória enquanto a aplicação está aberta.
- O backend entrega um token Bearer no corpo da resposta; não há cookie `HttpOnly`, endpoint de refresh ou endpoint de logout/revogação no contrato atual.
- Evite guardar o token em `localStorage` ou `sessionStorage` sem uma decisão explícita de segurança do produto: scripts executados na origem da aplicação podem lê-lo.
- Nunca coloque token ou senha em URL, logs, mensagens de erro, analytics ou ferramentas de monitoramento.
- Use HTTPS em produção e não persista a senha após o login.
- Como não há refresh token, quando o JWT expirar a API retorna `401`; limpe a sessão local e peça novo login. Não faça tentativas automáticas repetidas.
- Após logout na interface, descarte o token mantido pelo frontend. Como o backend não oferece revogação, um token já copiado permanece válido até expirar.

## 4. Papéis e permissões

O campo `usuario.tipo` e a lista `usuario.permissoes` podem orientar a interface, mas não devem ser considerados credenciais. O backend revalida o JWT e aplica as regras abaixo.

| Perfil/permissão | Acesso |
|---|---|
| `ADMIN` | Gerencia usuários, perfis, EJs e dados globais; acesso administrativo às demais áreas e exclusões. |
| `LEITOR` | Consulta notícias, comentários, empresas, colaboradores, gestores, responsáveis, diretorias, diretores e funções. |
| `ESCRITOR` | Consulta e cria/atualiza/exclui notícias e comentários, conforme a política do perfil autenticado. O backend também concede leitura quando há `ESCRITOR`. |
| `GESTOR` | Consulta solicitações associadas ao próprio gestor e aceita/recusa as próprias solicitações recebidas. |
| `RESPONSAVEL` | Consulta, cria e edita dados da própria EJ; não pode excluir registros nem consultar/alterar outras EJs. Também cria solicitações em seu próprio nome, consulta as próprias e cancela as próprias enquanto pendentes. |
| `USUARIO` sem permissões | Pode consultar o próprio cadastro e fazer login, mas não recebe acesso geral aos recursos da API. |

As permissões do diretor são incluídas no token quando ele faz login. Para refletir mudanças de tipo/permissão, o usuário deve autenticar novamente; tokens emitidos anteriormente conservam as autoridades até expirarem.

## 5. Matriz de acesso por área

| Rotas | Consulta | Criação/alteração/exclusão |
|---|---|---|
| `POST /usuarios/login` | Pública | — |
| `POST /usuarios` | Pública somente para `tipo: "USUARIO"` | Tipos `COLABORADOR`, `GESTOR` e `RESPONSAVEL` exigem `ADMIN`. |
| `GET /usuarios/{id}` | Próprio usuário ou `ADMIN` | — |
| `GET /usuarios` | `ADMIN` | — |
| `PUT/DELETE /usuarios/{id}` | — | `ADMIN` |
| `/colaboradores` | `LEITOR` ou `ADMIN`; responsável só consulta a própria EJ | `ADMIN` ou responsável da EJ para criar/editar dentro da própria EJ |
| `/gestores` | `LEITOR`, `RESPONSAVEL` ou `ADMIN` | `ADMIN` |
| `/responsaveis` | `LEITOR` ou `ADMIN`; responsável só consulta o próprio perfil | `ADMIN` |
| `/empresas` | `LEITOR` ou `ADMIN`; responsável só consulta a própria EJ | `ADMIN` cria; `ADMIN` ou responsável da EJ edita e envia logo/adiciona membros na própria EJ |
| `/diretorias`, `/diretores`, `/funcoes` | `LEITOR` ou `ADMIN`; responsável só consulta itens da própria EJ | `ADMIN` ou responsável da EJ cria/edita; `DELETE` somente ADMIN |
| `/noticias`, `/comentarios` | `LEITOR` ou `ADMIN`; responsável só consulta conteúdo da própria EJ | `ESCRITOR`, `ADMIN` ou responsável da EJ cria/edita dentro da própria EJ; `DELETE` não é permitido ao responsável |
| `POST /solicitacoes` | — | `RESPONSAVEL` ou `ADMIN`; para `RESPONSAVEL`, o backend usa o ID do token. |
| Aceitar/recusar solicitação | — | `GESTOR` ou `ADMIN`; gestor só pode responder à solicitação dirigida a ele. |
| Cancelar solicitação | — | `RESPONSAVEL` ou `ADMIN`; responsável só pode cancelar a própria solicitação. |
| Consultar solicitação por ID | — | `ADMIN`, ou gestor/responsável associado ao registro. |
| Listar solicitações de um gestor/responsável | — | `ADMIN`, ou o próprio gestor/responsável indicado na rota. |
| `GET /solicitacoes` | `ADMIN` | — |
| `GET /uploads/**` | Pública | — |

Para upload de imagem de notícia, aplica-se a mesma regra de escrita da notícia (`ESCRITOR`, `ADMIN` ou responsável da EJ proprietária). O upload de logo pode ser feito por `ADMIN` ou pelo responsável da EJ proprietária.

Os endpoints de função agora associam o cargo a uma EJ pelo campo `empresaId`. Para responsável, o backend ignora qualquer empresa informada e usa a empresa vinculada ao token. Funções antigas sem empresa (`empresaId: null`) permanecem globais e só podem ser consultadas/alteradas por perfis autorizados não responsáveis; um ADMIN pode associá-las pelo `PUT /funcoes/{id}`.

**Observação sobre imagens:** `GET /uploads/**` continua público para permitir renderização direta em `<img>`. O isolamento do responsável aplica-se aos registros e operações da API; arquivos cujo endereço seja conhecido continuam acessíveis sem token.

Cadastro público de conta `USUARIO` não cria automaticamente uma sessão nem concede permissões de leitura/escrita. Não tente cadastrar um perfil `ADMIN` pelo frontend: a conta administrativa é provisionada no backend por configuração operacional.

## 6. Tratamento de respostas de segurança

As respostas de erro usam o formato padrão da API:

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Acesso não permitido.",
  "timestamp": "2026-10-30T12:00:00"
}
```

| Status | Significado | Ação recomendada na interface |
|---|---|---|
| `401 Unauthorized` | Token ausente, inválido ou expirado; no login, credenciais inválidas. | Em rota protegida, limpar a sessão e encaminhar ao login. No login, mostrar erro genérico de credenciais. |
| `403 Forbidden` | Usuário autenticado, mas sem papel/permissão ou sem relação com o recurso. | Informar falta de acesso; não repetir a chamada automaticamente. |

Uma resposta `401` não deve ser tratada como uma lista vazia nem como sucesso. Um `403` também não deve provocar logout automático: a sessão pode estar válida para outras áreas.

## 7. Configuração e CORS

Configure a URL base por ambiente no frontend. Em desenvolvimento, use o endereço local do backend; em produção, use o host publicado. O backend aceita os métodos usados pela API e não exige credenciais de cookie CORS para este fluxo Bearer.

Se o navegador bloquear uma chamada, diferencie erro de CORS/rede de `401` e `403` observando a aba Network. Não tente contornar CORS com proxy público ou desativando as proteções do navegador.

## 8. Referências

- [Contrato completo da API](./API.md)
- [Fluxo de solicitações](./FEATURE_SOLICITACAO.md)
- [Fluxo de upload de imagens](./FEATURE_UPLOAD_IMAGEM.md)
