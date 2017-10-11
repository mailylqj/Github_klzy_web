<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
    <title>$Title$</title>
    <script type="text/javascript" src="<%=basePath%>js/jquery-1.9.1.js"></script>
    <script>
        function Login() {
            var login = {};
            login.username = "klzy";
            login.password = "12345";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>Login",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(login),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function getDeviceList() {
            var device={};
            device.username = "klzy";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>getDeviceList",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    // 返回的是一个DeviceBean的集合  设备列表
                    alert(jsonResult.result);
                }
            });
        }

        function regDevice() {
            var reg = {};
            reg.uid = "002355892C10";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>regDevice",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(reg),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function unregDevice() {
            var unreg = {};
            unreg.uid = "002355892C10";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>unregDevice",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(unreg),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function readConfigFile(){
            var config ={};
            config.filename = "qlly001"
            $.ajax({
                type: "POST",
                url: "<%=basePath%>readConfigFile",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(config),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function control(){
            var control ={};
            control.uid = "0023558B2178"
            control.imei = "02883203826";
            control.datatype = 1;
            control.datadecimals = 1;
            control.writeadd = 1;
            control.value = 234.1;

            $.ajax({
                type: "POST",
                url: "<%=basePath%>control",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(control),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function readModData(){
            var mdata ={};
            mdata.uid = "0023558B2178"
            mdata.startTime = "2017-09-11 15:10:10"
            mdata.endTime = "2017-09-11 15:10:10"

            $.ajax({
                type: "POST",
                url: "<%=basePath%>readModData",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(mdata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function readControlData(){
            var cdata ={};
            cdata.uid = "0023558B2178"
            cdata.startTime = "2017-09-11 14:00:10"
            cdata.endTime = "2017-09-11 15:10:10"

            $.ajax({
                type: "POST",
                url: "<%=basePath%>readControlData",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(cdata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function readAlarmData(){
            var adata ={};
            adata.uid = "0023558B2178"
            adata.startTime = "2017-09-11 14:00:10"
            adata.endTime = "2017-09-11 15:10:10"

            $.ajax({
                type: "POST",
                url: "<%=basePath%>readAlarmData",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(adata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }
    </script>


    <%--实时数据--%>
    <script type="text/javascript">

        function webSocket() {
            if (!!window.EventSource) { //EventSource是SSE的客户端.此时说明浏览器支持EventSource对象
                var source = new EventSource('<%=basePath%>/curData');//发送消息
                s = '';
                source.addEventListener('message', function (e) {
                    alert(e.data);
                });//添加客户端的监听

                source.addEventListener('open', function (e) {
                    alert("连接打开");
                }, false);

                source.addEventListener('error', function (e) {
                    if (e.readyState == EventSource.CLOSED) {
                        alert("连接关闭");
                    } else {
                        alert(e.readyState);
                    }
                });
            } else {
                alert("您的浏览器不支持SSE");
            }
        }
    </script>
</head>
<body>

<button type="button" value="login" onclick="Login()"> 登录</button>
<br><br>
<input type="button" value="getdevicelist" onclick="getDeviceList()"/>
<br><br>
<input type="button" value="regDevice" onclick="regDevice()"/>
<br><br>
<input type="button" value="unregDevice" onclick="unregDevice()"/>
<br><br>
<input type="button" value="readConfigFile" onclick="readConfigFile()"/>
<br><br>
<input type="button" value="control" onclick="control()"/>
<br><br>
<input type="button" value="readMData" onclick="readModData()"/>
<br><br>
<input type="button" value="readControlData" onclick="readControlData()"/>
<br><br>
<input type="button" value="readAlarmData" onclick="readAlarmData()"/>
<br><br>



<input type="button" value="webSocket" onclick="webSocket()"/>


</body>
</html>
