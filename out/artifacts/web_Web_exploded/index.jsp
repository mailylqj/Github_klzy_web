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
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
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
            reg.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
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
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
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
            config.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            config.filename = "klzy004"
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
            control.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
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
            mdata.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            mdata.uid = "0023558B2178"
//            mdata.startTime = 1506757210000
//            mdata.endTime =   1506767098000

            mdata.startTime = 1509759459000;
            mdata.endTime =   1509959459000;

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
            var mdata = {};
            mdata.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/ModDataNext",
                contentType: "application/json",
                dataType: "json",
                data:JSON.stringify(mdata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function ModData_Prev() {
            var mdata = {};
            mdata.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/ModDataPrev",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(mdata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function ModData_Page() {
            var mdata = {};

            mdata.page = 3;
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/ModDataPage",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(mdata),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function readControlData() {
            var cdata = {};
            cdata.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            cdata.uid = "0023558B2178"
            cdata.startTime = 1506757210000
            cdata.endTime =   1506768610000

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
            adata.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
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
        var url_01 = 'ws://' + window.location.host + '/ws/socket/0023558B2178';
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
        var url_02 = 'ws://' + window.location.host + '/ws/socket/0023558B213A';
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
               // alert('Received: ' + event.data);
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
            user.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            user.name = "add_test";
            user.password = "12345";
            user.type = 1;
            user.pro_company_id = 100000;
            user.use_company_id = 100000;
            user.level = 9;

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
            user.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            user.namelist = ["100001", "100002"];
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
            user.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
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
            user.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
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

        function user_selectbyid() {
            var user = {};
            user.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            user.id = 100001;
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userSelectByID",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function user_selectall() {
            var user = {};
            user.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userSelectAll",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function user_update() {
            //level  1 用户操作员 2 用户管理员  3 厂家操作员 4 厂家管理员
            var user = {};
            user.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            user.id = 100001;
            user.name = "add_test";
            user.type = 1;
            user.pro_company_id = 100000;
            user.use_company_id = 100005;
            user.level = 9;

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
            //  1-- 设备id  2-- 设备类型(0:dtu 1 HMI)  3-- 采集数据长度  4-- 循环间隔时间
            //  5-- 使用厂家 6--经度  7--纬度 8--设备名 9--配置文件名
            var device = {};
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            device.device_id = "002355892C15";
            device.type = 1;
            device.cmd_len = 30;
            device.looptime = 2;
            device.pro_company_id = 100000;
            device.use_company_id = 100000;
            device.longitude = 23.2293;
            device.latitude = 104.332;
            device.devicename = "测试设备12";
            device.configfilename = "klzy213";
            device.ischeck = 1;
            device.allow_date = 1506768610000;

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
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            device.id_list = [100025];
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


        function device_selectbyid() {
            var device = {};
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            device.id = 100000;
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceSelectByID",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_selectbydeviceid() {
            var device = {};
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            device.device_id = "002355892C10";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceSelectByDeviceID",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_selectall() {
            var device = {};
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceSelectAll",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_select_unallow() {
            var device = {};
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceSelect_UNAllow",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_update() {
            //level  1 用户操作员 2 用户管理员  3 厂家操作员 4 厂家管理员
            var device = {};
            device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            device.id  = 100025;
            device.device_id = "002355892C18";
            device.type = 1;
            device.cmd_len = 30;
            device.looptime = 2;
            device.pro_company_id = 100000;
            device.use_company_id = 100000;
            device.longitude = 23.2293;
            device.latitude = 104.332;
            device.devicename = "测试设备555";
            device.configfilename = "klzy213";
            device.ischeck = 1;
            device.allow_date = 1508469071000;

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

    <%--procompany 操作--%>
    <script>
        function procompany_add() {
            var company = {};
            company.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            company.pro_company = "qlly";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/proCompanyAdd",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(company),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function procompany_del() {
            var company = {};
            company.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            company.id_list = [100001];
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/proCompanyDel",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(company),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function procompany_update() {
            var company = {};
            company.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            company.id  =  100002;
            company.pro_company = "qlly2";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/proCompanyUpdate",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(company),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function procompany_selectall() {
            var company = {};
            company.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/proCompanySelectAll",
                contentType: "application/json",
                dataType: "json",
                data:JSON.stringify(company),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }
    </script>


    <%--usecompany 操作--%>
    <script>
        function usecompany_add() {
            var company = {};
            company.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            company.use_company = "1213123";
            company.pro_company_id = 100000;

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/useCompanyAdd",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(company),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function usecompany_del() {
            var company = {};
            company.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            company.id_list = [100001,100002];
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/useCompanyDel",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(company),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function usecompany_update() {
            var company = {};
            company.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            company.id  =  100005;
            company.use_company = "klzy002";
            company.pro_company_id = 100002;
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/useCompanyUpdate",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(company),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function usecompany_selectbyprocompanyid() {
            var company = {};
            company.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            company.pro_company_id = 100000;
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/useCompanySelectByProCompanyID",
                contentType: "application/json",
                dataType: "json",
                data:JSON.stringify(company),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }
    </script>



    <%-- 用户和设备关联--%>
    <script>
        function user_device_select() {
            var user_device = {};
            user_device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            user_device.user_id = 100000;
            user_device.pro_company_id = 100000;

            user_device.level = 9;

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userDeviceSelect",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user_device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function user_device_update() {
            var user_device = {};
            user_device.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            user_device.user_id = 100000;
            user_device.chose_list = [100000,100001,100002,100003,100004,100005,100020];

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/userDeviceUpdate",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(user_device),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

    </script>

    <%-- 设备和imei关联--%>
    <script>
        function device_imei_select() {
            var device_imei = {};
            device_imei.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            device_imei.device_id = 100000;
            device_imei.pro_company_id = 100000;

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceImeiSelect",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device_imei),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function device_imei_update() {
            var device_imei = {};
            device_imei.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            device_imei.device_id = 100000;
            device_imei.imei_list =[100003,100004,100005,100006];

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/deviceImeiUpdate",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(device_imei),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }


        // 对于imei添加  根据用户权限 仅能选取
        function imei_add() {
            var imei = {};
            imei.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            imei.imei = "354882070085224";
            imei.pro_company_id =100000;
            imei.use_company_id =100000;
            imei.info = "info";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/imeiAdd",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(imei),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function imei_del() {
            var imei = {};
            imei.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            imei.imei_list = [100018];

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/imeiDel",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(imei),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function imei_update() {
            var imei = {};
            imei.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            imei.id  = 100018;
            imei.imei = "354882070085229";
            imei.pro_company_id =100000;
            imei.use_company_id =100000;
            imei.info = "info";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/imeiUpdate",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(imei),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function imei_selectbyid() {
            var imei = {};
            imei.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            imei.id = 100001;

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/imeiSelectByID",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(imei),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function imei_selectbyimei() {
            var imei = {};
            imei.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            imei.imei = "354882070085223";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/imeiSelectByImei",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(imei),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function imei_selectall() {
            var imei = {};
            imei.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/imeiSelectAll",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(imei),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

    </script>

    <script>

        function getLevel() {
            var level = {};
            level.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/getLevel",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(level),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

    </script>

    <%-- 配置文件--%>
    <script>

        // 对于config添加  根据用户权限 仅能选取
        function config_add() {
            var config = {};
            config.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            config.filename = "test005";
            config.pro_company_id =100000;
            config.use_company_id =100000;
            config.type = 2;
            config.data_name = "压力";
            config.data_units  = "pa";
            config.data_type = 1;
            config.data_decimal= 1;
            config.data_rwtype = 1;
            config.data_level= 2;
            config.data_showtype = 100002;
            config.data_max = 500;
            config.data_min = 0;

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/configAdd",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(config),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function config_del() {
            var config = {};
            config.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            config.id_list = [100037];

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/configDel",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(config),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function config_update() {
            var config = {};
            config.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            config.id  = 100037;
            config.filename = "test005";
            config.pro_company_id = 100000;
            config.use_company_id = 100000;
            config.type = 2;
            config.data_name = "压力2";
            config.data_units  = "pa";
            config.data_type = 1;
            config.data_decimal= 1;
            config.data_rwtype = 1;
            config.data_level= 2;
            config.data_showtype = 100002;
            config.data_max = 500;
            config.data_min = 0;

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/configUpdate",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(config),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function config_selectbyfilename() {
            var config = {};
            config.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            config.filename = "klzy004";

            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/configSelectByFilename",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(config),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function config_selectbydataname() {
            var config = {};
            config.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            config.filename = "klzy004";
            config.data_name = "压力";
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/configSelectByDataname",
                contentType: "application/json",
                dataType: "json",
                data:JSON.stringify(config),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }

        function config_selectbyid() {
            var config = {};
            config.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew";
            config.id = 100000;
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/configSelectByID",
                contentType: "application/json",
                dataType: "json",
                data:JSON.stringify(config),
                success: function (jsonResult) {
                    alert(jsonResult.result);
                }
            });
        }


        function config_showtype_select() {
            var config = {};
            config.token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew"
            $.ajax({
                type: "POST",
                url: "<%=basePath%>ajax/configShowtypeSelect",
                contentType: "application/json",
                dataType: "json",
                data:JSON.stringify(config),
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



<br>----------------- 生产公司 操作 （只有权限为9的用户才能操作） -------------------<br>
<input value="公司添加" type="button" id="procompany_add" onclick="procompany_add()"/><br>
<input value="公司删除" type="button" id="procompany_del" onclick="procompany_del()"/><br>
<input value="公司修改" type="button" id="procompany_update" onclick="procompany_update()"/><br>
<input value="查询所有生产公司" type="button" id="procompany_selectall" onclick="procompany_selectall()"/><br>

<br>----------------- 最终使用公司 操作-------------------<br>
<input value="最终使用公司添加" type="button" id="usecompany_add" onclick="usecompany_add()"/><br>
<input value="最终使用公司删除" type="button" id="usecompany_del" onclick="usecompany_del()"/><br>
<input value="最终使用公司修改" type="button" id="usecompany_update" onclick="usecompany_update()"/><br>
<input value="查询所有使用公司（权限为9是根据选中的 生产公司id 来获取，权限为4是根据本身登录人员的 生产公司id来获取）" type="button" id="usecompany_selectall" onclick="usecompany_selectbyprocompanyid()"/><br>


<br>-----------------用户操作-------------------<br>
<input value="用户添加" type="button" id="user_add" onclick="user_add()"/><br>
<input value="用户删除" type="button" id="user_del" onclick="user_del()"/><br>
<input value="密码修改" type="button" id="user_cpwd" onclick="user_cpwd()"/><br>
<input value="查询指定用户名" type="button" id="user_selectbyname" onclick="user_selectbyname()"/><br>
<input value="查询指定id" type="button" id="user_selectbyid" onclick="user_selectbyid()"/><br>
<input value="查询所有" type="button" id="user_selectall" onclick="user_selectall()"/><br>
<input value="用户修改" type="button" id="user_update" onclick="user_update()"/><br>


<br>-----------------设备操作-------------------<br>
<input value="设备添加" type="button" id="device_add" onclick="device_add()"/><br>
<input value="设备删除" type="button" id="device_del" onclick="device_del()"/><br>
<input value="查询指定设备通过device_ID" type="button" id="device_selectbyname" onclick="device_selectbydeviceid()"/><br>
<input value="查询指定设备通过ID" type="button" id="device_selectbyid" onclick="device_selectbyid()"/><br>
<input value="查询所有设备" type="button" id="device_selectall" onclick="device_selectall()"/><br>
<input value="设备修改" type="button" id="device_update" onclick="device_update()"/><br>

<br>----------------- imei的相关操作-------------------<br>
<input value="imei增" type="button" id="imei_add" onclick="imei_add()"/><br>
<input value="imei删" type="button" id="imei_del" onclick="imei_del()"/><br>
<input value="imei改" type="button" id="imei_update" onclick="imei_update()"/><br>
<input value="imei查by_id" type="button" id="imei_selectbyid" onclick="imei_selectbyid()"/><br>
<input value="imei查by_imei" type="button" id="imei_selectbyimei" onclick="imei_selectbyimei()"/><br>
<input value="imei查all" type="button" id="imei_selectall" onclick="imei_selectall()"/><br>


<br>----------------- 配置文件添加 ------------------<br>
<input value="config增" type="button" id="config_add" onclick="config_add()"/><br>
<input value="config删" type="button" id="config_del" onclick="config_del()"/><br>
<input value="config改" type="button" id="config_update" onclick="config_update()"/><br>
<input value="config查by_name" type="button" id="config_selectbydataname" onclick="config_selectbydataname()"/><br>
<input value="config查all" type="button" id="config_selectbyfilename" onclick="config_selectbyfilename()"/><br>
<input value="config查id" type="button" id="config_selectbyid" onclick="config_selectbyid()"/><br>



<br>-----------------设备与用户的关联操作-------------------<br>
<input value="选择能操作用户的用户名" type="button" id="user_device_getuser" onclick="user_selectall()"/><br>
<input value="获取该用户能管理的所有设备信息" type="button" id="user_device_select" onclick="user_device_select()"/><br>
<input value="用户和设备关联更新" type="button" id="user_device_update" onclick="user_device_update()"/><br>



<br>-----------------设备与imei的关联操作-------------------<br>
<input value="选择该用户能操作的设备" type="button" id="device_imei_getdevice" onclick="device_selectall()"/><br>
<input value="获取该设备能对应的imei信息" type="button" id="device_imei_select" onclick="device_imei_select()"/><br>
<input value="设备和imei关联更新" type="button" id="device_imei_update" onclick="device_imei_update()"/><br>


<br>-----------------  权限 对应 -------------------<br>
<input value="权限列表" type="button" id="getlevel" onclick="getLevel()"/><br>



</body>
</html>
