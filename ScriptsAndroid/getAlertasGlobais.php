<?php
	$url="127.0.0.1";
	$database="sid2019"; //nameDB//
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database); //username and password are input arguments//
	$sql = "select nomevariavel,datahora,descricao,intensidade,valormedicao,limiteinferior,limitesuperior from variavel, alerta_variavel,VariavelMedida,MedicaoVariavel where alerta_variavel.idmedicao = medicaoVariavel.id and medicaoVariavel.idVariavelMedida = VariavelMedida.id and medicaoVariavel.idVariavel = variavel.id and medicaoVariavel.datahoramedicao(?) =(igual ao dia) date";
	$result = mysqli_query($conn, $sql);
	$response["alertas_globais"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["nomeVariavel"] = $r['nomeVariavel'];
				$ad["dataHoraAlerta"] = $r['dataHoraAlerta'];
				$ad["descricao"] = $r['descricao'];
				$ad["intensidade"] = $r['intensidade'];
				$ad["valorMedicao"] = $r['valorMedicao'];
				$ad["limiteInferior"] = $r['limiteInferior'];
				$ad["limiteSuperior"] = $r['limiteSuperior'];
				array_push($response["alertas_globais"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["alertas_globais"]);
	echo $json;
	mysqli_close ($conn);