<?php
    class Entity {
      public $id;
      public $userName = "";
      public $displayName;
      public $birthday;
      public $email;
      public $timeSinceLastLoad = 0;
      public $transactionHistory = 0;
      public $cashBalance;
    }

  require_once 'database.php';

  if (isset($_POST['loadEntities'])){
    $reserveID = $_POST['reserveID'];

    //Access the database
    $db = new Database();

    $query = $db->getDB()->prepare("SELECT * FROM entity WHERE ent_res_fk = :RESPK");
    $query->execute([":RESPK"=> $reserveID, ]);
    $result = $query->fetchAll(); //(PDO::FETCH_ASSOC);
    $formatResults = Array();

    for ($i = 0; $i < sizeof($result); $i++){
      $record = $result[$i];
      $formatResults[$i] = new Entity();
      $formatResults[$i]->id = $result[$i]['ent_pk'];
      $formatResults[$i]->displayName = $result[$i]['ent_displayName'];
      $formatResults[$i]->birthday = $result[$i]['ent_birthday'];
      $formatResults[$i]->email = $result[$i]['ent_email'];
      $formatResults[$i]->cashBalance = $result[$i]['ent_balance'];
    }

    $json = json_encode($formatResults);
    print_r ($json);//['res_pk'];

  }
 ?>
