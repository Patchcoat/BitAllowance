<?php

  require_once 'database.php';

  if (isset($_POST['register'])){
    $username = $_POST['username'];
    $password = $_POST['password'];
    $email = $_POST['email'];
    $displayname = $_POST['displayname'];

    if ($username == "") {
      echo "-4"; //indicate missing username
      return;
    }
    if ($email == ""){
      echo "-5"; //indicate missing email
      return;
    }
    if ($password == "")
    {
      echo "-6"; //indicate missing password;
      return;
    }
    if ($displayname == "") {
      echo "-7"; //indicate missing displayname
      return;
    }

    //Hash the password
    $password = password_hash($password, PASSWORD_DEFAULT);

    //Access the database
    $db = new Database();

    /***** Make sure email & username don't already exist *****/
    //Check database for username
    $query = $db->getDB()->prepare("SELECT * FROM reserve WHERE res_username = :USERNAME");
    $query->execute([":USERNAME"=> $username, ]);
    $resultUsername = $query->fetch(PDO::FETCH_ASSOC);
    //Check database for email
    $query = $db->getDB()->prepare("SELECT * FROM reserve WHERE res_email = :EMAIL");
    $query->execute([":EMAIL"=> $email, ]);
    $resultEmail = $query->fetch(PDO::FETCH_ASSOC);

    //If username & email do not exist - add record
    if ($resultUsername == null && $resultEmail == null){
     $query = $db->getDB()->prepare("INSERT INTO reserve VALUES(NULL, :USERNAME, :PASSWORD, :DISPLAY, :EMAIL, 'Bit-Bucks', '$')");
     if ($query->execute([":USERNAME"=> $username, ":PASSWORD"=> $password, ":DISPLAY"=>$displayname, ":EMAIL"=> $email])){

       //If registration was successful return Reserve primary key or ID
       $query = $db->getDB()->prepare("SELECT * FROM reserve WHERE res_username = :USERNAME");
       $query->execute([":USERNAME"=> $username, ]);
       $newResult = $query->fetch(PDO::FETCH_ASSOC);
       echo $newResult['res_pk'];
     } else {
       echo "-1"; // indicate registration failed
     }
   } else {
     if ($resultUsername != null){
       echo "-2";
     } else {
       echo "-3"; //return error indicating username or email already in use
     }
   }


  }

 ?>
