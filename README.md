# 🍕 PediuPatrão — Sistema de Gestão de Pizzaria

---

## Tecnologias e Arquitetura

- **Back-end**: Java 17+, Spring Boot 3.x (Spring MVC, Spring Security, Spring Data MongoDB)
- **Segurança**: Autenticação via formulário, proteção contra CSRF, senhas criptografadas em BCrypt com hash único
- **Banco de Dados**: MongoDB
- **Front-end**: Thymeleaf, HTML5, JavaScript (Fetch API, Chart.js), Bootstrap 5, Bootstrap Icons
- **Build & Dependências**: Apache Maven

---

## Módulo 1: Operação e Atendimento

### RF01 — Autenticação, Controle de Acesso e Perfis
- **Perfis de Acesso**:
    - `ADMIN`: Acesso total, incluindo `/usuarios`, `/clientes`, `/pedidos` e `/auditoria`.
    - `GERENTE`: Acesso a `/pedidos`, `/clientes`, `/auditoria`, concessão de descontos e cancelamento de pedidos.
    - `ATENDENTE`: Acesso restrito a `/pedidos` e `/clientes`.
- **Proteção contra Autoelevação**: Apenas administradores autenticados podem acessar os métodos de alteração de papéis.
- **Criptografia Segura**: Senhas codificadas via `PasswordEncoder` (`BCrypt`) no serviço.

### RF02 & RF03 — Gestão de Pedidos e Ciclo de Vida
- **Cálculo no Servidor**: O valor total do pedido são calculados no backend.
- **Transições de Status Validadas**: O método `isTransicaoValida()` garante o avanço estruturado:
    - `RECEBIDO` ➔ `EM PREPARAÇÃO` ➔ `PRONTO` ➔ `SAIU PARA ENTREGA` ou `RETIRADO` ➔ `FINALIZADO`.
    - Pedidos finalizados, entregues ou cancelados são bloqueados para novas alterações de estado.
- **Rastreabilidade**: Gravação automática de `dataHoraEntrada`, `responsavelEntrada`, `dataHoraSaida` e `responsavelSaida`.

### RF04 — Regras de Negócio e Restrições de Segurança
- **Concessão de Descontos**: Limitada ao perfil `GERENTE` e ao teto máximo de **20%**. Se um atendente tentar aplicar desconto, o sistema zera o valor no servidor e exibe um alerta.
- **Cancelamento de Pedidos**: Permitido exclusivamente para o perfil `GERENTE`, sendo obrigatório o preenchimento da justificativa de cancelamento.

### RF05 — Trilha de Auditoria Persistente
- **Operações Auditadas**:
    - Inclusão e alteração de clientes (rastreando mudanças em `nome`, `telefone` e `logradouro`).
    - Inclusão de novos pedidos, transições de status, aplicação de descontos e cancelamentos.
- **Estrutura dos Registros**: Cada log armazena a ação realizada, a entidade afetada, o ID do recurso, a data/hora exata, o usuário responsável e a coleção `alteracoes` contendo os pares `valorAnterior` e `valorNovo`.
- **Integridade dos Logs**: Os logs de auditoria são imutáveis e protegidos contra remoção. O acesso à tela `/auditoria` é restrito a `ADMIN` e `GERENTE`.

---
## Módulo 2: Dashboar Administrativo com API

Painel estratégico para acompanhamento de vendas e identificação de tendências operacionais, consumindo dados via API RESTful dedicada.

* **RF01 — Indicadores gerais de vendas**: Cards resumidos com faturamento total, ticket médio e volume de pedidos.
* **RF02 — Análise de vendas (Gráficos)**: Visualização gráfica do faturamento ao longo do tempo (Chart.js).
* **RF03 — Situação dos pedidos**: Mapeamento quantitativo dos pedidos por status atual.
* **RF04 — Produtos mais vendidos**: Ranking dinâmico dos itens com maior saída na operação.
* **RF05 — Filtros de consulta**: Capacidade de refinar a busca do painel por período temporal.
* **RF06 — Integração com a API**: Comunicação 100% assíncrona entre o front-end (Thymeleaf/JS) e os endpoints REST.
* **RF07 — Atualização e tratamento de dados**: Consistência na resposta da API e tratamento de cenários sem vendas no período.

---
## Execução

### Pré-requisitos
- **Java JDK 17** ou superior instalado.
- **MongoDB** em execução na porta local padrão `27017` (ou via URI configurada).
- **Apache Maven 3.8+**.

### Passo a Passo

1. **Clonar o Repositório**:
   ```bash
   git clone https://github.com/Anny004/PediuPatrao.git
   
2. **Verificar a configuração com Banco de Dados**:
- spring.data.mongodb.host=localhost
- spring.data.mongodb.port=27017
- spring.data.mongodb.database=pizzaria_pedidos

3. **Compilar e rodar o projeto (run)**

4. **Acessar o sistema no navegador e realizar login:** http://localhost:8080/

A aplicação inicializa com os seguintes usuários pré-cadastrados para login de teste:

Login     | Senha

ADMIN     | 123456

GERENTE   | 123456

ATENDENTE | 123456