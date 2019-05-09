	<?php
	$url="127.0.0.1";
	$database="sid2019";
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$sql = "select dataHoraMedicao,valorMedicaoTemperatura from medicoesTemperatura where dataHoraMedicao >= now() - interval 5 minute";
	$result = mysqli_query($conn, $sql);
	$response["medicoes"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["valorMedicaoTemperatura"] = $r['valorMedicaoTemperatura'];
				$ad["dataHoraMedicao"] = $r['dataHoraMedicao'];
				array_push($response["medicoes"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["medicoes"]);
	echo $json;
	mysqli_close ($conn);