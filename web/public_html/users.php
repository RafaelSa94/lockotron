<?php
require_once("../controller/HistoricoController.class.php");
require_once("../controller/AutorizacaoController.class.php");
require_once("../model/autorizacao.php");
header("Content-Type:application/json");
$user = new UsuarioController();
$accessController = new AutorizacaoController();

/**
 * Procura as regras de acesso de um usuário e coloca no array junto com ele
 * @param array $user O array do usuário
 */
function accessToUserArray(array &$user)
{
	$user['access'] = (new AutorizacaoController())->getForUser($user['id']);
	foreach ($user['access'] as $i => $rule) {
		$user['access'][$i] = $rule->toArray();
	}
}

if (isset($_GET['insert'])) {
	$error = false;
	if (isset($_POST['name']) && $_POST['name'] != '' &&
		isset($_POST['access']) && $_POST['access'] != '')
	{
		$access = json_decode($_POST['access'], true);
		if (count($access) == 0) {
			$error = true;
		}
	} else {
		$error = true;
	}

	if ($error) {
		http_response_code(400);
		echo json_encode(array(
			'success' => false,
		));
	} else {
		$new = $user->insert(new Usuario(null, $_POST['name']));
		foreach ($access as $i => $rule) {
			$accessController->insert(new Autorizacao(null, $new, $rule['day'], $rule['time-start'], $rule['time-end']));
		}
		$data = $new->toArray();
		accessToUserArray($data);
		echo json_encode(array(
			'success' => true,
			'data' => array($data),
		));
	}
} else {
	$data = $user->getAll();
	foreach ($data as $i => $value) {
		$data[$i] = $value->toArray();
		accessToUserArray($data[$i]);
	}
	echo json_encode(array(
		'success' => true,
		'data' => $data,
	));
}
