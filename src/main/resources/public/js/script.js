/**
 * Created by linzhouzhi on 2018/3/12.
 */
$(function () {

    var address = getUrlParam('address');
    if(isEmpty(address) || !isValidAddress(address)){
        layer.msg("Zookeeper address is null or not correct!");
        $('#myModal').modal({
            keyboard: true
        });
    } else {
        window.address = address;
        addAddressList(window.address);
    }

    getAddressList();

    var path = getUrlParam('path');
    if( isEmpty(path)){
        path = "/";
    }

    $('#myTree').jstree({
        "core" : {
            "animation" : 0,
            "check_callback" : true,
            "themes" : { "stripes" : false },
            'data' : {
                'url' : function (node) {
                    return '/getAllPath?address=' + window.address + '&path=' + path;
                },
                'data' : function (node) {
                    return { 'id' : node.id };
                }
            }
        },
        "types" : {
            "default" : {
                "icon" : "glyphicon glyphicon-flash"
            },
            "leaf" : {
                "icon" : "glyphicon glyphicon-flash"
            },
            "tempLeaf" : {
                "icon" : "glyphicon glyphicon-flash"
            },
            "ellipsis" : {
                "icon" : "glyphicon glyphicon-flash"
            },
            "parent" : {
                "icon" : "glyphicon glyphicon-ok"
            }
        },
        "plugins" : [ "search","types","state" ]
    });

    $('#myTree').on("changed.jstree", function (e, data) {
        var path = data.selected[0];
        $("#input-path").val(path);
        $("#current-path").text(path);
        $.get("/getPathDetail?address=" + window.address + "&path=" + path,function(data){
            $("#node-detail").html(syntaxHighlight(data));
            $("#node-data").val(data.data);
            /*if (!isEmpty(data.data) && path.indexOf(".xml") != -1)  {
                var editor = CodeMirror.fromTextArea(document.getElementById("#node-data"), {
                    mode: "xml",
                    lineNumbers: true
                });
            }*/
            // $("#node-data").val( JSON.stringify(data["data "], null, 4) );
        });
    });

    $("#myTree").on("open_node.jstree", function (e, data) {
        var children = data.node.children;
        if( children.length == 1 && children[0].endsWith("/...") ){

            var url = '/getAllPath?address=' + window.address + '&path=' + data.node.id;
            //console.log( url );
            $.get(url, function(result){
                var ref = $("#myTree").jstree(true);
                ref.delete_node(children[0]);
                result.children.forEach(function (element) {
                    ref.create_node(data.node, element);
                })
            });
        }
    });

    $(".new-address").on("click", function () {
        $('#myModal').modal({
            keyboard: true
        })
    });

    $("#confirm").on("click", function () {
        var address = $("#address-input").val().trim();
        if (isEmpty(address)) {
            layer.msg("Address can't be null!");
        } else if (!isValidAddress(address)) {
            layer.msg("Address is not correct!");
        } else {
            window.address = address;
            window.location.href= window.location.href.split('?')[0] + '?address=' + window.address + '&path=' + path;
        }
    });

    $("body").delegate(".text", "click", function () {
        var address = $(this).text();
        if (isEmpty(address)) {
            layer.msg("Address can't be null!")
        } else {
            window.address = address;
            window.location.href= window.location.href.split('?')[0] + '?address=' + window.address + '&path=' + path;
        }
    });
});

var flag = false;
// 搜索框
$('#search-node').keyup(function () {
    if(flag) { clearTimeout(flag); }
    flag = setTimeout(function () {
        var searchValue = $('#search-node').val();
        $('#myTree').jstree(true).search(searchValue);
    }, 100);
});

$("#save-node").on("click", function () {
    var param = {};
    param.zkAddress = window.address;
    param.zkPath = $("#input-path").val();
    param.data = $("#node-data").val();
    $.ajax({
        type : "post",
        contentType: 'application/json',
        url : "/updateNode",
        data : JSON.stringify(param),
        dataType:'json',
        success : function(data){
            $("#node-detail").html(syntaxHighlight(data));
            window.location.reload();
            layer.msg("Update Success!");
        }
    });
});

$("#delete-node").on("click", function () {
    var param = {};
    var path = $("#input-path").val().trim();
    param.zkPath = path;
    param.zkAddress = window.address;
    if (isEmpty(path)) {
        layer.msg("Please choose one node.");
        return;
    }
    //询问框
    layer.confirm("Delete " + path + "?", {
        title: "Delete path",
        btn: ["Delete","Cancel"], //按钮
        skin: "my-skin",
        resize: false
    }, function(index){
        $.ajax({
            type : "post",
            contentType: 'application/json',
            url : "/deleteMode",
            data : JSON.stringify(param),
            dataType:'json',
            success : function(data){
                $("#node-detail").html(syntaxHighlight(data));
                layer.close(index);
                layer.msg("Delete Success!");
                window.location.reload();
            }
        });
    }, function(){
    });

});

function addAddressList(address) {
    var addressListStr = getCookie("addressList");
    if (!isEmpty(address) && addressListStr.indexOf(address) == -1) {
        addressListStr += "," + address;
        setCookie("addressList", addressListStr, 30);
    }
}

function getAddressList() {
    var addressListStr = getCookie("addressList");
    var addressList = addressListStr.split(",");
    for (var i = 0; i < addressList.length; i++) {
        var address = addressList[i];
        if (!isEmpty(address)) {
            $(".selectpicker").append("<option>" + address +"</option>>")
        }
    }
    $('.selectpicker').selectpicker();
}

