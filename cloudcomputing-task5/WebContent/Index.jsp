<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="css/bootstrap.min.css">
<script src="js/jquery-1.11.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/jquery.backstretch.min.js"></script>
<title>WordCount-based Search Engine</title>
</head>

<body>
	<div class="container"><br>
		<div class="row" style="border-bottom-width:1px; border-style:solid; border-left-width:0px; border-right-width:0px; border-top-width:0px;">
			<div class="col-md-2 col-lg-2 col-xs-12 col-sm-2">
				<h2>WordSearch</h2>
			</div>
			<div class="col-md-10 col-lg-10 col-xs-12 col-sm-10">
				<form action="indexServlet" method="post">
				<div class="input-group col-md-6 col-lg-6 col-xs-8 col-sm-10">
					<input type="text" class="form-control" id="keyword" name="keyword">
					<span class="input-group-btn">
						<button class="btn btn-default" type="submit">
							GO!
						</button>
					</span>
				</div><br>
				</form>
			</div>
		</div><br><br>
		<div class="row" style="min-height: 600px">
			<div class="col-md-8 col-lg-8 col-xs-12 col-sm-8">
				<%
				String msg = (String)request.getAttribute("msg");
				if(msg != null){
					out.write(msg);
				}
			%>
			</div>
		</div>
		<div class="row" style="color: white; background-color:rgba(00, 00, 00, 0.9)">
			<address style="margin-left: 20px">
  				<strong>United International College (UIC)</strong><br>
  				Cloud Computing<br>
  				Created by Willlee<br>
			</address>
		</div>
	</div>
	
</body>
</html>