<%--
  Created by IntelliJ IDEA.
  User: kimia
  Date: 5/11/2017
  Time: 3:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
  $("#createTree").on("submit",function(){
    $("#wait").html("creating Tree .....");
    $("#createButton").disable();
    $.post($(this).attr('action'), $(this).serialize(), function (json) {

      console.log(json);
      $.each(json, function (val, data) {


        console.log(val);
        if(val=="success"){
          $("#wait").append(' <div class="alert alert-success">'+data+'</div>');
          $("#createButton").enable();
          $("#next").enable();
        }
        else{
          $("#createButton").enable();
        }
      });
    }, 'json');

    return false;
  });

</script>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>K-NNE</title>
    <link href="resources/css/bootstrap.min.css" rel="stylesheet">


    <link href="resources/css/mystyles.css" rel="stylesheet">
    <meta charset="utf-8">

    <script src="resources/javascript/jquery-3.1.0.min.js" type="text/javascript"></script>
    <script src="resources/javascript/Myjs.js" type="text/javascript"></script>
    <script src="resources/javascript/bootstrap.min.js" type="text/javascript"></script>
  </head>
  <body>
  <div class="col-md-10">
    <div class="myclass" id="result">


    </div>
  </div>



  </div>
  </body>
</html>
