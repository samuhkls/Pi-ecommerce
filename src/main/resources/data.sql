CREATE TABLE IF NOT EXISTS USUARIO(ID INT PRIMARY KEY, NOME VARCHAR(255), CPF VARCHAR(255), EMAIL VARCHAR (255), SENHA VARCHAR(255), GRUPO ENUM('Estoquista', 'Administrador') NOT NULL);