	<?php
	$url="127.0.0.1";
	$database="main"; //dbName//
	$idCultura=$_POST['idCultura'];
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$sql = "select Nome_Cultura,Descricao_Cultura from cultura where Id_Cultura =$idCultura";
	$result = mysqli_query($conn, $sql);
	$response["infocultura"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["nomeCultura"] = $r['Nome_Cultura'];
				$ad["descricaoCultura"] = $r['Descricao_Cultura'];
				array_push($response["infocultura"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["infocultura"]);
	echo $json;
	mysqli_close ($conn);
