CREATE TABLE IF NOT EXISTS caminhao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    tara DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS tipo_grao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco_ton DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS filial (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS balanca (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_hardware VARCHAR(50) NOT NULL UNIQUE,
    filial_id BIGINT NOT NULL,
    FOREIGN KEY (filial_id) REFERENCES filial(id)
);

CREATE TABLE IF NOT EXISTS transacao_transporte (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    caminhao_id BIGINT NOT NULL,
    tipo_grao_id BIGINT NOT NULL,
    balanca_id BIGINT NOT NULL,
    filial_id BIGINT NOT NULL,

    peso_bruto DECIMAL(10,2),
    tara DECIMAL(10,2),
    peso_liquido DECIMAL(10,2),
    custo_carga DECIMAL(14,2),

    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP,

    FOREIGN KEY (caminhao_id) REFERENCES caminhao(id),
    FOREIGN KEY (tipo_grao_id) REFERENCES tipo_grao(id),
    FOREIGN KEY (balanca_id) REFERENCES balanca(id),
    FOREIGN KEY (filial_id) REFERENCES filial(id)
);
