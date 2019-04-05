<?php
    class Transaction {
      public $id;
      public $name;
      public $value;
      public $memo;
      public $type;
      public $expirable;
      public $expireDate;
      public $coolDown;
      public $repeatable;
    }

  require_once 'database.php';

  if (isset($_POST['loadTransactions'])){
    $reserveID = $_POST['reserveID'];

    //Access the database
    $db = new Database();

    $query = $db->getDB()->prepare("SELECT * FROM transaction WHERE txn_res_fk = :RESPK");
    $query->execute([":RESPK"=> $reserveID, ]);
    $result = $query->fetchAll(); //(PDO::FETCH_ASSOC);
    $formatResults = Array();

    for ($i = 0; $i < sizeof($result); $i++){
      $record = $result[$i];
      $formatResults[$i] = new Transaction();
      $formatResults[$i]->id = $result[$i]['txn_pk'];
      $formatResults[$i]->name = $result[$i]['txn_name'];
      $formatResults[$i]->value = $result[$i]['txn_value'];
      $formatResults[$i]->memo = $result[$i]['txn_memo'];
      $formatResults[$i]->type = $result[$i]['txn_type'];
      $formatResults[$i]->expirable = $result[$i]['txn_expires'];
      $formatResults[$i]->expireDate = $result[$i]['txn_expiredate'];
      $formatResults[$i]->repeatable = $result[$i]['txn_repeatable'];
      $formatResults[$i]->coolDown = $result[$i]['txn_cooldown'];
    }

    $json = json_encode($formatResults);
    print_r ($json);//['res_pk'];
  }
 ?>
