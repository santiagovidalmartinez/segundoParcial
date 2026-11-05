# Prueba Segundo Corte — Banco de Preguntas Saber PRO

Universidad del Cauca · Ingeniería de Software II · Taller 09 (microservicios y mensajería) + Taller 10 (JPA y arquitectura limpia)

Tres microservicios independientes (un `pom.xml` y un puerto cada uno):

| Microservicio | Puerto | Protocolo | Responsabilidad |
|---|---|---|---|
| `validation-service` | 8082 | HTTP REST | Reglas de validación estructural (HU03): 4 opciones exactas, clave A-D, justificación |
| `question-service` | 8083 | HTTP REST / AMQP | Crea y guarda preguntas (JPA + H2), llama a Validation, publica comandos |
| `lifecycle-service` | 8084 | AMQP (RabbitMQ) | Autoridad del estado: consume el comando y pasa DRAFT → PENDING_REVIEW |

```
POST /questions ──► Question Service ──(REST síncrono, RestTemplate)──► Validation Service  POST /validate
                         │  200 → DRAFT + guardar + 201 Created        400 → 400 Bad Request + errores
                         │
POST /questions/{id}/submit
                         └─ 202 Accepted {operationId, PROCESSING}
                         └─(RabbitTemplate)─► saberpro.direct.exchange ─ question.submit ─► question.submit.queue
                                                                                              │ @RabbitListener
                                                                                         Lifecycle Service → PENDING_REVIEW
```

## Punto 1 — Comunicación síncrona REST (30 %)
`RestQuestionValidator` (`question-service/.../infrastructure/client`) invoca con `RestTemplate` el endpoint `POST /validate` **antes** de guardar.
200 → la pregunta se crea en `DRAFT`, se guarda y se responde **201 Created**. 400 → no se guarda y se devuelve **400 Bad Request** con la lista de errores.

## Punto 2 — Persistencia JPA y arquitectura limpia (40 %)
Paquetes de `question-service`:

```
domain.model           Question, AnswerOption, QuestionStatus   (Java puro, SIN @Entity)
domain.repository      QuestionRepository                       (interfaz del dominio)
application            QuestionService + puertos (QuestionValidator, CommandPublisher)
infrastructure.jpa.record      QuestionRecord (@Entity @Table(name="questions"), @OneToMany cascade ALL), AnswerOptionRecord
infrastructure.jpa.mapper      QuestionDomainMapper             (bidireccional)
infrastructure.jpa.repository  SpringQuestionDataRepository + QuestionRepositoryJpaImpl
api                    QuestionController, DTOs, ApiExceptionHandler
```
`DomainPurityTest` verifica automáticamente que `domain` no importa JPA, Spring ni Jackson.

## Punto 3 — Comunicación asíncrona con RabbitMQ (20 %)
`POST /questions/{id}/submit` publica el comando `SubmitQuestionForReview` en la exchange `saberpro.direct.exchange` con routing key `question.submit`
y responde de inmediato **202 Accepted** `{operationId, status: PROCESSING}`.
En `lifecycle-service`, `@RabbitListener(queues = "question.submit.queue")` consume el comando y actualiza el estado a `PENDING_REVIEW`.

## Cómo ejecutar
Requisitos: Java 17+, Maven, Docker Desktop. Puertos libres: 5672, 15672, 8082, 8083, 8084 (**detener antes los servicios del Taller 09**).

```powershell
docker compose up -d            # RabbitMQ  ->  http://localhost:15672  (guest / guest)
cd lifecycle-service;  mvn spring-boot:run     # terminal 1
cd validation-service; mvn spring-boot:run     # terminal 2
cd question-service;   mvn spring-boot:run     # terminal 3
```
Pruebas automáticas (en cada servicio): `mvn clean test`

Consola H2 del Question Service: `http://localhost:8083/h2-console` — JDBC URL `jdbc:h2:mem:questiondb`, usuario `sa`, contraseña vacía.

## Punto 4 — Postman
Importar `postman/Prueba_SaberPro.postman_collection.json` y ejecutarla con **Runner** (en orden):
1. Crear pregunta válida → **201 Created**
2. Rechazo por validación estructural → **400 Bad Request**
3. Envío asíncrono a revisión → **202 Accepted** con `operationId`
4. Confirmación de la cola en la consola de gestión de RabbitMQ (`http://localhost:15672`)
5. (Extra) Estado `PENDING_REVIEW` en Lifecycle Service
