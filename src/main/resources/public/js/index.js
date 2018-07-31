/**
 * Created by lzz on 2018/3/12.
 */

$("#save-node").click(function () {
    var reqdata = {};
    reqdata.zk = window.zk;
    reqdata.path = $("#input-path").val();
    reqdata.data = $("#node-data").val();
    $.ajax({
        type : "post",
        contentType: 'application/json',
        url : "/update_node",
        data : JSON.stringify(reqdata),
        dataType:'json',
        success : function(data){
            console.log(data);
            $("#node-detail").html(syntaxHighlight(data));
        }
    });
});

$("#delete-node").click(function () {
    var reqdata = {};
    reqdata.path = $("#input-path").val();
    reqdata.zk = window.zk;
    $.ajax({
        type : "post",
        contentType: 'application/json',
        url : "/delete_node",
        data : JSON.stringify(reqdata),
        dataType:'json',
        success : function(data){
            console.log(data);
            $("#node-detail").html(syntaxHighlight(data));
        }
    });
});

$(function () {
    var to = false;
    $('#search-node').keyup(function () {
        if(to) { clearTimeout(to); }
        to = setTimeout(function () {
            var v = $('#demo_q').val();
            $('#jstree').jstree(true).search(v);
        }, 250);
    });

    var path = getUrlParam('path');
    if( typeof path == "undefined" || path == null ){
        path = "/";
    }
    var zk = getUrlParam('zk');
    if( typeof zk == "undefined" || zk == null ){
        alert("zk is null");
    }
    window.zk = zk;
    $('#jstree').jstree({
        "core" : {
            "animation" : 0,
            "check_callback" : true,
            "themes" : { "stripes" : false },
            'data' : {
                'url' : function (node) {
                    return '/get_all_path_ajax?zk=' + window.zk + '&path=' + path;
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
            "tmp-leaf" : {
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


    $('#jstree').on("changed.jstree", function (e, data) {
        var path = data.selected[0];
        $("#zk-path").text(path);
        $("#input-path").val(path);
        console.log(path);
        $.get("/get_path_detail_ajax?zk=" + window.zk + "&path=" + path,function(data){
            $("#node-detail").html(syntaxHighlight(data));
            $("#node-data").val( JSON.stringify(data["data"], null, 4) );
        });
    });

    $("#jstree").on("open_node.jstree", function (e, data) {
       var children = data.node.children;
        if( children.length == 1 && children[0].endsWith("/...") ){

            var url = '/get_all_path_ajax?zk=' + window.zk + '&path=' + data.node.id;
            console.log( url );
            $.get(url, function(result){
                var ref = $("#jstree").jstree(true);
                ref.delete_node(children[0]);
                result.children.forEach(function (element) {
                    ref.create_node(data.node, element);
                })
            });
        }
    });

});

