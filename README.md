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
- PostgreSQL (inicialização nativa via `schema-DB_CREDENTIAL_PLACEHOLDER.sql`)
- Spring Security (form login, CSRF)
- Bootstrap 5 (vendorizado)
- htmx 2.0.4 (navegação parcial SPA na área autenticada)

## Pré-requisitos

- Java 21
- Maven (ou `./mvnw`)
- PostgreSQL 15+

## Banco de dados

```bash
psql -U DB_CREDENTIAL_PLACEHOLDER -c "CREATE DATABASE hotel_impact;"
```

O script `schema-DB_CREDENTIAL_PLACEHOLDER.sql` é executado automaticamente na inicialização (configurado em `application.yml` com `spring.sql.init.mode=always`). Contém `CREATE TABLE IF NOT EXISTS`, constraints, índices e a extensão `btree_gist` para a constraint de exclusão de reservas sobrepostas. Idempotente: pode ser executado repetidamente.

## Variáveis de ambiente

| Variável | Padrão local |
|---|---|
| `SPRING_DATASOURCE_URL` | `DB_URL_PLACEHOLDER` |
| `SPRING_DATASOURCE_USERNAME` | `DB_CREDENTIAL_PLACEHOLDER` |
| `SPRING_DATASOURCE_PASSWORD` | `DB_CREDENTIAL_PLACEHOLDER` |
| `PORT` | `8080` |
| `HOTELIMPACT_USER` | `RECEPCIONISTA_USER_PLACEHOLDER` |
| `HOTELIMPACT_PASSWORD` | `RECEPCIONISTA_PASSWORD_PLACEHOLDER` |
| `APP_SEED_DEMO` | `false` |

## Execução

```bash
./mvnw spring-boot:run
```

Acessos:
- `http://localhost:8080` — site institucional público
- `http://localhost:8080/login` — autenticação da recepção
- `http://localhost:8080/painel` — área administrativa (após login)

Credenciais padrão: `RECEPCIONISTA_USER_PLACEHOLDER` / `RECEPCIONISTA_PASSWORD_PLACEHOLDER`.

## Observações

- Stack minimalista por exigência acadêmica: sem JPA/Hibernate, Flyway/Liquibase, Lombok, MapStruct, Docker.
- Persistência via Spring Data JDBC com SQL explícito (JdbcTemplate).
- Segurança: Spring Security com form login, usuário único via variáveis de ambiente, CSRF habilitado.
- Navegação SPA parcial via htmx na área autenticada.
- Schema idempotente (`CREATE TABLE IF NOT EXISTS`), sem dados de demonstração automáticos.