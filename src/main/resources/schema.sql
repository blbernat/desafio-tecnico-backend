CREATE TABLE caminhao (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    tara DECIMAL(10,2) NOT NULL
);

CREATE TABLE tipo_grao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco_ton DECIMAL(10,2) NOT NULL
);

CREATE TABLE filial (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE balanca (
    id BIGSERIAL PRIMARY KEY,
    codigo_hardware VARCHAR(50) NOT NULL UNIQUE,
    filial_id BIGINT NOT NULL,
    FOREIGN KEY (filial_id) REFERENCES filial(id)
);

CREATE TABLE transacao_transporte (
    id BIGSERIAL PRIMARY KEY,
    caminhao_id BIGINT NOT NULL,
    tipo_grao_id BIGINT NOT NULL,
    balanca_id BIGINT NOT NULL,

    peso_bruto DECIMAL(10,2),
    tara DECIMAL(10,2),
    peso_liquido DECIMAL(10,2),
    custo_carga DECIMAL(14,2),

    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP,

    FOREIGN KEY (caminhao_id) REFERENCES caminhao(id),
    FOREIGN KEY (tipo_grao_id) REFERENCES tipo_grao(id),
    FOREIGN KEY (balanca_id) REFERENCES balanca(id)
);

CREATE INDEX idx_transacao_caminhao ON transacao_transporte (caminhao_id);
CREATE INDEX idx_transacao_balanca  ON transacao_transporte (balanca_id);
CREATE INDEX idx_transacao_inicio   ON transacao_transporte (inicio);
