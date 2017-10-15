<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
    <title>$Title$</title>
    <script type="text/javascript" src="<%=basePath%>js/jquery-1.9.1.js"></script>

    <%--ajax 请求--%>
    <script>
        function Login() {
            var login = {};
            login.username = "klzy";
            login.password = "12345";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/Login",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(login),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function getDeviceList() {
            var device = {};
            device.username = "klzy";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/getDeviceList",
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
            reg.uid = "0023558B2178";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/regDevice",
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
            unreg.uid = "0023558B2178";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/unregDevice",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(unreg),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function readConfigFile() {
            var config = {};
            config.filename = "qlly001"
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/readConfigFile",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(config),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function control() {
            var value = document.getElementById('value').value;
            var control = {};
            control.uid = "0023558B2178"
            control.name = "温度"
            control.value = value;

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/control",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(control),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function readModData() {
            var mdata = {};
            mdata.uid = "0023558B2178"
            mdata.startTime = 1507525098000
            mdata.endTime = 1507527098000

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/readModData",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(mdata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function ModData_Next() {
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/ModDataNext",
                contentType: "application/json",
                dataType: "json",
                data: "",
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function ModData_Prev() {
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/ModDataPrev",
                contentType: "application/json",
                dataType: "json",
                data: "",
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }


        function readControlData() {
            var cdata = {};
            cdata.uid = "0023558B2178"
            cdata.startTime = 1506757210000
            cdata.endTime = 1506768610000

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/readControlData",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(cdata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function readAlarmData() {
            var adata = {};
            adata.uid = "0023558B2178"
            adata.startTime = 1506757210000
            adata.endTime = 1506768610000

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/readAlarmData",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(adata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }
    </script>

    <%--实时数据  websocket--%>
    <script type="text/javascript">
        var ws_01 = null;
        var url_01 = 'ws://' + window.location.host + '/ws/socket01';
        var transports = [];

        function setConnected01(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('echo').disabled = !connected;
        }

        function websocket_connect01() {
            alert("url:" + url_01);
            if (!url_01) {
                alert('Select whether to use W3C WebSocket or SockJS');
                return;
            }

            ws_01 = (url_01.indexOf('sockjs') != -1) ?
                new SockJS(url_01, undefined, {protocols_whitelist: transports}) : new WebSocket(url_01);

            ws_01.onopen = function () {
                setConnected01(true);
                alert('Info: connection opened.');
            };
            ws_01.onmessage = function (event) {
                // alert('Received: ' + event.data);
            };
            ws_01.onclose = function (event) {
                setConnected01(false);
                alert('Info: connection closed.');
            };
        }

        function disconnect01() {
            if (ws_01 != null) {
                ws_01.close();
                ws_01 = null;
            }
            setConnected01(false);
        }

        function send01() {
            if (ws_01 != null) {
                var message = document.getElementById('message').value;
                ws_01.send(message);
            } else {
                alert('connection not established, please connect.');
            }
        }

        function updateTransport(transport) {
            alert(transport);
            transports = (transport == 'all') ? [] : [transport];
        }
    </script>

    <%--实时数据  websocket--%>
    <script type="text/javascript">
        var ws_02 = null;
        var url_02 = 'ws://' + window.location.host + '/ws/socket02';
        var transports = [];

        function setConnected02(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('echo').disabled = !connected;
        }

        function websocket_connect02() {
            alert("url_02:" + url_02);
            if (!url_02) {
                alert('Select whether to use W3C WebSocket or SockJS');
                return;
            }

            ws_02 = (url_02.indexOf('sockjs') != -1) ?
                new SockJS(url_02, undefined, {protocols_whitelist: transports}) : new WebSocket(url_02);

            ws_02.onopen = function () {
                setConnected02(true);
                alert('Info: connection opened.');
            };
            ws_02.onmessage = function (event) {
                alert('Received: ' + event.data);
            };
            ws_02.onclose = function (event) {
                setConnected02(false);
                alert('Info: connection closed.');
            };
        }

        function disconnect02() {
            if (ws_02 != null) {
                ws_02.close();
                ws_02 = null;
            }
            setConnected02(false);
        }

        function send02() {
            if (ws_02 != null) {
                var message = document.getElementById('message02').value;
                ws_02.send(message);
            } else {
                alert('connection not established, please connect.');
            }
        }

        function updateTransport(transport) {
            alert(transport);
            transports = (transport == 'all') ? [] : [transport];
        }
    </script>

    <%--user 操作--%>
    <script>
        function user_add() {
            //level  1 用户操作员 2 用户管理员  3 厂家操作员 4 厂家管理员

            var user = {};
            user.name = "add_test";
            user.password = "12345";
            user.type = 1;
            user.pro_company = "klzy";
            user.use_company = "klzy";
            user.level = 9;
            user.imei = "990001233312212"

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userAdd",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function user_del() {
            var user = {};
            user.namelist = ["add_test", "add_test01"];
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userDel",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function user_cpwd() {
            var user = {};
            user.name = "klzy";
            user.original_pwd = "12345";
            user.new_pwd = "456767";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userCpwd",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }


        function user_selectbyname() {
            var user = {};
            user.name = "klzy";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userSelectByName",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function user_selectall() {
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userSelectAll",
                contentType: "application/json",
                dataType: "json",
                data: "",
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function user_update() {
            //level  1 用户操作员 2 用户管理员  3 厂家操作员 4 厂家管理员
            var user = {};
            user.name = "add_test";
            user.value = "56789";
            user.type = 1;
            user.pro_company = "klzy";
            user.use_company = "klzy";
            user.level = 9;
            user.imei = "990001233312212"
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userUpdate",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

    </script>

    <%--device 操作--%>
    <script>
        function device_add() {
            //level  1 用户操作员 2 用户管理员  3 厂家操作员 4 厂家管理员
            var device = {};
            device.id = "002355892C11";
            device.type = 0;
            device.cmd = "01030020";
            device.looptime = 2;
            device.pro_company = "klzy";
            device.use_company = "klzy";
            device.longitude = 23.2293;
            device.latitude = 104.332;
            device.devicename = "测试设备12";
            device.configfilename = "klzy213";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceAdd",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_del() {
            var device = {};
            device.namelist = ["002355892c11", "002355892c12"];
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceDel",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_selectbyname() {
            var device = {};
            device.id = "002355892c11";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceSelectByName",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_selectall() {
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceSelectAll",
                contentType: "application/json",
                dataType: "json",
                data: "",
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_update() {
            //level  1 用户操作员 2 用户管理员  3 厂家操作员 4 厂家管理员
            var device = {};
            device.id = "002355892C11";
            device.type = 0;
            device.cmd = "01030020";
            device.looptime = 2;
            device.pro_company = "klzy";
            device.use_company = "klzy";
            device.longitude = 23.2293;
            device.latitude = 104.332;
            device.devicename = "测试设备12";
            device.configfilename = "klzy213";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceUpdate",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

    </script>

</head>
<input>

<input type="button" value="login" onclick="Login()"> 登录</input>
<br><br>
<input type="button" value="getdevicelist" onclick="getDeviceList()">获取设备列表</input>
<br><br>
<input type="button" value="regDevice" onclick="regDevice()"> 注册设备（选中该设备并获取实时数据）</input>
<br><br>
<input type="button" value="unregDevice" onclick="unregDevice()">取消注册</input>
<br><br>
<input type="button" value="readConfigFile" onclick="readConfigFile()">强制更新配置文件（配置文件客户修改后刷新用，因配置文件是缓存下来的，修改后需重新读取数据库并替换缓存）</input>
<br><br>
<textarea id="value" style="width: 350px">value</textarea>
<input type="button" value="control" onclick="control()">控制</input>
<br><br>
<input type="button" value="readMData" onclick="readModData()">历史数据</input>
<input type="button" value="readMDataPrev" onclick="ModData_Prev()">上一页</input>
<input type="button" value="readMDataNext" onclick="ModData_Next()">下一页</input>
<br><br>
<input type="button" value="readControlData" onclick="readControlData()">控制数据查询</input>
<br><br>
<input type="button" value="readAlarmData" onclick="readAlarmData()">报警数据查询</input>
<br><br>


<textarea id="message" style="width: 350px">Here is a message!</textarea>
<input type="button" value="webSocket_connection" onclick="websocket_connect01()"/>websocket连接后，再点击reg 即可获取到reg设备的实时数据
<input type="button" value="webSocket_disconnection" onclick="disconnect01()"/>
<input type="button" value="webSocket_send" onclick="send01()"/>

<br><br>

<textarea id="message02" style="width: 350px">Here is a message!</textarea>
<input type="button" value="webSocket_connection" onclick="websocket_connect02()"/>
<input type="button" value="webSocket_disconnection" onclick="disconnect02()"/>
<input type="button" value="webSocket_send" onclick="send02()"/>


<br>-----------------用户操作-------------------<br>
<input value="用户添加" type="button" id="user_add" onclick="user_add()"/><br>
<input value="用户删除" type="button" id="user_del" onclick="user_del()"/><br>
<input value="密码修改" type="button" id="user_cpwd" onclick="user_cpwd()"/><br>
<input value="查询指定用户名" type="button" id="user_selectbyname" onclick="user_selectbyname()"/><br>
<input value="查询所有" type="button" id="user_selectall" onclick="user_selectall()"/><br>
<input value="用户修改" type="button" id="user_update" onclick="user_update()"/><br>


<br>-----------------设备操作-------------------<br>
<input value="设备添加" type="button" id="device_add" onclick=""/><br>
<input value="设备删除" type="button" id="device_del" onclick=""/><br>
<input value="查询指定设备" type="button" id="device_selectbyname" onclick=""/><br>
<input value="查询所有设备" type="button" id="device_selectall" onclick=""/><br>
<input value="设备修改" type="button" id="device_update" onclick=""/><br>


</body>
</html>
