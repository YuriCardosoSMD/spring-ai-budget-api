# 🎙️ API Inteligente de Orçamento com Reconhecimento de Fala e Spring Boot

API REST inteligente desenvolvida com **Java 17**, **Spring Boot 3** e **Spring AI** para gerenciamento de orçamento pessoal por meio de comandos de texto e voz.

---

## 📌 O Que o Projeto Faz

A aplicação permite registrar e consultar transações financeiras (receitas e despesas) tanto por endpoints REST tradicionais quanto por comandos em linguagem natural (texto ou arquivo de áudio). O fluxo principal funciona da seguinte maneira:

1. **Recebimento do Áudio:** O cliente envia um arquivo de áudio (`.mp3`, `.wav`, `.m4a`) para a API;
2. **Transcrição (Speech-to-Text):** A `Transcription API` (OpenAI Whisper) converte a fala em texto;
3. **Interpretação com IA (`ChatClient`):** O modelo de linguagem analisa a intenção da pessoa usuária;
4. **Execução Real (`Tool Calling`):** O Spring AI aciona automaticamente os métodos Java anotados com `@Tool` (`TransactionTools`), persistindo ou consultando transações no banco de dados;
5. **Resposta Multimodal:** A API devolve uma resposta clara em texto ou sintetizada em voz (`.mp3`) usando a `Speech API` (Text-to-Speech).

---

## ✨ Melhorias Implementadas (Evolução do Projeto Base)

1. **Novas Ferramentas para o Tool Calling (`TransactionTools`):**
   - `consultarPorCategoria`: permite que a IA filtre gastos específicos (ex: *"Quanto eu gastei com Alimentação?"*).
   - `consultarResumoFinanceiro`: calcula em tempo real o total de receitas, total de despesas, quantidade de lançamentos e saldo líquido.
2. **Validações de Regras de Negócio (`Bean Validation` e Service):**
   - Validação com `@Positive`, `@NotBlank` e verificação na camada de serviço para impedir transações com valores negativos, zerados ou sem descrição antes de persistir no banco.
3. **Fluxo Multimodal Ponta a Ponta (`/api/transactions/ai/voice-to-speech`):**
   - Endpoint que recebe o áudio do usuário, transcreve, executa a função financeira e já devolve o áudio `.mp3` sintetizado com a confirmação da operação.
4. **Suporte Híbrido de Banco de Dados (H2 + PostgreSQL com Docker) e Swagger UI:**
   - Permite testar instantaneamente em memória com H2 ou subir o container PostgreSQL via `docker-compose.yml`.

---

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.4**
- **Spring AI** (`ChatClient`, `Tool Calling`, `OpenAiAudioTranscriptionModel`, `OpenAiAudioSpeechModel`)
- **Spring Data JPA** & **Bean Validation**
- **H2 Database** (Desenvolvimento rápido) & **PostgreSQL 16 com Docker**
- **Springdoc OpenAPI / Swagger UI** (Documentação interativa)

---

## 🛠️ Como Executar a Aplicação

### Pré-requisitos
- JDK 17+ instalado
- Chave de API da OpenAI configurada na variável de ambiente `OPENAI_API_KEY` (necessária para os endpoints `/ai/**`)

### Passo a Passo
1. Clone o repositório:
   - `git clone https://github.com/YuriCardosoSMD/spring-ai-budget-api.git`
   - `cd spring-ai-budget-api`
2. *(Opcional)* Se desejar utilizar o PostgreSQL via Docker, suba o container:
   - `docker compose up -d`
3. Configure sua chave da OpenAI no terminal e execute o projeto:
   - `export OPENAI_API_KEY="sua-chave-aqui"`
   - `./mvnw spring-boot:run` (ou execute a classe `Application.java` diretamente pela sua IDE)
4. Acesse a interface interativa do **Swagger UI** no navegador:
   - `http://localhost:8080/swagger-ui.html`

---

## 🧪 Como Testar o Fluxo Principal

### 1. Cadastro e Resumo via REST Tradicional (Funciona mesmo sem chave da OpenAI)
- **Criar Transação (`POST /api/transactions`):**
  Envie o JSON `{ "description": "Supermercado Mensal", "amount": 450.00, "category": "Alimentação", "type": "EXPENSE" }`.
- **Consultar Saldo e Resumo (`GET /api/transactions/summary`):**
  Retorna o JSON com `totalIncome`, `totalExpense`, `balance` e `transactionCount`.

### 2. Comando Inteligente por Texto (`POST /api/transactions/ai/command`)
- Envie o JSON `{ "command": "Registre uma despesa de 85 reais com Uber na categoria Transporte e me diga qual é o meu saldo atual." }`.

### 3. Comando por Voz (`POST /api/transactions/ai/voice` e `/api/transactions/ai/voice-to-speech`)
- Pelo Swagger UI, clique em **POST `/api/transactions/ai/voice` > Try it out**, envie um arquivo de áudio dizendo *"Recebi meu salário de 3500 reais hoje"* e clique em **Execute**.

---

## 📚 O Que Aprendi Durante o Desafio

- Como estruturar e configurar o **Spring AI** mantendo a separação de responsabilidades entre Controller, Service, Tools e Repository;
- Como conectar um LLM a funções reais da aplicação Java por meio de **Tool Calling (`@Tool`)**, garantindo que a IA execute regras de negócio seguras em vez de apenas gerar texto;
- Como integrar pipelines de **Speech-to-Text (Transcrição)** e **Text-to-Speech (Síntese de Voz)** em uma API RESTful com Spring Boot.

---

## 👤 Autor

Desenvolvido por **Yuri Cardoso**
- GitHub: [@YuriCardosoSMD](https://github.com/YuriCardosoSMD)
- LinkedIn: [Yuri Cardoso](https://www.linkedin.com/in/yuri-da-silva-cardoso-515886215/)