# HotelImpact

Sistema de gestão hoteleira (PMS — Property Management System) desenvolvido como
trabalho acadêmico de desenvolvimento de software.

## Stack

- Java 21 · Spring Boot 4.1.2 · Maven
- Spring MVC + Thymeleaf (renderização no servidor)
- Spring Data JDBC (SQL explícito via JdbcTemplate)
- PostgreSQL (inicialização nativa via `schema-DB_CREDENTIAL_PLACEHOLDER.sql`)
- Bootstrap 5 (vendorizado em `src/main/resources/static/vendor`)
- htmx 2.0.4 (navegação parcial SPA na área autenticada)

## Interface

O design system "operação hoteleira" vive em `static/css/site.css` como
tokens CSS custom properties (cores semânticas por tema claro/escuro,
escala de espaçamento, raios e estados) sobre a fundação do Bootstrap 5.
Os componentes compartilhados — sidebar agrupada pela rotina do hotel,
cabeçalho de página com contexto do módulo, badges de status com ponto e
rótulo legível, tabela operacional densa com numerais tabulares e
mensagens de feedback acessíveis — ficam nos fragments de
`templates/layout/fragments.html` e são reutilizados por todas as telas.
O painel inicial mostra a rotina de abertura da recepção: indicadores do
dia, barra de estados dos quartos e as listas de chegadas e saídas.

## Execução local

Pré-requisitos: Java 21, Maven (ou `./mvnw`), PostgreSQL 15+ rodando localmente.

```bash
# 1. Criar o banco de dados (PostgreSQL)
psql -U DB_CREDENTIAL_PLACEHOLDER -c "CREATE DATABASE hotel_impact;"

# 2. Configurar variáveis de ambiente (opcional, usa padrões locais)
export SPRING_DATASOURCE_URL=DB_URL_PLACEHOLDER
export SPRING_DATASOURCE_USERNAME=DB_CREDENTIAL_PLACEHOLDER
export SPRING_DATASOURCE_PASSWORD=DB_CREDENTIAL_PLACEHOLDER
export HOTELIMPACT_USER=RECEPCIONISTA_USER_PLACEHOLDER
export HOTELIMPACT_PASSWORD=RECEPCIONISTA_PASSWORD_PLACEHOLDER

# 3. Rodar a aplicação
./mvnw spring-boot:run

# 4. Acessar
# http://localhost:8080          → site institucional público
# http://localhost:8080/login    → autenticação da recepção
# http://localhost:8080/painel   → área administrativa (após login)
```

O site público (`/`, `/funcionalidades`, `/sobre`) é aberto a qualquer
visitante; todo o restante do sistema exige usuário autenticado. Após o
login, a recepção cai direto no painel operacional.

Credenciais e endereço do banco são lidos de variáveis de ambiente, com padrões
adequados ao PostgreSQL local:

| Variável | Padrão local |
|---|---|
| `SPRING_DATASOURCE_URL` | `DB_URL_PLACEHOLDER` |
| `SPRING_DATASOURCE_USERNAME` | `DB_CREDENTIAL_PLACEHOLDER` |
| `SPRING_DATASOURCE_PASSWORD` | `DB_CREDENTIAL_PLACEHOLDER` |
| `PORT` | `8080` |
| `HOTELIMPACT_USER` | `RECEPCIONISTA_USER_PLACEHOLDER` |
| `HOTELIMPACT_PASSWORD` | `RECEPCIONISTA_PASSWORD_PLACEHOLDER` |
| `APP_SEED_DEMO` | `false` |

Não há dados de demonstração automáticos; o schema é criado limpo e idempotente
(`CREATE TABLE IF NOT EXISTS`).

## Inicialização do schema

O Spring Boot executa `schema-DB_CREDENTIAL_PLACEHOLDER.sql` automaticamente na inicialização
(configurado em `application.yml`):

```yaml
spring:
  sql:
    init:
      mode: always
      schema-locations: classpath:schema-DB_CREDENTIAL_PLACEHOLDER.sql
```

O script contém `CREATE TABLE IF NOT EXISTS`, constraints, índices e
extensões necessárias (`btree_gist` para exclusão de reservas sobrepostas).
Pode ser executado repetidamente sem erros.

## Funcionalidades

### Site institucional (público)
- **Início** — Apresentação do produto, pilares e chamada para login
- **Funcionalidades** — Lista detalhada de cada módulo implementado
- **Sobre** — Contexto acadêmico, decisões de engenharia e qualidade

