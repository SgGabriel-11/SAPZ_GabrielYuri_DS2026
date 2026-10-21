# SAPZ - Sistema de Gerenciamento de Tarefas (API)

**Disciplina:** Segurança e Auditoria de Projetos de Software (SAPZ)  
**Turma:** DS2026  
**Alunos:** Gabriel e Yuri  
**Repositório:** [SAPZ_GabrielYuri_DS2026](https://github.com/SgGabriel-11/SAPZ_GabrielYuri_DS2026)

---

## 📌 Visão Geral do Entregável - Parte II

Este repositório contém a implementação da **Parte II** do trabalho prático da disciplina de SAPZ. O foco principal desta etapa foi estruturar a suíte de **Testes Unitários Automatizados** e configurar a ferramenta de **Cobertura de Código (JaCoCo)** sobre a camada de regras de negócio (`UserService`) e camada web/REST (`UserController`).

---

## 💡 Dificuldades e Esclarecimentos sobre os Testes Unitários

> **Nota dos Alunos para o Professor / Avaliador:**  
> Durante o desenvolvimento desta etapa, nos deparamos com muitas dúvidas conceituais e práticas sobre como implementar testes unitários de forma correta. Inicialmente, tínhamos a impressão de que para testar uma API seria necessário ter o banco de dados (MySQL/PostgreSQL) rodando localmente e fazer chamadas reais às tabelas.
>
> Ao longo da realização da atividade, compreendemos os seguintes pontos fundamentais:
> 
> 1. **O que é um Mock e por que usá-lo (Mockito):**  
>    Entendemos que o teste unitário **não deve depender de serviços externos** (como banco de dados real ou conexões de rede). Por isso, utilizamos o `@Mock` para simular o comportamento do `UserRepository` e o `@InjectMocks` para injetar esse repositório falso dentro do `UserService`. Com `Mockito.when(...).thenReturn(...)`, ensinamos o mock a devolver exatamente o que queremos testar.
>
> 2. **Diferença entre Testar a Service e o Controller:**  
>    - Na **Service (`UserServiceTest`)**, testamos as regras de negócio puras (validação de senha, tratamento de exceções como `ObjectNotFoundException` e `DataBindingViolationException`).  
>    - No **Controller (`UserControllerTest`)**, testamos o protocolo HTTP usando `MockMvc` (rotas, status HTTP 200, 201, 400, 404, e serialização JSON).
>
> 3. **Independência de Banco de Dados:**  
>    Para garantir que os testes rodem em qualquer máquina ou pipeline de CI sem necessidade de configurar MySQL, configuramos o banco de dados em memória **H2** no arquivo `src/test/resources/application.properties`.
>
> 4. **Cobertura com JaCoCo:**  
>    Configuramos o plugin `jacoco-maven-plugin` no `pom.xml` para gerar métricas de cobertura de código, permitindo auditar quais linhas e métodos foram exercitados pelos testes.

---

## 🧪 Estrutura dos Testes Implementados

Foram implementados **11 testes unitários automatizados**, todos executados com **100% de sucesso**:

### 1. `UserServiceTest` (Camada de Serviço)
- `findById_Success`: Valida a busca de usuário por ID quando ele existe.
- `findById_NotFound`: Valida o lançamento da exceção `ObjectNotFoundException` quando o ID não existe.
- `create_Success`: Valida o cadastro de novo usuário com senha criptografada via BCrypt e persistência no repositório.
- `update_Success`: Valida a atualização de senha de um usuário existente.
- `delete_Success`: Valida a deleção correta de um usuário.
- `delete_DataIntegrityViolation`: Valida o lançamento de `DataBindingViolationException` caso o usuário possua vínculos com outras entidades (ex: tarefas).

### 2. `UserControllerTest` (Camada REST / Controller)
- `findUserById_Success`: Requisição `GET /user/{id}` retornando status `200 OK` e dados do usuário em JSON.
- `findUserById_NotFound`: Requisição `GET /user/{id}` retornando `404 Not Found` quando o usuário não existe.
- `createUser_Success`: Requisição `POST /user` com payload JSON retornando status `201 Created` e header `Location`.
- `updateUser_Success`: Requisição `PUT /user/{id}` retornando status `204 No Content`.

---

## 🚀 Como Executar os Testes Localmente

### Pré-requisitos
- **Java JDK 17** ou superior instalado.
- O projeto já inclui o Maven Wrapper (`mvnw`), portanto **não** é obrigatório ter o Maven instalado globalmente.

### Passo a Passo

1. Abra um terminal na pasta `api`:
   ```bash
   cd api
   ```

2. Execute o comando de testes e geração do relatório JaCoCo:
   - **No Windows (PowerShell / CMD):**
     ```powershell
     .\mvnw.cmd clean test
     ```
   - **No Linux / macOS:**
     ```bash
     ./mvnw clean test
     ```

3. **Resultado Esperado:**
   ```text
   [INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
   [INFO] BUILD SUCCESS
   ```

4. **Visualizar Relatório de Cobertura (JaCoCo):**  
   Abra no seu navegador o arquivo gerado em:
   ```text
   api/target/site/jacoco/index.html
   ```

---

## 📦 Estrutura de Pastas do Projeto

```text
SAPZ_GabrielYuri_DS2026/
├── .gitignore
├── README.md
└── api/
    ├── pom.xml
    ├── mvnw / mvnw.cmd
    ├── src/
    │   ├── main/
    │   │   ├── java/tarefas02/api/        # Código fonte da aplicação
    │   │   └── resources/                 # Configurações de produção/dev
    │   └── test/
    │       ├── java/tarefas02/api/
    │       │   ├── controllers/           # UserControllerTest.java
    │       │   └── services/              # UserServiceTest.java
    │       └── resources/                 # application.properties (H2 em memória)
    └── view/                              # Frontend básico da aplicação
```

---

## 👥 Autores
- Gabriel
- Yuri Mendes