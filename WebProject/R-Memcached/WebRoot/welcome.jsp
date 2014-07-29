<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <!--  <base href="<%=basePath%>">-->
    
    <title>R-Memcached-experience</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link href="css/boilerplate.css" rel="stylesheet" type="text/css">
	<link href="css/memcache.css" rel="stylesheet" type="text/css">
	
	<script type="text/javascript" src="scripts/jquery-1.3.2.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function(){showBar();});
	</script>
 </head>
  
<body onLoad="initAjax()">
<div class="gridContainer clearfix">
  <div class="fluid top" id="top">
    <div class="fluid toptitle"><a href="#">Memcached</a></div>
    <div class="fluid topmenu">
      <div class="fluid menuitem" id="welcome"><a href="welcome.jsp">Welcome</a></div>
      <div class="fluid menuitem" id="experience"><a href="experience.jsp">Experience</a></div>
  	</div>
  </div>
  
  <div class="fluid middle">
    <div class="fluid exp">
      <div class="fluid title1">How to Use?</div>
      <div class="expout" style="background:black;">
      	<video src="" controls="controls" style="height:518px;width:100%;margin-top:8px;">
      </div>
    </div>
    <div class="fluid result">
      <div class="fluid title1">Structure</div>
      <div class="introduce" style="text-align:center;">
      	<img alt="" src="data/intro.png" style="height:320px; width:90%; margin-top:20px;">
      </div>
    </div>
    <div class="fluid result">
      <div class="fluid title1">Introduction</div>
      <div class="introduce">
      	<h3 style="padding:0px; margin:2px">基本说明</h3>
      	R-Memcached是基于Memcached的Java客户端的改进，通过添加副本机制均衡负载。
      	<h3 style="padding:0px; margin:2px">主要功能</h3>
      	R-Memcached的主要功能是
      	<h3 style="padding:0px; margin:2px">创新点</h3>
      	R-Memcached的主要创新点是
      </div>
    </div>
  </div>
  
  <div class="fluid bottom">
    <p>R-Memcached@ACT.BUAA</p>
  </div>
</div>
</body>
<script language="JavaScript">
	
</script>
</html>