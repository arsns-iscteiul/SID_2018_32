<?php
	$url="127.0.0.1";
	$database="main"; 
	$date=$_POST['date'];
	$idcultura=$_POST['idCultura'];
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	$sql = "select Nome_Variavel,datahora,descricao,intensidade,Valor_Medicao,LimiteInferior,LimiteSuperior from variavel, alerta_variavel,variavel_medida,medicao where alerta_variavel.idmedicao = medicao.Id_Medicao and medicao.Variavel_medida_fk = variavel_medida.Variavel_medida_ID and variavel_medida.cultura_fk=$idcultura and variavel_medida.variavel_fk = variavel.Id_variavel and (select SUBSTRING((select alerta_variavel.datahora),1,10) = '$date') order by alerta_variavel.datahora";
	$result = mysqli_query($conn, $sql);
	$response["alertas_variavel"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["nomeVariavel"] = $r['Nome_Variavel'];
				$ad["dataHoraAlerta"] = $r['datahora'];
				$ad["descricao"] = $r['descricao'];
				$ad["intensidade"] = $r['intensidade'];
				$ad["valorMedicao"] = $r['Valor_Medicao'];
				$ad["limiteInferior"] = $r['LimiteInferior'];
				$ad["limiteSuperior"] = $r['LimiteSuperior'];
				array_push($response["alertas_variavel"], $ad);
			}
		}	
	}
	$json = json_encode($response["alertas_variavel"]);
	echo $json;
	mysqli_close ($conn);
?>	
