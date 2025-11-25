package com.ticketnotify.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketnotify.entity.User;
import com.ticketnotify.exception.AppException;
import com.ticketnotify.exception.ErrorCode;
import com.ticketnotify.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        String passwordHash = new BCryptPasswordEncoder().encode(user.getPassword());
        if (passwordHash.isEmpty() || passwordHash.isBlank()) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.FAILED_DEPENDENCY);
        }

        user.setPassword(passwordHash);

        User createdUser = userRepository.save(user);

        return createdUser;

    };

}


/*
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
*/