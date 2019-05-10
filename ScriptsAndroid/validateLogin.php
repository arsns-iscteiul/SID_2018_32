	<?php
	$url="127.0.0.1";
	$database="sid2019";
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$response["valid"] = array();
	$json = json_encode($response["valid"]);
	echo $json;
	mysqli_close ($conn);