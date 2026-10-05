# ProjectsManeger

**ProjectsManeger** é uma aplicação web para gerenciamento de projetos de desenvolvimento, com integração ao GitHub para centralizar projetos, repositórios e métricas de atividade.

O projeto está sendo desenvolvido como uma aplicação full-stack, com foco em uma arquitetura organizada, autenticação segura e integração com ferramentas utilizadas no desenvolvimento de software.

> 🚧 **Status:** Em desenvolvimento

## Tecnologias

### Backend

- Java 21
- Spring Boot 3.5
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven
- JUnit 5
- Mockito

### Frontend

- React
- TypeScript
- Vite
- Tailwind CSS

### Infraestrutura

- Docker
- Docker Compose
- Nginx
- Terraform

### Integrações

- GitHub API
- GitHub Webhooks

## Funcionalidades

### Projetos

- Criar projetos
- Listar projetos do usuário autenticado
- Visualizar um projeto
- Atualizar projetos
- Excluir projetos
- Controle de acesso por proprietário

### Autenticação

- Login com usuário e senha
- Senhas protegidas com BCrypt
- Autenticação baseada em JWT
- Rotas protegidas com Spring Security

### GitHub

A integração com o GitHub permitirá:

- Vincular uma conta GitHub
- Sincronizar repositórios
- Consultar branches
- Acompanhar commits
- Acompanhar pull requests
- Receber eventos através de webhooks

### Métricas

O projeto também terá métricas relacionadas à atividade dos repositórios, permitindo visualizar a evolução dos commits e outras informações relevantes de desenvolvimento.

## Arquitetura

O projeto está organizado separando frontend, backend e infraestrutura:

```text
ProjectsManeger/
├── backend/
├── frontend/
├── github/
├── infrastructure/
├── docs/
├── docker-compose.yml
└── README.md
```

O backend segue uma organização baseada em responsabilidades, separando entidades, repositories, services, controllers, segurança e integrações externas.

## Banco de dados

O ProjectsManeger utiliza PostgreSQL como banco de dados principal.

As alterações de schema são controladas pelo **Flyway**, permitindo versionamento das migrations e reprodução consistente da estrutura do banco.

Principais entidades atualmente modeladas:

```text
User
Project
GitHubAccount
Repository
ProjectRepositoryLink
Branch
Commit
PullRequest
WebhookEvent
```

## API

A API REST é disponibilizada pelo backend através da porta `8080`.

Principais endpoints atualmente implementados:

```text
POST   /api/auth/login

GET    /api/projects
GET    /api/projects/{id}
POST   /api/projects
PUT    /api/projects/{id}
DELETE /api/projects/{id}

POST   /api/webhooks/github
```

As rotas de projetos exigem autenticação.

## Testes

O backend possui testes unitários e de camada web utilizando JUnit 5, Mockito e Spring Boot Test.

Atualmente, a suíte possui **28 testes automatizados**.

Para executar os testes:

```bash
cd backend
mvn test
```

## Executando localmente

### Pré-requisitos

- Java 21
- Maven
- PostgreSQL
- Node.js e npm
- Git

Configure as variáveis de ambiente necessárias para o backend:

```bash
export DB_PASSWORD="sua-senha"
export JWT_SECRET="$(openssl rand -base64 64)"
```

Depois execute o backend:

```bash
cd backend
mvn spring-boot:run
```

O servidor ficará disponível em:

```text
http://localhost:8080
```

> A configuração de Docker e do ambiente completo de desenvolvimento ainda está em desenvolvimento.

## Objetivos do projeto

O ProjectsManeger está sendo desenvolvido com os seguintes objetivos:

- Praticar desenvolvimento backend com Java e Spring Boot
- Aplicar conceitos de arquitetura e organização de software
- Trabalhar com autenticação e autorização
- Integrar uma aplicação com a API do GitHub
- Trabalhar com PostgreSQL e migrations
- Utilizar Docker para padronização do ambiente
- Desenvolver uma interface web moderna em React
- Criar uma aplicação que centralize informações relevantes sobre projetos de desenvolvimento

## Roadmap

- [x] Estrutura inicial do projeto
- [x] Configuração do Spring Boot
- [x] PostgreSQL
- [x] Flyway
- [x] Modelagem inicial do banco
- [x] Autenticação JWT
- [x] CRUD de projetos
- [x] Controle de ownership dos projetos
- [x] Testes automatizados do CRUD
- [ ] Tratamento global de exceções
- [ ] DTOs de resposta
- [ ] Integração completa com GitHub
- [ ] Sincronização de repositórios
- [ ] Commits e branches
- [ ] Pull requests
- [ ] Processamento de webhooks
- [ ] Dashboard
- [ ] Frontend completo
- [ ] Dockerização completa
- [ ] Nginx
- [ ] Terraform
- [ ] Deploy

## Licença

Este projeto está sob a licença definida no arquivo [LICENSE](LICENSE).