### Área administrativa (autenticada)
- **Dashboard** — Indicadores do dia (ocupação, chegadas/saídas, receita, alerta de estoque)
- **Reservas** — Criação com disponibilidade verificada, confirmação, cancelamento, no-show, edição controlada
- **Hospedagens** — Check-in, check-out (com saldo ou justificativa), consumos, pagamentos, estorno
- **Hóspedes** — Cadastro com CPF validado, busca por termo único, exclusão bloqueada com histórico
- **Quartos** — Cadastro com categoria, andar, capacidade operacional, estados (disponível, ocupado, limpeza, manutenção, interditado), interdição/liberação administrativa
- **Categorias** — Cadastro com capacidade, camas, preço-base, ativação/desativação
- **Catálogo de itens** — Produtos/serviços, controle de estoque, alerta de estoque baixo
- **Manutenção** — Ordens com prioridade, interdição crítica, ciclo completo com restauração seletiva
- **Relatórios** — Por período: receita, check-ins/outs reais, reservas criadas/canceladas/no-show, hospedagens iniciadas

## Arquitetura simplificada

```
src/main/java/.../
├── config/          SecurityConfig, HxRedirectEntryPoint, HxUrlInterceptor, WebConfig, UiAdvice
├── controller/      11 controllers MVC (Site, Home, Categoria, Hospede, Quarto, Reserva, Hospedagem, Item, Manutencao, Relatorio)
├── dto/             Forms (entrada, @Valid) e Views (projeções de leitura final)
├── domain/          Conta (record), Fechamento (genérico), Lancamento (interface)
├── enums/           11 enumerações de estado/tipo
├── exception/       NegocioException, RegistroNaoEncontradoException, GlobalExceptionHandler
├── model/           10 POJOs com lógica de negócio (máquinas de estado encapsuladas)
├── repository/      10 repositories com JdbcTemplate + SQL explícito
├── service/         11 services com @Transactional (regras de negócio)
└── validation/      @Cpf + CpfValidator (algoritmo oficial)
```

## Segurança

- Form login Spring Security (`/login`), usuário único via env `HOTELIMPACT_USER`/`HOTELIMPACT_PASSWORD` com role `RECEPCAO`
- Rotas públicas: `/`, `/sobre`, `/funcionalidades`, `/login`, recursos estáticos; todo o resto exige autenticação
- CSRF habilitado (padrão do Boot); formulários Thymeleaf injetam `_csrf` via `th:action`
- Sessão expirada na SPA: `HxRedirectEntryPoint` responde `HX-Redirect: /login` para pedidos htmx
- Navegação SPA parcial via htmx: `app.js` intercepta links/forms → `htmx.ajax` troca `#app-conteudo`; servidor devolve `th:fragment="conteudo"` com `HX-Push-Url`

## Banco de dados

PostgreSQL com:
- Tabelas: `categoria_quarto`, `quarto`, `hospede`, `reserva`, `hospedagem`, `consumo`, `item_catalogo`, `pagamento`, `ordem_manutencao`, `tarefa_limpeza`, `registro_auditoria`
- Constraints de negócio: unicidades (CPF, número do quarto, código da reserva, etc.), CHECKs de domínio, FKs
- **Exclusão de reservas sobrepostas**: `EXCLUDE USING gist (quarto_id WITH =, daterange(...) WITH &&) WHERE estado IN ('PENDENTE','CONFIRMADA')`
- **Única hospedagem ativa por quarto**: `CREATE UNIQUE INDEX ... WHERE estado = 'EM_ANDAMENTO'`
- **Única tarefa de limpeza aberta por quarto**: `CREATE UNIQUE INDEX ... WHERE estado IN ('PENDENTE','EM_EXECUCAO')`
- Índices de consulta em todas as FKs e colunas de filtro

## Qualidade

- Sem JPA/Hibernate, sem Flyway/Liquibase, sem Lombok, sem MapStruct, sem Docker
- SQL explícito, legível e parametrizado (sem concatenação insegura)
- RowMappers claros, transações declarativas (`@Transactional`)
- Tratamento centralizado de exceções (`GlobalExceptionHandler`)
- Validação declarativa nos Forms (`@Valid` + Bean Validation + `@Cpf` custom)
- PRG (Post-Redirect-Get) em todos os POSTs válidos

## Estrutura do projeto

```
hotel_impact/
├── pom.xml / mvnw
├── src/main/
│   ├── java/com/gabrielflorencco/hotel_impact/
│   │   ├── HotelImpactApplication.java
│   │   ├── config/
│   │   ├── controller/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── validation/
│   └── resources/
│       ├── application.yml
│       ├── schema-DB_CREDENTIAL_PLACEHOLDER.sql
│       ├── static/       # css/, js/, vendor/, img/, images/
│       └── templates/    # layout/, site/, dashboard/, reservas/, hospedagens/, quartos/, categorias/, hospedes/, itens/, manutencoes/, relatorios/, error/, login.html
```

## Observações

Esta é a **segunda implementação** do HotelImpact, adaptada às exigências de uma
disciplina acadêmica (stack minimalista, sem JPA/Hibernate/Flyway/Lombok/Docker).
O projeto original (portfólio completo) permanece em `/hddata/GitHub/hotelimpact`
e não foi modificado.