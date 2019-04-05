<?php

  require_once 'database.php';


  if (isset($_POST['updateAssignment'])){
    $entPK = $_POST['entPK'];
    $txnPK = $_POST['txnPK'];
    $assigned = $_POST['assigned'];


    //Access the database
    $db = new Database();

    //Check database and make sure the record exists
    $query = $db->getDB()->prepare("SELECT * FROM assignments WHERE ent_fk = :ENTPK AND txn_fk = :TXNPK");
    $query->execute([":ENTPK"=> $entPK, ":TXNPK"=> $txnPK]);
    $result = $query->fetch(PDO::FETCH_ASSOC);


    //If record exists, update the record
    if ($result != null){
      $query = $db->getDB()->prepare("UPDATE assignments SET is_assigned = :ASSIGNED  WHERE ent_fk = :ENTPK AND txn_fk = :TXNPK");
      if ($query->execute([":ASSIGNED"=> $assigned, ":ENTPK"=> $entPK, ":TXNPK"=> $txnPK])){
        echo 0;
      } else {
        echo "-1";
      }
    } else { //If record does not exist, create it.
      $query = $db->getDB()->prepare("INSERT INTO assignments VALUES(:TXNPK, :ENTPK, :ASSIGNED)");
      if ($query->execute([":ASSIGNED"=> $assigned, ":ENTPK"=> $entPK, ":TXNPK"=> $txnPK])){
        echo 1;
      } else {
        echo "-2";
      }
    }
  }

 ?>
