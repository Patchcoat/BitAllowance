<?php

  require_once 'database.php';


  if (isset($_POST['updateTransaction'])){
    $txnPK = $_POST['txnPK'];
    $name = $_POST['name'];
    $value = $_POST['value'];
    $memo = $_POST['memo'];
    $type = $_POST['type'];
    $expire = $_POST['expires'];
    $expireDate = $_POST['expireDate'];
    $repeat = $_POST['repeat'];
    $cooldown = $_POST['cooldown'];
    $resPK = $_POST['resPK'];

  /*  For testing purposes
    echo " * ";
    echo $txnPK;
    echo " * ";
    echo $name;
    echo " * ";
    echo $value;
    echo " * ";
    echo $memo;
    echo " * ";
    echo $type;
    echo " * ";
    echo $expire;
    echo " * ";
    echo $expireDate;
    echo " * ";
    echo $repeat;
    echo " * ";
    echo $cooldown;
    echo " * ";
    echo $resPK;
    echo " * "; //*/


    //Access the database
    $db = new Database();

    //Check database and make sure the record exists
    $query = $db->getDB()->prepare("SELECT * FROM transaction WHERE txn_pk = :TXNPK");
    $query->execute([":TXNPK"=> $txnPK, ]);
    $result = $query->fetch(PDO::FETCH_ASSOC);


    //If record exists, update the record
    if ($result != null){
      $query = $db->getDB()->prepare("UPDATE transaction
                                      SET txn_name = :NAME,
                                          txn_value = :VALUE,
                                          txn_memo = :MEMO,
                                          txn_type = :TYPE,
                                          txn_expires	= :EXPIRES,
                                          txn_expiredate = :EXPIREDATE,
                                          txn_repeatable = :REPEATABLE,
                                          txn_cooldown = :COOLDOWN
                                      WHERE txn_pk = :TXNPK");
      if ($query->execute([":NAME"=> $name,
                           ":VALUE"=> $value,
                           ":MEMO"=> $memo,
                           ":TYPE"=> $type,
                           ":EXPIRES"=> $expire,
                           ":EXPIREDATE"=> $expireDate,
                           ":REPEATABLE"=> $repeat,
                           ":COOLDOWN"=> $cooldown,
                           ":TXNPK"=> $txnPK])){
        echo '0';
      } else {
        echo "-1";
      }
    } else { //If record does not exist, create it.
      $query = $db->getDB()->prepare("INSERT INTO transaction
                                        VALUES(
                                          NULL,
                                          :NAME,
                                          :VALUE,
                                          :MEMO,
                                          :TYPE,
                                          :EXPIRES,
                                          :EXPIREDATE,
                                          :REPEATABLE,
                                          :COOLDOWN,
                                          :RESFK)");
      if ($query->execute([":NAME"=> $name,
                           ":VALUE"=> $value,
                           ":MEMO"=> $memo,
                           ":TYPE"=> $type,
                           ":EXPIRES"=> $expire,
                           ":EXPIREDATE"=> $expireDate,
                           ":REPEATABLE"=> $repeat,
                           ":COOLDOWN"=> $cooldown,
                           "RESFK"=>$resPK])){
        echo $db->getDB()->lastInsertID(); // Last Insert ID
      } else {
        echo "-2";
      }
    }
  }

 ?>
