# 📊 Gerenciador de Investimentos (FIIs, Ações e Renda Fixa)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Angular](https://img.shields.io/badge/angular-%23DD0031.svg?style=for-the-badge&logo=angular&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

Aplicação Full-Stack desenvolvida para o gerenciamento de carteiras de investimentos. O sistema permite o controle detalhado de ativos (Fundos Imobiliários, Ações e Renda Fixa), cálculo automático de preço médio, acompanhamento de dividendos e visualização do patrimônio através de gráficos dinâmicos.

🚀 **Acesse o projeto rodando ao vivo:** [CLIQUE AQUI PARA TESTAR](https://gerenciador-fii.vercel.app/)
> **Nota de Infraestrutura:** O backend está hospedado em um serviço gratuito (Render). O primeiro acesso pode levar cerca de 50 segundos para "acordar" o servidor. As requisições seguintes ocorrem em tempo real.

## 🛠️ Tecnologias Utilizadas

O projeto foi construído separando completamente as responsabilidades entre Frontend e Backend, consumindo uma API RESTful.

**Frontend:**
* **Angular 17+** (Framework SPA)
* **TypeScript**
* **Chart.js** (Renderização de gráficos)
* **HTML5 & CSS3** (Interface responsiva)
* **Vercel** (Deploy contínuo / Hospedagem)

**Backend:**
* **Java 21**
* **Spring Boot 3** (Web, Data JPA, Security)
* **Hibernate** (Mapeamento Objeto-Relacional)
* **PostgreSQL** (Banco de Dados Relacional)
* **Neon.tech** (Hospedagem do Banco de Dados em Nuvem)
* **Render & Docker** (Deploy da API)

---

## ⚙️ Funcionalidades Principais

- [x] **Autenticação:** Sistema de login seguro.
- [x] **Dashboard Visual:** Gráfico interativo mostrando a distribuição da carteira.
- [x] **CRUD de Ativos:** Cadastro, edição, listagem e exclusão de ativos financeiros.
- [x] **Cálculos Automáticos:** O sistema calcula o preço médio com base no valor total investido e a quantidade de cotas.
- [x] **Gestão de Dividendos:** Lançamento de rendimentos atrelados a cada ativo específico, somando ao total de dividendos recebidos.
- [x] **Estratégia de Cache (UX):** Uso de `localStorage` para carregamento instantâneo do dashboard (padrão Stale-While-Revalidate).

---

## 👨‍💻 Autor

**Kaique Santos** 📍 São Paulo, Brasil  
Estudante de Análise e Desenvolvimento de Sistemas
* [LinkedIn](https://www.linkedin.com/in/kaiquehsfs/).
