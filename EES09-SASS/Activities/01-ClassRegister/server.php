<?php

require __DIR__.'/Application.php';

$app = new Application();

$app->register('GET', '/(.*?)/register', function($name) {
	$classes = json_decode(file_get_contents(__DIR__.'/classes.json'));
	if ($classes->{$name}) return $classes->{$name};
	else return ['error' => 'Class not found'];
});

$app->run();
