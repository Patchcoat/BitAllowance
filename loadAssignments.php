<?php
    class Assignment {
      public $ent_fk;
      public $txn_fk;
      public $assigned;
    }

  require_once 'database.php';

  if (isset($_POST['loadAssignments'])){
    $txnPK = $_POST['txnPK'];

    //Access the database
    $db = new Database();

    $query = $db->getDB()->prepare("SELECT * FROM assignments WHERE txn_fk = :TXNPK ORDER BY ent_fk");
    $query->execute([":TXNPK"=> $txnPK]);
    $result = $query->fetchAll(); //(PDO::FETCH_ASSOC);
    $formatResults = Array();

    for ($i = 0; $i < sizeof($result); $i++){
      $formatResults[$i] = new Assignment();
      $formatResults[$i]->ent_fk = $result[$i]['ent_fk'];
      $formatResults[$i]->txn_fk = $result[$i]['txn_fk'];
      $formatResults[$i]->assigned = $result[$i]['is_assigned'];
    }

    $json = json_encode($formatResults);
    print_r ($json);//['res_pk'];

  }
 ?>
