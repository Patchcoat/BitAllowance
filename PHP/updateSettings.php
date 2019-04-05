<?php

  require_once 'database.php';


  if (isset($_POST['updateSettings'])){

    $resPK = $_POST['resPK'];
    $currency = $_POST['currency'];
    $symbol = $_POST['symbol'];

    if ($currency == ""){
      $currency = "Bit-Bucks";
    }
    if ($symbol == ""){
      $symbol = "$";
    }

    //Access the database
    $db = new Database();

    //Check database and make sure the record exists
    $query = $db->getDB()->prepare("SELECT * FROM reserve WHERE res_pk = :RESPK");
    $query->execute([":RESPK"=> $resPK, ]);
    $result = $query->fetch(PDO::FETCH_ASSOC);


    //If username & email do not exist - add record
    if ($result != null){
      $query = $db->getDB()->prepare("UPDATE reserve SET res_currencyname = :CURRENCY, res_symbol = :SYMBOL WHERE res_pk = :RESPK");
      if ($query->execute([":CURRENCY"=> $currency, ":SYMBOL"=> $symbol, ":RESPK"=>$resPK])){
        echo "success";
      } else {
        echo "Update Failed";
      }
    } else {
      echo "Record does not exist";
    }
  }

 ?>
