	<?php
	$url="127.0.0.1";
	$database="sid2019";
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$sql = "select idcultura from cultura,investigador where investigador.email = currentUser() and investigador.id = cultura.idinvestigador";
	$result = mysqli_query($conn, $sql);
	$response["culturas"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["idCultura"] = $r['idCultura'];
				array_push($response["culturas"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["culturas"]);
	echo $json;
	mysqli_close ($conn);


