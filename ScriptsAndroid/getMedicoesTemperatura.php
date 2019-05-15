	<?php
	$url="127.0.0.1";
	$database="sid2019";
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$sql = "select Data_Hora_Medicao,Valor_Medicao_Temperatura from medicao_temperatura where Data_Hora_Medicao >= now() - interval 5 minute";
	$result = mysqli_query($conn, $sql);
	$response["medicoes"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["valorMedicaoTemperatura"] = $r['Valor_Medicao_Temperatura'];
				$ad["dataHoraMedicao"] = $r['Data_Hora_Medicao'];
				array_push($response["medicoes"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["medicoes"]);
	echo $json;
	mysqli_close ($conn);