<?php

  class Database{

    private $host = "localhost";
    private $db = "hybarcom_bitallowance";
    private $user = "hybarcom_baAdmin";
    private $pass = "kidcurrencies";
    private $instance = null; //database instance

    public function __construct() {
      try{
    //    echo "connected";
        $this->instance = new PDO("mysql:host={$this->host};dbname={$this->db}", $this->user, $this->pass);
        if ($this->instance == null){
          echo "INSTANCE IS NULL";
        }
      }catch(PDOException $e) {
        die("failed to connect");
      }
    }

    public function getDB() {
      return $this->instance;
    }

  }

  ?>
