# Fluxos da aplicação

## Links

https://medium.com/@choubeyayush4/securing-node-js-applications-with-jwt-refresh-tokens-and-redis-80ffbb54285a
https://www.luiztools.com.br/post/implementando-refresh-token-em-node-js/
(PUT - PATH) -> https://www.youtube.com/watch?v=tp498vX_OR4

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

## Criar conta user admin
(Apenas o admin pode criar ou dar permissões a um usuário normal)
- Um usuário admin pode ser criado de duas formas, diretamente no sistema, sem necessidade de passar pelo processo do registro de user normal.
- Ou poderá ser um usuário comum, que receberá a permissão de admin.

## Login
- A requisição chega no backend.
- Validaremos os dados via valiration beans no DTO da requisição.
- Verificamos se o usuário existe na aplicação e não está revogado.
- Verificamos as credenciais.
- Se tiver algum erro, volta erro ao usuário.
- Se estiver tudo verto, gera sessão de refresh token e salva no redis para validação e revogação.
- Volta sucesso ao usuário se tudo der certo.

## Registrar um novo evento
- Recebe a requisição de registrar o evento.
- Valida os dados da requisição no dto com validations beans.
- Se estiver tudo certo, vai para o service.
- Verifica se o usuário que está tentando criar o novo evento, tem permissão neecssária para isso.
- Se não tiver, volta erro ao usuário.
- Se tiver, tenta criar o novo evento.
- Se der erro, volta erro ao usuário.
- Se der tudo certo, retorna o objeto recem criado de evento e sucesso.

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