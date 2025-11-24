# TicketNotify

*Sistema de Reserva de Ingressos com Notificação*

---

O projeto é uma API onde usuários podem se cadastrar, listar eventos disponíveis e "comprar" um ingresso.

## Stack

Stack utilizada nesse projeto

### Spring Security + JWT

    - Cenário: Login de usuário e proteção de rotas.

    - Desafio: Implementar o fluxo de **Refresh Token** com revogação via redis.

### PostgreSQL + JPA

    - Tabelas: *Users*, *Roles*, *Events*, *Tickets*.

### Bean Validation (Jakarta)

### Kafka --- !!!RABBITMQ!!!

    - Produtor: Quando a compra é salva no banco, será enviado um evento *TicketPurchasedEvent* para o tópico *ticket-confirmation*.

    - Consumidor: Um *@KadkaListener* que envia um e-mail para o remetente.

### GitHub Actions

    - Pipeline de testes para cada push e quebra do build em caso de falhas.

### Swagger

    - Documentação da API


### Docker

Ir3mos rodar em docker, com containers separados para rabbitmq, api, database e redis