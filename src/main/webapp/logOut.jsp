<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>logOut</title>
</head>
<body>
<%

session.invalidate();
Cookie cookies[]=request.getCookies();
for(Cookie c:cookies){
	if(c.getName().equals("username")){
		c.setMaxAge(0);
		response.addCookie(c);
	}
}
response.sendRedirect("index.jsp");
%>
</body>
</html>
