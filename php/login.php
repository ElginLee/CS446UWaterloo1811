<?php

$id = $_GET["userid"];
$pass = $_GET["password"];

$mysqli = mysqli_connect('sql9.freemysqlhosting.net', 'sql9226735', 'iyWjRaetAA', 'sql9226735');

if ($mysqli === false) {
    die('Connect Error (' . $mysqli->connect_errno . ') '
            . $mysqli->connect_error);
}

$sql = "SELECT * FROM User WHERE userid='".$id."' AND password='".$pass."'";
$result = mysqli_query($mysqli,$sql);
if (mysqli_num_rows($result) > 0) {
	echo "match";
}
else {
	echo "fail";
}

$mysqli->close();
?>