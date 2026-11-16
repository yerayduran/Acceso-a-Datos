CREATE TABLE IF NOT EXISTS propietarios (
    id_propietario INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS coches (
    matricula VARCHAR(10) PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    extras VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    id_propietario INT,
    CONSTRAINT fk_coche_propietario FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
);

CREATE TABLE IF NOT EXISTS traspasos (
    id_traspaso INT AUTO_INCREMENT PRIMARY KEY,
    matricula_coche VARCHAR(10) NOT NULL,
    id_vendedor INT,
    id_comprador INT NOT NULL,
    monto_economico DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_traspaso_coche FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),
    CONSTRAINT fk_traspaso_comprador FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)
);