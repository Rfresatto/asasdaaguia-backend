# Águia Branca — Backend (Sprint 2)

Backend da plataforma de gestão do conhecimento e projetos da Águia Branca, desenvolvido em **Java + Spring Boot**, com persistência em **MongoDB (Atlas)** e autenticação via **JWT**.

## Sumário

- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do projeto](#configuração-do-projeto)
- [Configurando o MongoDB Atlas](#configurando-o-mongodb-atlas)
- [Executando a aplicação](#executando-a-aplicação)
- [Usuário inicial (seed)](#usuário-inicial-seed)
- [Perfis de usuário e hierarquia](#perfis-de-usuário-e-hierarquia)
- [Documentação dos endpoints (Swagger)](#documentação-dos-endpoints-swagger)
- [Diferencial de IA](#diferencial-de-ia)
- [Tratamento de erros](#tratamento-de-erros)
- [Executando os testes](#executando-os-testes)
- [Estrutura do projeto](#estrutura-do-projeto)

---

## Tecnologias utilizadas

- Java 17+
- Spring Boot 4.1.1
- Spring Security + JWT (JJWT)
- Spring Data MongoDB
- MongoDB Atlas (cluster gratuito M0)
- Lombok
- Springdoc OpenAPI 3.1.1 (Swagger UI)
- Google Gemini API — modelo `gemini-3.6-flash` (diferencial de IA)
- Maven

## Pré-requisitos

Antes de começar, tenha instalado:

- [JDK 17 ou superior](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- Uma conta no [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) (gratuita) com um cluster criado
- Uma chave de API gratuita do [Google AI Studio](https://aistudio.google.com/apikey) (necessária apenas para o endpoint de insights com IA)

> Não é necessário Docker nem instalação local do MongoDB — o projeto está configurado para conectar diretamente a um cluster no MongoDB Atlas.

## Configuração do projeto

1. Clone o repositório e entre na pasta do backend:
   ```bash
   git clone <url-do-repositorio>
   cd asasdaaguia
   ```

2. Abra o arquivo `src/main/resources/application.properties` e configure as variáveis necessárias:

   ```properties
   spring.application.name=asasdaaguia

   spring.data.mongodb.uri=${MONGODB_URI}
   spring.data.mongodb.database=${MONGODB_DATABASE}

   gemini.api.key=${API_KEY}
   gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent

   jwt.secret=${JWT_SECRET}
   jwt.expiration-ms=86400000

   springdoc.swagger-ui.path=/docs
   springdoc.api-docs.path=/api-docs
   ```

## Configurando o MongoDB Atlas

Se você (ou o avaliador) precisar criar/conectar um novo cluster do zero:

1. No [Atlas](https://cloud.mongodb.com/), vá em **Database → Build a Database** e escolha o plano **M0 Free**
2. Ao criar, **não** carregue o "Sample Dataset" — deixe em branco/skip para começar com um banco limpo
3. Em **Database Access**, crie um usuário de banco (login e senha usados na connection string — evite caracteres especiais na senha para não precisar fazer URL encode)
4. Em **Network Access**, libere o IP de quem vai rodar a aplicação (ou "Allow Access from Anywhere" para ambiente de estudo/avaliação)
5. Em **Database → Connect → Drivers → Java**, copie a connection string no formato:
   ```
   mongodb+srv://<usuario>:<senha>@<cluster>.mongodb.net/?retryWrites=true&w=majority
   ```
6. Adicione o nome do banco (`aguiabranca`) antes do `?`, exatamente como no exemplo da seção anterior

O banco e as collections (`usuarios`, `estrategias`, `ideias`, `projetos`) são criados automaticamente pelo MongoDB na primeira vez que a aplicação grava um documento — não é necessário criar nada manualmente.

## Executando a aplicação

Com o `application.properties` configurado, compile e execute:

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

Se tudo estiver certo, o console deve exibir a mensagem de criação do usuário líder inicial (veja seção abaixo) e `Started AsasdaaguiaApplication` sem erros, com o driver do Mongo confirmando conexão com os hosts do cluster Atlas (não `localhost:27017`).

## Usuário inicial (seed)

Como o cadastro de novos usuários (`POST /auth/registro`) é restrito ao perfil **LIDERANCA**, a aplicação cria automaticamente um usuário líder na primeira execução (apenas se nenhum LIDERANCA existir ainda no banco):

| Campo | Valor |
|---|---|
| Email | `lideranca@aguiabranca.com` |
| Senha | `senha123` |

> ⚠️ Essa senha é apenas para ambiente de desenvolvimento/avaliação.

Use esse usuário para logar em `POST /auth/login` e, com o token recebido, cadastrar os demais usuários (Gestores e Operadores) via `POST /auth/registro`.

## Perfis de usuário e hierarquia

O sistema possui três perfis, organizados em uma **hierarquia de permissões** configurada no Spring Security (`RoleHierarchy`):

```
LIDERANCA  >  GESTOR  >  OPERADOR
```

Um perfil superior automaticamente possui as permissões dos perfis inferiores (ex.: um Gestor satisfaz qualquer regra que exija role Operador).

| Recurso | Operador | Gestor | Liderança |
|---|:---:|:---:|:---:|
| Login | ✅ | ✅ | ✅ |
| Registrar novos usuários | ❌ | ❌ | ✅ |
| Estratégias — consultar (find many / find one) | ✅ | ✅ | ✅ |
| Estratégias — criar/editar/excluir | ❌ | ❌ | ✅ |
| Ideias — cadastrar/consultar/editar/excluir as próprias | ✅ | ✅ (herdado) | ✅ (herdado) |
| Ideias — priorizar/aprovar/reprovar | ❌ | ✅ | ✅ |
| Projetos — consultar (find many / find one) | ❌ | ✅ | ✅ |
| Projetos — cadastrar/editar/atualizar progresso/excluir | ❌ | ✅ | ✅ |
| Dashboard (resumo, resumo por estratégia, insights de IA) | ❌ | ❌ | ✅ |

## Documentação dos endpoints (Swagger)

Com a aplicação em execução, acesse:

```
http://localhost:8080/docs
```

Os endpoints estão organizados por tags (**Autenticação**, **Estratégia**, **Ideia**, **Projeto**, **Dashboard**), cada um com resumo e descrição detalhada. Para testar rotas protegidas diretamente pela interface, clique em **Authorize** e informe o token JWT no formato `Bearer <token>`.

O JSON da especificação OpenAPI fica disponível em `http://localhost:8080/api-docs`.

## Diferencial de IA

O sistema integra a **Google Gemini API** (modelo `gemini-3.6-flash`) para gerar **insights automáticos sobre os resultados exibidos no dashboard**.

- **Endpoint**: `GET /dashboard/insights`
- **Perfil exigido**: LIDERANCA
- **Funcionamento**: o backend monta um resumo agregado dos projetos (investimento, retorno, ROI, produtividade ganha) e envia como prompt para a Gemini API, que retorna de 3 a 5 insights objetivos em português, no formato de lista.

## Tratamento de erros

A API retorna erros em um formato JSON padronizado (`{ "timestamp": ..., "mensagem": ... }`), com o status HTTP correspondente ao tipo de problema:

| Situação | Status HTTP |
|---|---|
| Login inválido / usuário inativo | 401 |
| Recurso não encontrado (id inválido) | 404 |
| Regra de negócio violada (ex.: email duplicado) | 400 |
| Tentativa de alterar recurso de outro usuário (ex.: ideia de outro operador) | 403 |
| Permissão insuficiente (role incompatível) | 403 |
| Dados inválidos no corpo da requisição (validação de campos) | 400 |
| Erro interno não mapeado | 500 |

## Executando os testes

```bash
mvn test
```

Para rodar um teste específico:

```bash
mvn test -Dtest=NomeDaClasseTest
```

A suíte cobre os services de todos os módulos (Auth, Estratégia, Ideia, Projeto, Dashboard) e o `TokenService`, incluindo os principais cenários de exceção (recurso não encontrado, acesso negado, regras de negócio).

## Estrutura do projeto

```
src/main/java/br/com/aguiabranca/asasdaaguia/
├── AsasdaaguiaApplication.java
├── auth/                  # Usuario, Role, login/registro, seed do lider inicial
├── config/security/       # JWT, filtro de autenticacao, SecurityConfig, hierarquia de roles
├── estrategia/            # CRUD de orientacoes estrategicas
├── ideia/                 # CRUD de ideias + priorizacao/aprovacao/reprovacao
├── projeto/               # CRUD de projetos + acompanhamento de progresso e resultados
├── dashboard/             # Resumo de resultados (ROI, lucro) + insights com IA (Gemini)
└── common/exception/      # Tratamento global de erros (GlobalExceptionHandler)
```

Cada módulo de domínio (`estrategia`, `ideia`, `projeto`, `dashboard`) segue o padrão em camadas: `Model` (entidade MongoDB) → `Repository` (Spring Data) → `Service` (regras de negócio) → `Controller` (endpoints REST).

---

## Integrantes

| Nome | RM |
|---|---|
| _Renan Fresatto Martins_ | _562801_ |
| _Julio Cesar Bastos de Vargas Junior_ | _562121_ |
| _Miguel Siqueira de Lima_ | _564124_ |
| _Arthur Tassinari Resende_ | _565201_ |
| _João Ricardo Fidelix Bueno_ | _555568_ |
