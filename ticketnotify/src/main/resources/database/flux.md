# Fluxos da aplicação

Explicação básica dos fluxos da aplicação

---

## Links

https://medium.com/@choubeyayush4/securing-node-js-applications-with-jwt-refresh-tokens-and-redis-80ffbb54285a
https://www.luiztools.com.br/post/implementando-refresh-token-em-node-js/
(PUT - PATH) -> https://www.youtube.com/watch?v=tp498vX_OR4

---

## Criar conta user normal

- **Request:** Chega no DTO com as validation beans.

1. Verifica se e-mail já existe no postgres.
2. Encripta senha com BCryptPasswordEncoder.
3. Salva usuário com status *PENDING*.

- **Redis:**

1. Gera token JWT de acesso e refresh.
2. Salva no Redis os tokens.
3. Response o usuário com os tokens e mensagem de verificação de email.

- **Kafka:**

1. É enviado uma mensagem/e-mail ao usuário, para ele validar seu e-mail.

- **Cron users inativos:**

1. Job para deletar usuários com status de Pending, criados a mais de 24h.

- **Confirmação criação de conta:**

1. Usuário clica no link do e-mail e é redirecionado para a página principal da aplicação.

... processos ...

?. Depois das validações de tokens no redis e comprovação de existência do usuário no db (não foi deletado pelo job), o status da conta é atualizado para *active* utilizando o MapStruct.

---

## Criar conta user admin

*Mesmo fluxo para outros tipos de usuários e diferentes roles! User admin apenas para exemplo*

### Fluxo 1

O primeiro admin é criado direto no db e pode criar novas contas admins sem passar pelo processo de validação de email. (Quase nunca utilizado)

### Fluxo 2

1. Admin logado chama endpoint (PATCH /users/{id}/promote) para promover um usuário a admin.
2. Backend verifica se quem chamou tem permissão de admin.
3. Verifica se o usuário a ser promovido existe e se não está revogado.
4. Verifica se o usuário já não é admin e se for, volta warn.
5. Atualiza a tabela de relacionamento user_roles, adicionando a Role admin ao usuário.

---

## Login (Auth JWT + Redis)

- **Request:** Chega email e senha.

1. Busca user no Postgres
2. Bate hash da senha (BCrypt)
3. Gera Access Token (JWT, 15m).
4. Gera Refresh Token (1d).

- **Redis**

1. Salva o refresh no Redis: Key=auth:{email}:{uuid}, Value=valid, TTL=7d

- Retorna Access Token no Body e Refresh Token (HttpOnly Cookie).

---

## Registrar um novo evento
- Recebe a requisição de registrar o evento.
- Valida os dados da requisição no dto com validations beans.
- Se estiver tudo certo, vai para o service.
- Verifica se o usuário que está tentando criar o novo evento, tem permissão neecssária para isso.
- Se não tiver, volta erro ao usuário.
- Se tiver, tenta criar o novo evento.
- Se der erro, volta erro ao usuário.
- Se der tudo certo, retorna o objeto recem criado de evento e sucesso.

- **Request:** DTO com dados do evento.
- **Validações**

    1. Verifica as permissões do usuário.
    2. Bean Validation (Data futura, preço positivo, etc...)

- **Segurança:** Verifica contexto SecurityContextHolder se user tem Role ORGANIZER ou ADMIN.

- **Service**

    1. Salva evento no Postgres

- **Response:** 201 Created com response do obj evento.

---

## Editar um evento
(Se for data/hora ou localização, envia um email com a modificação para todos que compraram ingressos para esse evento)
- Recebe a request e passa para o mapper.

## Excluir um evento
(Deve ser enviado um e-mail informando o cancelamento do evento e uma mensagem para o user solicitar o rembolso do valor)
- Passa pelo redis para validar credenciais.
- Requesição chega no backend.
- Verificamos se o evento existe e se não existir, gera erro.
- Verificamos se o usuário existe, se está válido e se ele tem a permissão necessária para excluir um evento (o user que criou ou adm podem apagar um evento), caso seja inválido, retorna erro.
- Tenta apagar o evento e se não conseguir, gera erro.
- Se conseguir, tenta pegar todos so usuários que tinham um ingresso referente a aquele evento.
- Em paralelo, volta sucesso ao usuário.
- Tenta enviar um email via kafka para todos os usuário, informando o cancelamento e passando link para suporte e rembolso. 

## Usuário comprar um ticket

## Usuário pedir rembolso do ticket recem comprado
(3 dias para desistência)

## Usuário criar uma nova regra

## Usuário editar uma regra existente

## Usuário remover uma regra existente