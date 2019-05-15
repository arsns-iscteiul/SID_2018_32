<?php
	$url="127.0.0.1";
	$database="main"; //nameDB//
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database); //username and password are input arguments//
	$sql = "select Nome_Variavel,datahoraalerta,descricao,intensidade,Data_Hora_Medicao,Valor_Medicao,LimiteInferior,LimiteSuperior from variavel, alerta_variavel,variavel_medida,medicao where alerta_variavel.idmedicao = medicao.Id_Medicao and medicao.Variavel_medida_fk = variavel_medida.Variavel_medida_ID and variavel_medida.variavel_fk = variavel.Id_variavel and medicao.Data_Hora_Medicao(?) =(igual ao dia) date";
	$result = mysqli_query($conn, $sql);
	$response["alertas_variavel"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["NomeVariavel"] = $r['Nome_Variavel'];
				$ad["DataHoraAlerta"] = $r['dataHoraAlerta'];
				$ad["Descricao"] = $r['descricao'];
				$ad["Intensidade"] = $r['intensidade'];
				$ad["DataHoraMedicao"] = $r['Data_Hora_Medicao'];
				$ad["ValorMedicao"] = $r['Valor_Medicao'];
				$ad["LimiteInferior"] = $r['LimiteInferior'];
				$ad["LimiteSuperior"] = $r['LimiteSuperior'];
				array_push($response["alertas_variavel"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["alertas_variavel"]);
	echo $json;
	mysqli_close ($conn);




-----OBSERVAÇÔES-----------------

$sql : letras minusculas?;datahoraalerta(Acho que não é preciso e retirávamos do alerta_variável, ou então simplesmente não colocávamos para o MySqlLite)
$response: não sei o nome para meter em " ' ' ", secalhar é o nome da tabela do main; Cascade para onde é mais utilizado no resto do código


