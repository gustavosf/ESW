<?php

if (!isset($argv[1])) {
	echo "You need to specify a class name.\n"
	   . "Ex: php ".basename(__FIlE__)." ES001";
	exit;


}

echo file_get_contents("http://127.0.0.1:8111/".$argv[1]."/register");