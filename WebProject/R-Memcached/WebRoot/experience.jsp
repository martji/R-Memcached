<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>  
    <title>R-Memcached-experience</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link href="css/boilerplate.css" rel="stylesheet" type="text/css">
	<link href="css/memcache.css" rel="stylesheet" type="text/css">
	
	<script type="text/javascript" src="scripts/jquery-1.3.2.js"></script>
	
	<link rel="stylesheet" type="text/css" href="css/jquery.jqplot.min.css" /> 
	<script language="javascript" type="text/javascript" src="js/jquery.min.js"></script> 
	<script language="javascript" type="text/javascript" src="js/jquery.jqplot.min.js"></script> 
	<script language="javascript" type="text/javascript" src="js/jqplot.pieRenderer.min.js"></script>
	<script type="text/javascript" src="js/jqplot.barRenderer.min.js"></script>
	<script type="text/javascript" src="js/jqplot.categoryAxisRenderer.min.js"></script>
	<script type="text/javascript" src="js/jqplot.pointLabels.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			showBar();
		});
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
      <div class="fluid title1">Experience</div>
      <div class="expout">
	      <form id="paras">
	      <div class="fluid paras">
	        <div class="fluid paraitem">
	        	<label>para1:</label>
	            <input class="parasinput" id="para1"></input>
	        </div>
	        <div class="fluid paraitem">
	       		<label>para2:</label>
	            <input class="parasinput" id="para2"></input>
	        </div>
	        <div class="fluid paraitem">
	       	  <label>para3:</label>
	            <input class="parasinput" id="para3"></input>
	        </div>
	        <div class="fluid paraitem">
	        	<label>para4:</label>
	            <input class="parasinput" id="para4"></input>
	        </div>
	        <div class="fluid paraitem">
	        	<label>para5:</label>
	            <input class="parasinput" id="para5"></input>
	        </div>
	        <div class="fluid buttons">
	       	  <input class="parasbutton" id="parabutton" type="button" value="start" onclick="showInfo()"></input>
	       	  <input class="parasbutton" id="resetbutton" type="button" value="reset" onclick="reset()"></input>
	        </div>
	      </div>
	      </form>
	      <div class="expcontainer">
      	<div class="fluid expanel" id="expleft">
        	<div class="fluid time">
            	<div style="margin-left:8px; float:left; width:80px; height:20px;"><label>Progress:</label></div>
                <div class="progressContainer">
					<div id="processbar1" style="background-color: #90EE90; width: 0px; height: 14px;"></div>
				</div>
                <label style="width:40px; text-align:right;" id ="progresslabel1">000%</label>
                <label style="margin-left:8px;">Time:</label>
                <label id="time1">0.000</label>
            </div>
        	<div class="fluid data">
        		<div class="innertitle">R-Memcahced节点访问记录(TYPE, NODE, KEY, VALUE)</div>
        		<div class="innerdata">
        			<textarea class="resultarea" id="resultarea1"></textarea>
        		</div>
            </div>
      	</div>
      	<div class="fluid expmiddle"></div>
      	<div class="fluid expanel" id="expright">
        	<div class="fluid time">
              	<div style="margin-left:8px; float:left; width:80px; height:20px;"><label>Progress:</label></div>
                <div class="progressContainer">
					<div id="processbar2" style="background-color: #90EE90; width: 0px; height: 14px;"></div>
				</div>
                <label style="width:40px; text-align:right;" id="progresslabel2">0%</label>
                <label style="margin-left:8px;">Time:</label>
                <label id="time2">0.000</label>
            </div>
        	<div class="fluid data">
        		<div class="innertitle">Memcached节点访问记录(TYPE, NODE, KEY, VALUE)</div>
        		<div class="innerdata">
        			<textarea class="resultarea" id="resultarea2"></textarea>
        		</div>
        	</div>
      	</div>
      </div>
      </div>
    </div>
    <div class="fluid result">
      <div class="fluid title1">Result</div>
      <div class="fluid resultout"><div class="innerout" id="chart1"></div>
      </div>
    </div>
    
    <div class="fluid result">
      <div class="fluid title1">Node Stats</div>
      <div class="fluid resultout">
      	<div class="nodepanel"><div class="innernode" id="chart2"></div></div>
      	<div class="nodemiddle"></div>
      	<div class="nodepanel"><div class="innernode" id="chart3"></div></div>
      </div>
    </div>
  </div>
  
  <div class="fluid bottom">
    <p>R-Memcached@ACT.BUAA</p>
  </div>
