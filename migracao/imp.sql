LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao.csv' 
	INTO TABLE auditordb.`[log]medicao`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';
	
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/categoria_profissional.csv' 
	INTO TABLE auditordb.`[log]categoria_profissional`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';
	
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/cultura.csv' 
	INTO TABLE auditordb.`[log]cultura`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/investigador.csv' 
	INTO TABLE auditordb.`[log]investigador`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';
	
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao_luminosidade.csv' 
	INTO TABLE auditordb.`[log]medicao_luminosidade`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';
	
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao_temperatura.csv' 
	INTO TABLE auditordb.`[log]medicao_temperatura`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';
	
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/sistema.csv' 
	INTO TABLE auditordb.`[log]sistema`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';
	
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/tipo_cultura.csv' 
	INTO TABLE auditordb.`[log]tipo_cultura`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';
	
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/variavel.csv' 
	INTO TABLE auditordb.`[log]variavel`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';
	
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/variavel_medida.csv' 
	INTO TABLE auditordb.`[log]variavel_medida`
	FIELDS TERMINATED BY ',' 
	ENCLOSED BY '"' 
	LINES TERMINATED BY '\n';