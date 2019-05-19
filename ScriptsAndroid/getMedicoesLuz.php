	<?php
	$url="127.0.0.1";
	$database="main";
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$sql = "select Data_Hora_Medicao,Valor_Medicao_Luminosidade from medicao_luminosidade where Data_Hora_Medicao >= now() - interval 5 minute order by Data_Hora_Medicao asc";
	$result = mysqli_query($conn, $sql);
	$response["medicoes"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["valorMedicaoLuminosidade"] = $r['Valor_Medicao_Luminosidade'];
				$ad["dataHoraMedicao"] = $r['Data_Hora_Medicao'];
				array_push($response["medicoes"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["medicoes"]);
	echo $json;
	mysqli_close ($conn);