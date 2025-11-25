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

- **Request:** PATCH /events/{id} com os campos parciais.

- **Service**

    1. Recupera evento antigo do banco (oldEvent).
    2. Usa MapStruct para mesclar DTO com Entity.
    3. Detecta mudanças críticas driff.

- **Persistencia:** Salva com @DynamicUpdate

- Se notifyUser == true, publica mensagem. Consumer pega os e-mails na tabela de tickets referentes a aquele evento e dispara aviso.

---

## Excluir um evento

Não deleta os dados no banco caso já exista ingressos vendidos, apenas invalida.

- **Fluxo:**

    1. Verifica as permissões do usuário.
    2. Muda status do evento para canceled.
    3. Muda status de todos os tickets vinculados para canceled_by_event.

- **RabbitMQ:** Dispara evento

    1. Consumer envia e-mail de aviso.
    2. Consumer aciona módulo de reembolso para estornar pagamentos.

---

## Usuário comprar um ticket

- **Request:** POST /events/{id}/buy

- **Service:**

    1. Verifica se Evento está ACTIVE e data é futura.
    2. **Controle de Concorrência:** Verifica (capacidade - tickets_vendidos) > 0. Utilizando query atômica no db.

- **Pagamento:**

    1. Chama serviço de pagamento (mock + kafka). Se sucesso -> status: paid.
    2. Se falhar -> Atualiza ticket para payment_failed e decrementa contador de vendidos do evento.

- **RabbitMQ:**

    1. Se paid, gera (NF e E-Ticket) e publica para envio em e-mail. 

---

## Usuário pedir rembolso

- **Request:** POST /tickets/{id}/refund

- **Validação:**

    1. Busca Ticket. Verifique se status é paid.
    2. Verifica regra de tempo { if (ticket.createdAt.plusDays(3).isAfter(now)) -> OK } se não, erro de prazo expirado.

- **Service:**

    1. Atualiza Ticket para refund_requested / refunded.
    2. Atualiza contador de vagas do evento devolvendo o ingresso.

- **Kafka:** Da mesma forma da compra, notifica o sistema financeiro.

- **RabbitMQ:** Envia e-mail de confirmação.

---

## Usuário criar uma nova regra

## Usuário editar uma regra existente

## Usuário remover uma regra existente