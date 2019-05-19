<?php
	$url="127.0.0.1";
	$database="main"; //nameDB//
	$date=$_POST['date'];
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database); //username and password are input arguments//
	$sql = "select tipo,datahoraalerta,descricao,intensidade,valormedicao,limiteinferior,limitesuperior from alerta_sensor , investigador where investigador.Email_Investigador = currentUser() and alerta_sensor.id_investigador=investigador.id_investigador and (select SUBSTRING((select alerta_sensor.datahoraalerta),1,10) = '$date') order by alerta_sensor.datahoraalerta asc";
	$result = mysqli_query($conn, $sql);
	$response["alertas_globais"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["nomeVariavel"] = $r['tipo'];
				$ad["dataHoraAlerta"] = $r['datahoraalerta'];
				$ad["descricao"] = $r['descricao'];
				$ad["intensidade"] = $r['intensidade'];
				$ad["valorMedicao"] = $r['valormedicao'];
				$ad["limiteInferior"] = $r['limiteinferior'];
				$ad["limiteSuperior"] = $r['limitesuperior'];
				array_push($response["alertas_globais"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["alertas_globais"]);
	echo $json;
	mysqli_close ($conn);
