<?php

  require_once 'database.php';


  if (isset($_POST['updateEntity'])){
    $entPK = $_POST['entPK'];
    $resPK = $_POST['resPK'];
    $display = $_POST['display'];
    $email = $_POST['email'];
    $birthday = $_POST['birthday'];
    $balance = $_POST['balance'];


    //Access the database
    $db = new Database();

    //Check database and make sure the record exists
    $query = $db->getDB()->prepare("SELECT * FROM entity WHERE ent_pk = :ENTPK");
    $query->execute([":ENTPK"=> $entPK, ]);
    $result = $query->fetch(PDO::FETCH_ASSOC);


    //If record exists, update the record
    if ($result != null){
      $query = $db->getDB()->prepare("UPDATE entity SET ent_displayName = :DISPLAY, ent_email = :EMAIL,
        ent_birthday = :BIRTHDAY, ent_balance = :BALANCE WHERE ent_pk = :ENTPK");
      if ($query->execute([":DISPLAY"=> $display, ":EMAIL"=> $email, ":BIRTHDAY"=> $birthday, "BALANCE"=>$balance,":ENTPK"=>$entPK])){
        echo 0; 
      } else {
        echo "-1";
      }
    } else { //If record does not exist, create it.
      $query = $db->getDB()->prepare("INSERT INTO entity VALUES(NULL, :DISPLAY, :EMAIL, :BIRTHDAY, :BALANCE, :RESPK)");
      if ($query->execute([":DISPLAY"=>$display, ":EMAIL"=> $email, ":BIRTHDAY"=> $birthday, "BALANCE"=>$balance, "RESPK"=>$resPK])){
        echo $db->getDB()->lastInsertID(); // Last Insert ID
      } else {
        echo "-2";
      }
    }
  }

 ?>