</div>
</body>
<script language="JavaScript">
	var xmlHttp = false;
	var status = true;
	var progresslength1 = 0;
	var timecost = 0;
	
	var resultlist = new Array();
	
	function initAjax(){
		if (window.XMLHttpRequest){
			xmlHttp = new XMLHttpRequest();
		} else if (window.ActiveObject){
			try {
				xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
			} catch(e) {
				try {
					xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
				}catch(e){
					alert("不支持Ajax!");
				}
			}
		}
	}
	function reset(){
		document.location.reload();
	}
	function setprogress(count){
		var para1 = document.getElementById("para1").value;
		var para2 = document.getElementById("para2").value;
		progresslength1 += 180*count/para1/para2;
		if (progresslength1 < 180){
			document.getElementById("processbar1").style.width = progresslength1 + 'px';
			$("#progresslabel1").text((progresslength1/180*100).toFixed(2) + "%");
		}
	}
	
	function showInfo(){
		status = true;
		progresslength1 = 0;
		resultlist = new Array();
		document.getElementById("resultarea1").value="";
		document.getElementById("processbar1").style.width ='0px';
		$("#progresslabel1").text("0%");
		$('#time1').text("0.000");
		var para1 = document.getElementById("para1").value;
		var para2 = document.getElementById("para2").value;
		var para3 = document.getElementById("para3").value;
		var para4 = document.getElementById("para4").value;
		var para5 = document.getElementById("para5").value;
		if (para1 == '' || para2 == '' || para3 == '' || para4 == '' || para5 == ''){
			alert("必须输入完整的参数！");
			return;
		}
		var paras= 'para1='+para1+'&para2='+para2+'&para3='+para3+'&para4='+para4+'&para5='+para5;
		var url = "servlets/GetInfoServlet?"+paras;
		xmlHttp.onreadystatechange = getResult;
		xmlHttp.open("POST",url,true);
		xmlHttp.send(null);
		setTimeout(startListen, 200);
		logresult();
	}
	function logresult(){
		var alog = '';
		while((alog = resultlist.shift()) != null){
			document.getElementById("resultarea1").value += alog;
			document.getElementById('resultarea1').scrollTop = 
				document.getElementById('resultarea1').scrollHeight ;
		}
		setTimeout(logresult, 20);
	}
	
	function getResult(){
		if (xmlHttp.readyState == 4){
			if (xmlHttp.status == 200){
				status = false;
				startListen();
				getTimeandStats();
			} else {
				alert("请求页面出错！");
			}
		} else {
			document.getElementById("parabutton").setAttribute("value", "on...");
			document.getElementById("parabutton").setAttribute("disabled", "true");
		}
	}
	function startListen(){
		var result = "";
		$.getJSON("servlets/GetInfoServlet?type=getResult", function(data, textStatus){
			result = data;
			if (result.length > 0){
				var length = result.length;
				for (var i = 0; i < length; i++){
					var type = result[i].type;
					var node = result[i].node;
					var key = result[i].key;
					var value = result[i].value;
					if (value.length > 40){
						value = value.substr(0,37) + '***';
					}
					var out = "- (" + type + ", " + node +", " + key + ", " + value + ")\n";
					resultlist.push(out);
					/*
					document.getElementById("resultarea1").value += out;
					document.getElementById('resultarea1').scrollTop = 
						document.getElementById('resultarea1').scrollHeight ;
					*/
				}
				setprogress(length);
			}
		});
		
		if (status) {
			setTimeout(startListen, 100);
		}
	}
	function getTimeandStats(){
		$.getJSON("servlets/GetInfoServlet?para1=time&para2=stats", function(data, textStatus){
			data = data[0];
			dataTime = data.time;
			dataStats = eval(data.stats);
			node0_get = dataStats[0].get;
			node0_set = dataStats[0].set;
			node1_get = dataStats[1].get;
			node1_set = dataStats[1].set;
			node2_get = dataStats[2].get;
			node2_set = dataStats[2].set;
			node3_get = dataStats[3].get;
			node3_set = dataStats[3].set;
			
			var arrayObj = new Array([9]);
			arrayObj[0] = dataTime;
			arrayObj[1] = node0_get;
			arrayObj[2] = node0_set;
			arrayObj[3] = node1_get;
			arrayObj[4] = node1_set;
			arrayObj[5] = node2_get;
			arrayObj[6] = node2_set;
			arrayObj[7] = node3_get;
			arrayObj[8] = node3_set;
			
			showBar(arrayObj);
		});
	}
	function showBar(arrayObj){
		$('#chart1').html('');
		$('#chart2').html('');
		$('#chart3').html('');
		if (arrayObj == null){
			progresslength1 = 0;
			var data1 = [[0,'Memcached'], [0,'R-Memcached']];
			var innerdata2 = [['Node0000', 0], ['Node0001', 0], ['Node0002', 0], ['Node0003', 0]];
			var innerdata3 = [['Node0000', 0], ['Node0001', 0], ['Node0002', 0], ['Node0003', 0]];
			var data2 = [innerdata2, innerdata2];
			var data3 = [innerdata3, innerdata3];
		}
		else {
			var dataTime = arrayObj[0];
			$('#time1').text(dataTime);
			progresslength1 = 180;
			
			var data1 = [[dataTime*2,'Memcached'], [dataTime,'R-Memcached']];
			var innerdata21 = [['Node0000', arrayObj[1]], ['Node0001', arrayObj[3]], 
			                  ['Node0002', arrayObj[5]], ['Node0003', arrayObj[7]]];
			var innerdata22 = [['Node0000', arrayObj[2]], ['Node0001', arrayObj[4]], 
			                  ['Node0002', arrayObj[6]], ['Node0003', arrayObj[8]]];
			var data2 = [innerdata21, innerdata22];
			var data3 = data2;
		}
		
		jQuery.jqplot.config.enablePlugins = true;
		var plot1 = $.jqplot('chart1', [data1], {
			title:'R-Memcached VS Memcached',
	        seriesDefaults: {
	            pointLabels: {show: true},
	        	shadow: false,showMarker: true, 
	        	renderer: $.jqplot.BarRenderer,
	            rendererOptions: {
	                barDirection: 'horizontal', barWidth: 60, barMargin:50
	            }
	        },
	        axes: {
		         yaxis: {
		            renderer: $.jqplot.CategoryAxisRenderer,
		            labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
		            tickRenderer: $.jqplot.CanvasAxisTickRenderer,
		            tickOptions: {
		                fontFamily: 'Courier New',
		                fontSize: '12pt'
		            }
		          },
		         xaxis: {
		            label: 'Time Cost',
		            labelRenderer: $.jqplot.CanvasAxisLabelRenderer
		         }
		    }
		});
		
		var plot2 = $.jqplot('chart2', data2, {
			title:'R-Memcached',
	        series:[{renderer:$.jqplot.BarRenderer}],
	        seriesDefaults: {
	        	pointLabels: {show: true},
	        	shadow: false,showMarker: true, 
	        	renderer: $.jqplot.BarRenderer,
	        	rendererOptions: {barWidth: 30,barMargin: 50}},
	        axes: {
	          xaxis: {
	            renderer: $.jqplot.CategoryAxisRenderer,
	            label: 'Node',
	            labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
	            tickRenderer: $.jqplot.CanvasAxisTickRenderer,
	            tickOptions: {
	                angle: -30,
	                fontFamily: 'Courier New',
	                fontSize: '9pt'
	            }
	             
	          },
	          yaxis: {
	            label: 'Count',
	            labelRenderer: $.jqplot.CanvasAxisLabelRenderer
	          }
	        }
	      });
	    
	    var plot3 = $.jqplot('chart3', data3, {
	    	title:'Memcached',
	        series:[{renderer:$.jqplot.BarRenderer}],
	        seriesDefaults: {
	        	pointLabels: {show: true},
	        	shadow: false,showMarker: true, 
	        	renderer: $.jqplot.BarRenderer,
	        	rendererOptions: {barWidth: 30,barMargin: 50}},
	        axes: {
	          xaxis: {
	            renderer: $.jqplot.CategoryAxisRenderer,
	            label: 'Node',
	            labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
	            tickRenderer: $.jqplot.CanvasAxisTickRenderer,
	            tickOptions: {
	                angle: -30,
	                fontFamily: 'Courier New',
	                fontSize: '9pt'
	            }
	             
	          },
	          yaxis: {
	            label: 'Count',
	            labelRenderer: $.jqplot.CanvasAxisLabelRenderer
	          }
	        }
	      });
	    document.getElementById("parabutton").setAttribute("value", "start");
		document.getElementById("parabutton").disabled = '';
		
		document.getElementById("processbar1").style.width = progresslength1 + 'px';
		$("#progresslabel1").text(progresslength1/180*100 + "%");
	}
</script>
</html>
