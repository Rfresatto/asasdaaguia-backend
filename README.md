# Asas da Águia

## Sumário

- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do projeto](#configuração-do-projeto)
- [Subindo o MongoDB (Docker)](#subindo-o-mongodb-docker)
- [Executando a aplicação](#executando-a-aplicação)
- [Usuário inicial](#usuário-inicial-seed)
- [Perfis de usuário e hierarquia](#perfis-de-usuário-e-hierarquia)
- [Diferencial de IA](#diferencial-de-ia)
- [Executando os testes](#executando-os-testes)
- [Estrutura do projeto](#estrutura-do-projeto)

---

## Tecnologias utilizadas

- Java 17+
- Spring Boot 3.3.2
- Spring Security + JWT
- Spring Data MongoDB
- MongoDB 7
- Lombok
- Google Gemini API
- Maven

## Pré-requisitos

Antes de começar, tenha instalado:

- [JDK 17 ou superior](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [Docker e Docker Compose](https://www.docker.com/products/docker-desktop/)
- Uma chave de API gratuita do [Google AI Studio](https://aistudio.google.com/apikey)

## Configuração do projeto

1. Clone o repositório e entre na pasta do backend:
   ```bash
   git clone https://github.com/Rfresatto/asasdaaguia.git
   cd asasdaaguia
   ```

2. Abra o arquivo `src/main/resources/application.properties` e configure as variáveis necessárias:
   
   
Se for rodar o projeto localmente descomente o segundo bloco comentado, e o primeiro bloco caso já queira com dados criados pelos end-points (o container do docker não pode estar up, rode `docker compose down`).

   ```properties
   spring.application.name=asasdaaguia

   # String de conexão com o Compass/IDE do Cluster no Atlas
   # spring.data.mongodb.uri=${MONGODB_URI}
   
   # Uso Local
   # spring.data.mongodb.uri=mongodb://localhost:27017
   
   
   spring.data.mongodb.database=${MONGODB_DATABASE}

   gemini.api.key=${API_KEY}
   gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent

   jwt.secret=${JWT_SECRET}
   jwt.expiration-ms=86400000
   ```

## Subindo o MongoDB (Docker)

O projeto já inclui um `docker-compose.yml` na raiz para rodar o MongoDB localmente:

```yaml
version: "3.8"
services:
  mongodb:
    image: mongo:7
    container_name: aguiabranca-mongo
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db

volumes:
  mongo-data:
```

Suba o banco com:

```bash
docker compose up -d
```

Confirme que o container está rodando:

```bash
docker ps
```

Deve aparecer `aguiabranca-mongo` na lista.

## Executando a aplicação

Com o MongoDB de pé, compile e execute:

```bash
mvn spring-boot:run
```

## Usuário inicial

Como o cadastro de novos usuários (`POST /auth/registro`) é restrito ao perfil **LIDERANCA**:

| Campo | Valor |
|---|---|
| Email | `lideranca@aguiabranca.com` |
| Senha | `senha123` |

> ⚠️ Essa senha é apenas para ambiente de desenvolvimento/avaliação.

Use esse usuário para logar em `POST /auth/login` e, com o token recebido, cadastrar os demais usuários (Gestores e Operadores) via `POST /auth/registro`.

## Perfis de usuário e hierarquia

O sistema possui três perfis, organizados em uma hierarquia de permissões:

```
LIDERANCA  >  GESTOR  >  OPERADOR
```

Um perfil superior automaticamente possui as permissões dos perfis inferiores (ex.: um Gestor pode fazer tudo que um Operador pode).

| Recurso | Operador | Gestor | Liderança |
|---|:---:|:---:|:---:|
| Login | ✅ | ✅ | ✅ |
| Registrar novos usuários | ❌ | ❌ | ✅ |
| Estratégias — consultar | ✅ | ✅ | ✅ |
| Estratégias — criar/editar/excluir | ❌ | ❌ | ✅ |
| Ideias — cadastrar/consultar as próprias | ✅ | ✅ | ✅ |
| Ideias — priorizar/aprovar/reprovar | ❌ | ✅ | ✅ |
| Projetos — consultar | ❌ | ✅ | ✅ |
| Projetos — cadastrar/editar/excluir | ❌ | ✅ | ✅ |
| Dashboard e Insights de IA | ❌ | ❌ | ✅ |

## Diferencial de IA

O sistema integra a **Google Gemini API** (modelo `gemini-3.6-flash`) para gerar **insights automáticos sobre os resultados exibidos no dashboard**.

- **Endpoint**: `GET /dashboard/insights`
- **Perfil exigido**: LIDERANCA
- **Funcionamento**: o backend monta um resumo agregado dos projetos (investimento, retorno, ROI, produtividade) e envia como prompt para a Gemini API, que retorna de 3 a 5 insights objetivos em português, no formato de lista.

## Executando os testes

```bash
mvn test
```

## Estrutura do projeto

```
src/main/java/br/com/aguiabranca/asasdaaguia/
├── AsasdaaguiaApplication.java
├── auth/                  # Usuario, Role, login/registro, seed do lider inicial
├── config/security/       # JWT, filtro de autenticacao, SecurityConfig, hierarquia de roles
├── estrategia/            # CRUD de orientacoes estrategicas
├── ideia/                 # CRUD de ideias + priorizacao/aprovacao
├── projeto/                # CRUD de projetos + acompanhamento de progresso
├── dashboard/              # Resumo de resultados + insights com IA
└── common/exception/       # Tratamento global de erros
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


