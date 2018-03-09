<#import "/manage/tpl/pageBase.ftl" as page>
<@page.pageBase currentMenu="门户滚动图片">
	<form action="${basepath}/manage/indexImg" theme="simple" enctype="multipart/form-data">
		<span id="pifeSpan" class="input-group-addon" style="display:none">${systemSetting().imageRootPath}</span>
		<table class="table table-bordered">
			<tr style="background-color: #dff0d8">
				<td colspan="2" style="background-color: #dff0d8;text-align: center;">
					<strong> 门 户 图 片 编 辑 </strong>
				</td>
			</tr>
			<tr style="display: none;">
				<th>id</th>
				<td><input type="hidden" value="${e.id!""}" name="id" label="id" id="idd"/></td>
			</tr>
			<tr>
				<th class="right">标题</th>
				<td style="text-align: left;"><input type="text"  value="${e.title!""}" name="title"  data-rule="标题:required;title;length[1~45];"
						id="title" /></td>
			</tr>
			<tr>
				<th>图片地址</th>
				<td style="text-align: left;" colspan="3">
					<input type="button" name="filemanager" value="浏览图片" class="btn btn-warning"/>
					<input type="text"  value="${e.picture!""}" name="picture"  id="picture" ccc="imagesInput" style="width: 600px;" data-rule="图片地址:required;picture;" />
					<#if e.picture??>
						<a target="_blank" href="${systemSetting().imageRootPath}/${e.picture!""}">
							<img style="max-width: 50px;max-height: 50px;" alt="" src="${systemSetting().imageRootPath}/${e.picture!""}">
						</a>
					</#if>
				</td>
			</tr>
			<tr>
				<th>广告链接</th>
				<td style="text-align: left;">
					<input type="text"  value="${e.link!""}" name="link"  id="link" />
				</td>
			</tr>
			<tr>
				<th>排序</th>
				<td style="text-align: left;"><input type="text"  value="${e.order1!""}" name="order1"  data-rule="排序:integer;order1;length[1~5];"
						id="order1" /></td>
			</tr>
			<tr>
				<th>描述</th>
				<td style="text-align: left;"><input type="text"  value="${e.desc1!""}" name="desc1"  data-rule="排序:desc1;length[1~100];"
						id="desc1" /></td>
			</tr>
			<tr>
				<td colspan="2" style="text-align: center;">
					<#if e.id??>
                        <button method="update" class="btn btn-success">
                            <i class="icon-ok icon-white"></i> 保存
                        </button>
					<#else>
                        <button method="insert" class="btn btn-success">
                            <i class="icon-ok icon-white"></i> 新增
                        </button>
					</#if>
			</tr>
		</table>
		<br>
                <table class="table table-bordered">
                    <tr style="background-color: #dff0d8">
                        <td>新增图片 </td>
                    </tr>
                    <tr>
                        <td>
                            <input id="uploadify" name="uploadify" value="添加" class="btn btn-warning" type="button"/></td>
                    </tr>
                    <tr id="firstTr" style="display:none">
                        <td>
                                <#--<input type="button" name="filemanager" value="浏览图片" class="btn btn-warning"/>-->
                                    <img name="img"  style="width:50px;height:50px;max-width: 50px;max-height: 50px;">
                                <#--<input type="text" ccc="imagesInput" name="images" style="width: 260px;" readonly="readonly"/>-->
                        </td>
                    </tr>
                </table>
	</form>

<link rel="stylesheet" href="${basepath}/resource/uploadify/uploadify.css"  type="text/css">
<script type="text/javascript" src="${basepath}/resource/uploadify/jquery.uploadify.min.js"></script>	
<script>
    $(document).ready(function() {
        var url = '${basepath}/common/uploadify.do';
        $("#uploadify").uploadify({
           'swf'         : '${basepath}/resource/uploadify/uploadify.swf',
           'uploader'       : url,//后台处理的请求
           'queueID'        : 'fileQueue',//与下面的id对应
           'multi'          : true,
           'buttonText'     : '本地上传',
           onUploadSuccess:function(file, data, response){
               data = $.parseJSON(data);
               if(data.error == '1') {
                   alert("上传失败：\n失败原因:" + data.msg);
               } else {
                    var $tr = $("#firstTr").clone();
                   $tr.find("img[name=img]").attr("src", "${systemSetting().imageRootPath}" + data.filePath);
                   $tr.find(":input[name=images]").val(data.filePath);
                   $("#firstTr").parent().append($tr);
                   $tr.show();
               }
           },
           onUploadError:function(file, errorCode, errorMsg) {
               alert("上传失败,data="+data+",file="+file+",response="+response);   
           }
        });
    });



KindEditor.ready(function(K) {
	var editor = K.editor({
		fileManagerJson : '${basepath}/editor/fileManager'
	});
	K('input[name=filemanager]').click(function() {
		var imagesInputObj = $(this).parent().children("input[ccc=imagesInput]");
		editor.loadPlugin('filemanager', function() {
			editor.plugin.filemanagerDialog({
				viewType : 'VIEW',
				dirName : 'image',
				clickFn : function(url, title) {
					//K('#picture').val(url);
					//alert(url);
					imagesInputObj.val(url);
					editor.hideDialog();
					clearRootImagePath(imagesInputObj);//$("#picture"));
				}
			});
		});
	});
	
});
</script>
</@page.pageBase>