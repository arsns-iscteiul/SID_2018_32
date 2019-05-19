<?php
	header('Access-Control-Allow-Origin: *');
	header('Content-type: application/json');
	$url="127.0.0.1";
	$database="main";
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$response["valid"] = array();
	$json = json_encode($response["valid"]);
	echo($json);
	mysqli_close ($conn);
?>	

	