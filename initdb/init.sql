-- Criar múltiplos databases
CREATE DATABASE db_auto;

-- (opcional) criar usuários
CREATE USER userauto WITH PASSWORD '123456';

-- criar tabela
\connect db_auto;

CREATE SCHEMA IF NOT EXISTS auto AUTHORIZATION userauto;
    
CREATE TABLE IF NOT EXISTS auto.auto_cliente
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    cliente_id character(15) COLLATE pg_catalog."default" NOT NULL,
    nu_contrato integer NOT NULL,
	contrato_seq smallint NOT NULL,
    auto_ativo boolean NOT NULL,
    status character varying(15) COLLATE pg_catalog."default" NOT NULL,
    data_criacao timestamp with time zone NOT NULL,
    data_alteracao timestamp with time zone,
    CONSTRAINT auto_cliente_pkey PRIMARY KEY (cliente_id, nu_contrato, contrato_seq)
)

TABLESPACE pg_default;

-- (opcional) permissões
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auto TO userauto;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auto TO userauto;
