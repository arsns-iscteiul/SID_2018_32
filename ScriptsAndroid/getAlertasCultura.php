<?php
	$url="127.0.0.1";
	$database="sid2019"; //nameDB//
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database); //username and password are input arguments//
	$sql = "select nomevariavel,datahoraalerta,descricao,intensidade,datahoramedicao,valormedicao,limiteinferior,limitesuperior from variavel, alerta_variavel,VariavelMedida,MedicaoVariavel where alerta_variavel.idmedicao = medicaoVariavel.id and medicaoVariavel.idVariavelMedida = VariavelMedida.id and medicaoVariavel.idVariavel = variavel.id and medicaoVariavel.datahoramedicao(?) =(igual ao dia) date";
	$result = mysqli_query($conn, $sql);
	$response["alertas_variavel"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				$ad["NomeVariavel"] = $r['NomeVariavel'];
				$ad["DataHoraAlerta"] = $r['dataHoraAlerta'];
				$ad["Descricao"] = $r['descricao'];
				$ad["Intensidade"] = $r['intensidade'];
				$ad["DataHoraMedicao"] = $r['dataHoraMedicao'];
				$ad["ValorMedicao"] = $r['valorMedicao'];
				$ad["LimiteInferior"] = $r['limiteInferior'];
				$ad["LimiteSuperior"] = $r['limiteSuperior'];
				array_push($response["alertas_variavel"], $ad);
			}
		}	
	}
	
	
	$json = json_encode($response["alertas_variavel"]);
	echo $json;
	mysqli_close ($conn);




-----OBSERVA��ES-----------------

$sql : letras minusculas?;datahoraalerta(Acho que n�o � preciso e retir�vamos do alerta_vari�vel, ou ent�o simplesmente n�o coloc�vamos para o MySqlLite)
$response: n�o sei o nome para meter em " ' ' ", secalhar � o nome da tabela do main; Cascade para onde � mais utilizado no resto do c�digo


