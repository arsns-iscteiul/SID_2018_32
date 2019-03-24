DELIMITER $$

DROP PROCEDURE IF EXISTS fileExp $$

-- Create the stored procedure to perform the migration
CREATE PROCEDURE fileExp()

begin
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao.csv') IS NULL then
	  select * FROM `[log]medicao` where `[log]medicao`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao.csv' 
	  FIELDS TERMINATED BY ',' 
	  ENCLOSED BY '"' 
	  LINES TERMINATED BY '\n';
      
	  update `[log]medicao` set `[log]medicao`.exported=1;
	end if;
	
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/categoria_profissional.csv') IS NULL then
	  select * FROM `[log]categoria_profissional` where `[log]categoria_profissional`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/categoria_profissional.csv' 
	  FIELDS TERMINATED BY ',' 
	  ENCLOSED BY '"' 
	  LINES TERMINATED BY '\n';
      
	  update `[log]categoria_profissional` set `[log]categoria_profissional`.exported=1;
	end if;
	
		  
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/cultura.csv') IS NULL then
		select * FROM `[log]cultura` where `[log]cultura`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/cultura.csv' 
		FIELDS TERMINATED BY ',' 
		ENCLOSED BY '"' 
		LINES TERMINATED BY '\n';
   
	  update `[log]cultura` set `[log]cultura`.exported=1;
	end if
	
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/investigador.csv') IS NULL then
		select * FROM `[log]investigador` where `[log]investigador`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/investigador.csv' 
		FIELDS TERMINATED BY ',' 
		ENCLOSED BY '"' 
		LINES TERMINATED BY '\n';
   
	  update `[log]investigador` set `[log]investigador`.exported=1;
	end if
	
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao_luminosidade.csv') IS NULL then
		select * FROM `[log]medicao_luminosidade` where `[log]medicao_luminosidade`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao_luminosidade.csv' 
		FIELDS TERMINATED BY ',' 
		ENCLOSED BY '"' 
		LINES TERMINATED BY '\n';
   
	  update `[log]medicao_luminosidade` set `[log]medicao_luminosidade`.exported=1;
	end if
	
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao_temperatura.csv') IS NULL then
		select * FROM `[log]medicao_temperatura` where `[log]medicao_temperatura`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/medicao_temperatura.csv' 
		FIELDS TERMINATED BY ',' 
		ENCLOSED BY '"' 
		LINES TERMINATED BY '\n';
   
	  update `[log]medicao_temperatura` set `[log]medicao_temperatura`.exported=1;
	end if
	
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/sistema.csv') IS NULL then
		select * FROM `[log]sistema` where `[log]sistema`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/sistema.csv' 
		FIELDS TERMINATED BY ',' 
		ENCLOSED BY '"' 
		LINES TERMINATED BY '\n';
   
	  update `[log]sistema` set `[log]sistema`.exported=1;
	end if
	
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/tipo_cultura.csv') IS NULL then
		select * FROM `[log]tipo_cultura` where `[log]tipo_cultura`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/tipo_cultura.csv' 
		FIELDS TERMINATED BY ',' 
		ENCLOSED BY '"' 
		LINES TERMINATED BY '\n';
   
	  update `[log]tipo_cultura` set `[log]tipo_cultura`.exported=1;
	end if
	
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/variavel.csv') IS NULL then
		select * FROM `[log]variavel` where `[log]variavel`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/variavel.csv' 
		FIELDS TERMINATED BY ',' 
		ENCLOSED BY '"' 
		LINES TERMINATED BY '\n';
   
	  update `[log]variavel` set `[log]variavel`.exported=1;
	end if
	
	if LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/variavel_medida.csv') IS NULL then
		select * FROM `[log]variavel_medida` where `[log]variavel_medida`.exported=0 INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/variavel_medida.csv' 
		FIELDS TERMINATED BY ',' 
		ENCLOSED BY '"' 
		LINES TERMINATED BY '\n';
   
	  update `[log]variavel_medida` set `[log]variavel_medida`.exported=1;
	end if
  end $$
  
  
-- Execute the stored procedure
CALL fileExp() $$

-- Don't forget to drop the stored procedure when you're done!
DROP PROCEDURE IF EXISTS fileExp $$
 
DELIMITER ;