	<?php
	$url="127.0.0.1";
	$database="sid2019"; //dbName//
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$sql = "select nomecultura,descricaocultura from cultura where cultura.id = idcultura";
	$result = mysqli_query($conn, $sql);
	$response["infocultura"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["nomeCultura"] = $r['nomeCultura'];
				$ad["descricaoCultura"] = $r['descricaoCultura'];
				array_push($response["infocultura"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["infocultura"]);
	echo $json;
	mysqli_close ($conn);