<?php

  require_once 'database.php';


  if (isset($_POST['dropTransaction'])){

    $txnPK = $_POST['txnPK'];

    //Access the database
    $db = new Database();

    //Drop assignments first
    $query = $db->getDB()->prepare("DELETE FROM assignments WHERE txn_fk = :TXNPK");
    $query->execute([":TXNPK"=> $txnPK, ]);
    $result = $query->fetch(PDO::FETCH_ASSOC);
    $query = $db->getDB()->prepare("DELETE FROM transaction WHERE txn_pK = :TXNPK");
    if ($query->execute([":TXNPK"=> $txnPK, ])) {
      echo "Record Deleted";
    } else {
      echo "Delete Failed";
    }
  }

 ?>
