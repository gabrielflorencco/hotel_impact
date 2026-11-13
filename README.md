# HotelImpact

Sistema de gestão hoteleira (PMS) desenvolvido como trabalho acadêmico.

## Funcionalidades

**Site público**
- Página inicial, funcionalidades e sobre

**Área administrativa (autenticada)**
- Dashboard com indicadores do dia (ocupação, chegadas/saídas, receita, alerta de estoque)
- Reservas: criação com verificação de disponibilidade, confirmação, cancelamento, no-show, edição controlada
- Hospedagens: check-in, check-out (com saldo ou justificativa), consumos, pagamentos, estorno
- Hóspedes: cadastro com CPF validado, busca por termo único, exclusão bloqueada com histórico
- Quartos: cadastro com categoria, andar, capacidade operacional, estados (disponível, ocupado, limpeza, manutenção, interditado), interdição/liberação administrativa
- Categorias: cadastro com capacidade, camas, preço-base, ativação/desativação
- Catálogo de itens: produtos/serviços, controle de estoque, alerta de estoque baixo
- Manutenção: ordens com prioridade, interdição crítica, ciclo completo com restauração seletiva
- Relatórios: por período (receita, check-ins/outs reais, reservas criadas/canceladas/no-show, hospedagens iniciadas)

## Tecnologias

- Java 21 · Spring Boot 4.1.2 · Maven
- Spring MVC + Thymeleaf (renderização no servidor)
- Spring Data JDBC (SQL explícito via JdbcTemplate)
- PostgreSQL (inicialização nativa via `schema-postgres.sql`)
- Spring Security (form login, CSRF)
- Bootstrap 5 (vendorizado)
- htmx 2.0.4 (navegação parcial SPA na área autenticada)

## Pré-requisitos

- Java 21
- Maven (ou `./mvnw`)
- PostgreSQL 15+

## Banco de dados

```bash
psql -U $SPRING_DATASOURCE_USERNAME -c "CREATE DATABASE hotel_impact;"
```

O script `schema-postgres.sql` é executado automaticamente na inicialização (configurado em `application.yml` com `spring.sql.init.mode=always`). Contém `CREATE TABLE IF NOT EXISTS`, constraints, índices e a extensão `btree_gist` para a constraint de exclusão de reservas sobrepostas. Idempotente: pode ser executado repetidamente.

## Variáveis de ambiente

Todas as variáveis são obrigatórias, exceto quando indicado:

| Variável | Descrição | Obrigatório |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão com o banco PostgreSQL | Sim |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco de dados | Sim |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados | Sim |
| `PORT` | Porta da aplicação | Não |
| `HOTELIMPACT_USER` | Usuário de autenticação da recepção | Sim |
| `HOTELIMPACT_PASSWORD` | Senha de autenticação da recepção | Sim |
| `APP_SEED_DEMO` | Carregar massa de dados de demonstração | Não |

## Execução

```bash
./mvnw spring-boot:run
```

Acessos:
- `http://localhost:8080` — site institucional público
- `http://localhost:8080/login` — autenticação da recepção
- `http://localhost:8080/painel` — área administrativa (após login)

Defina todas as variáveis de ambiente obrigatórias antes de executar. Não há credenciais padrão.
