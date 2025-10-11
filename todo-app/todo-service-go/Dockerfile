FROM golang:1.21-alpine AS builder

WORKDIR /app

COPY go.mod go.sum* ./
RUN go mod download

COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o todo-service .

FROM alpine:latest

WORKDIR /app

COPY --from=builder /app/todo-service .

EXPOSE 8082

CMD ["./todo-service"]
