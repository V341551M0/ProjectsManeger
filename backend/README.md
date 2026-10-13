# Backend

## Configuração

Java 21 e Maven são necessários. O Spring Boot conecta por padrão a
`jdbc:postgresql://localhost:5432/projectsmaneger`; use `SPRING_DATASOURCE_URL`
para informar outra URL. `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET` são
necessários. O segredo JWT deve ter ao menos 256 bits, como exige o algoritmo
HMAC usado pela aplicação.

Para iniciar localmente, configure as variáveis no ambiente e execute:

```bash
mvn spring-boot:run
```

Com Docker Compose, copie `.env.example` para `.env`, substitua os valores
fictícios localmente e execute `docker compose up --build`. O Compose publica o
PostgreSQL na porta `5433` do host e o backend conecta ao serviço `postgres` na
porta interna `5432`. O volume nomeado `postgres_data` preserva o banco.

As migrações Flyway criam o esquema e `spring.jpa.hibernate.ddl-auto=validate`
confere os mapeamentos JPA na inicialização. Não altere migrações já aplicadas
em bases persistentes; acrescente uma nova versão.

## Funcionalidades presentes

- Login JWT em `POST /api/auth/login` e usuário atual em `GET /api/auth/me`.
- CRUD de projetos em `/api/projects`, limitado ao proprietário autenticado.
- Início e callback OAuth em `/api/github/oauth/authorize` e
  `/api/github/oauth/callback`.
- Recepção de eventos com validação HMAC-SHA256 em `POST /api/webhooks/github`.

As entidades e tabelas de repositórios, commits, branches e pull requests estão
modeladas, mas a sincronização correspondente com a API do GitHub ainda não está
implementada. O endpoint de webhook persiste eventos recebidos de forma
deduplicada; o processamento de eventos ainda não está implementado. Configure
`GITHUB_WEBHOOK_SECRET` com o segredo definido nas configurações do webhook do
GitHub. Sem esse valor, todas as requisições de webhook são rejeitadas.

O `state` OAuth fica em memória com validade de cinco minutos e uso único. Isso
é adequado somente para uma instância; réplicas não compartilham estados. O
escopo OAuth `repo` permite acesso a repositórios privados autorizados pelo
usuário e deve ser mantido restrito às necessidades de sincronização.

## Verificação

```bash
mvn clean verify
```

Os testes de repositório usam PostgreSQL e exigem um banco de teste acessível
com as configurações `DB_USERNAME` e `DB_PASSWORD`. Não aponte esses testes para
uma base com dados que precisem ser preservados.
