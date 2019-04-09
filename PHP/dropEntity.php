<?php

  require_once 'database.php';


  if (isset($_POST['dropEntity'])){

    $entPK = $_POST['entPK'];

    //Access the database
    $db = new Database();

    //Drop assignments first
    $query = $db->getDB()->prepare("DELETE FROM assignments WHERE ent_fk = :ENTPK");
    $query->execute([":ENTPK"=> $entPK, ]);
    $result = $query->fetch(PDO::FETCH_ASSOC);
    $query = $db->getDB()->prepare("DELETE FROM entity WHERE ent_pk = :ENTPK");
    if ($query->execute([":ENTPK"=> $entPK, ])) {
      echo "Record Deleted";
    } else {
      echo "Delete Failed";
    }
  }

 ?>
