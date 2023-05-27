<?php

$db = 'Camcours';
$user = 'root';
$pass = '';
$host = 'localhost';
$port = 3306;

$db = new PDO("mysql:host=$host;port=$port;dbname=$db", $user, $pass);

?>
