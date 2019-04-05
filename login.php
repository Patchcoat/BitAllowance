<?php

  require_once 'database.php';

  if (isset($_POST['login'])){
    $username = $_POST['username'];
    $password = $_POST['password'];

    //Access the database
    $db = new Database();

    $query = $db->getDB()->prepare("SELECT * FROM reserve WHERE res_username = :USERNAME");
    $query->execute([":USERNAME"=> $username, ]);
    $result = $query->fetch(PDO::FETCH_ASSOC);

    if(password_verify($password, $result['res_password'])) {
      echo $result['res_pk'];
    } else {
      echo "-1";
    }
    //print_r($result);
  }
 ?>
