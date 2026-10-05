-- ============================================
-- Base de datos: SpeedFast
-- Actividad: Semana 8 - Desarrollo Orientado a Objetos II
-- Autor: Alejandro Serrano
-- Descripción: Script de creación de la base de datos
--              con las tablas repartidores, pedidos y entregas.
-- ============================================

-- Crear la base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS speedfast_db;

-- Usar la base de datos
USE speedfast_db;

-- ============================================
-- Tabla: repartidores
-- ============================================
CREATE TABLE repartidores (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              nombre VARCHAR(100) NOT NULL
);

-- ============================================
-- Tabla: pedidos
-- ============================================
CREATE TABLE pedidos (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         direccion VARCHAR(100) NOT NULL,
                         tipo ENUM('COMIDA','ENCOMIENDA','EXPRESS') NOT NULL,
                         estado ENUM('PENDIENTE','EN_REPARTO','ENTREGADO') NOT NULL
);

-- ============================================
-- Tabla: entregas
-- Relaciona un pedido con un repartidor
-- ============================================
CREATE TABLE entregas (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          id_pedido INT NOT NULL,
                          id_repartidor INT NOT NULL,
                          fecha DATE NOT NULL,
                          hora TIME NOT NULL,
                          FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
                          FOREIGN KEY (id_repartidor) REFERENCES repartidores(id)
);