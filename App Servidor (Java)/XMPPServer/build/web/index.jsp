<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
	String pushStatus = "";
	Object pushStatusObj = request.getAttribute("pushStatus");

	if (pushStatusObj != null) {
		pushStatus = pushStatusObj.toString();
	}
%>
<head>
<title>XMPP Demo UAM</title>
</head>
<body>

	<h1>XMPP Demo UAM</h1>

	<form action="GCMNotification" method="post">

		<div>
			<textarea rows="2" name="message" cols="23"
				placeholder="Mensaje a enviar al dispositivo"></textarea>
		</div>
            
                <br />
                
                <div>
			<textarea rows="2" name="regid" cols="23"
                                  placeholder="Registration ID del dispositivo"></textarea>
		</div>
                
                <br />
                
		<div>
			<input type="submit" value="Enviar" />
		</div>
	</form>
	<p>
		<h3>
			<%=pushStatus%>
		</h3>
	</p>
</body>
</html>
