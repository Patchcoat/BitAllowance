<!DOCTYPE HTML>
<html>
  <head>
    <title>
      Kid-Currencies Index
    </title>
  </head>
  <body>
    <div class="containter">
      <fieldset>
        <legend>Login</legend>
        <form action="login.php" method="POST">
          <input type="text" name="username" placeholder="Username">
          <input type="password" name="password" placeholder="Password">
          <input type="submit" name="login" value="Login">
        </form>
      </fieldset>

      <fieldset>
        <legend>Register</legend>
        <form action="register.php" method="POST">
          <input type="text" name="username" placeholder="Username">
          <input type="password" name="password" placeholder="Password">
          <input type="email" name="email" placeholder="Email">
          <input type="text" name="displayname" placeholder="Display Name">
          <input type="submit" name="register" value="Register">
        </form>
      </fieldset>
    </div>
  </body>
</html>
