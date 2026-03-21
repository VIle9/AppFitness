# Fitness SaaS - Backend
API REST para aplicação de gerenciamento de alimentação e fitness.

## 🚀 Tecnologias
- Java17
- Spring Boot 3.3.5
- PostgreSQL
- JWT Authentication
- Docker
- Maven

## 📋 Pré-requisitos
- JDK 17+
- Docker & Docker Compose
- Maven 3.6+


## 🔧 Configurações

### 1. Clonar repositório
```bash
git clone https://github.com/Vlle9/AppFitness.git
cd AppFitness
```

### 2. Configurar
Copie o arquivo `.env.example` para `.env`:
```bash
cp .env.example .env
```

Edite `.env` com suas configurações:
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/appfitness
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
JWT_SECRET=sua-chave-secreta-aqui-minimo-32-caracteres
```

### 3. Iniciar PostgreSQL (Docker)
```bash
docker run --name postgres-fitness \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \ 
    -e POSTGRES_DB=appfitness \
    -p 5432:5432 \
    -d postgres:15-alpine
```

### 4. Rodar aplicação
```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 📚 Documentação API

Acesse a documentação Swagger em: 
```
http://localhost:8080/swagger-ui.html
```

## 🔑 Endpoints principais

### Autenticação
- `POST /api/v1/auth/register` - Registrar usuário
- `POST /api/v1/auth/login` - Login 

### Usuários
- `GET /api/v1/users/profile` - Obter perfil
- `PUT /api/v1/users/profile` - Atualizar perfil
- `PUT /api/v1/users/goals` - Atualizar metas

### Alimentos
- `GET /api/v1/foods` - Listar alimentos
- `GET /api/v1/foods/search?q=query` - Buscar alimentos
- `POST /api/v1/foods` - Criar alimento
- `DELETE /api/v1/foods/{id}` - Deletar alimento

### Refeições
- `POST /api/v1/meals` - Criar refeição
- `GET /api/v1/meals?date=YYYY-MM-DD` - Listar por data
- `GET /api/v1/meals/summary` - Resumo nutricional
- `DELETE /api/v1/meals/{id}` - Deletar refeição

## 🧪 Testando com cUrl

### Registrar usuário
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "password": "senha123",
    "name": "Teste User",
    "gender": "MALE",
    "birthDate": "1995-05-15",
    "height": 175,
    "weight": 75,
    "activityLevel": "MODERATE",
    "goal": "MAINTAIN"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "password": "senha123"
  }'
```

## 🐳 Docker Compose (Opcional)

Crie `docker-compose.yml`:
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    container_name: postgres-fitness
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: appfitness
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

Rodar com:
```bash
docker-compose up -d
```

## 📂 Estrutura do projeto
```

src/main/java/com/fit/AppFitness/
├── auth/                # Autenticação
├── dto/                 # Data Transfer Objects 
├── exception/           # Tratamento de erros
├── foods/               # Alimentos
├── meal/                # Refeições
├── mealfood/            # Relacionamento Meal-Food
├── security/            # Configurações de segurança
├── user/                # Usuários
└── HomeController.java 
```

## 🎯 Roadmap
- [x] API REST completa
- [x] Autenticação JWT
- [x] CRUD de usuários, alimentos e refeições
- [ ] Frontend Next.js
- [ ] Deploy em produção
- [ ] App mobile React Native 
- [ ] Integração com APIs externas de alimentos

## 📝 Licença
Este projeto é privado e todos os direitos são reservados.

## 👤 Autor 
Levi Falcão - [@Vlle9(https://github.com/Vlle9)]