<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$('#form-upload').submit(function(){
				if($('#file-upload').val() == ''){
					alert('파일이 없습니다.');
					return false;
				}
				size = $('#file-upload')[0].files[0].size;
				if(size > 3000000){
					alert('3MB를 초과하는 파일은 올릴 수 없습니다.');
					return false;
				}
			});
			$('#form-delete').submit(function(){
				if($('#filename-delete').val().length <= 0){
					alert('이름을 입력해주세요.')
					return false;
				}
			});
			$('#btn-add').click(function(){
				count = $('#count').val();
				mydiv = "<input id='multifile"+count+"' type='file'>";
				$('#count').val(Number(count) + 1);
				$('#mydiv').append(mydiv);
			});
			$('#btn-multifile').click(function(){
				count = 0;
				form = new FormData();
				for(var i = 0; i < Number($('#count').val()); i++){
					if($('#multifile'+i).val() != ''){	
						file = $('#multifile'+i)[0].files[0];
						if(file.size > 3000000){
							alert('업로드 과정 중 3MB를 초과하는 파일이 제외되었습니다.');
							continue;
						}
						form.append("file"+count, file);
						count += 1;
					}
				}
				form.append("count", count);
				$.ajax({	
					type: 'POST',
					url: '/uploads',
					enctype: 'multipart/form-data',
					contentType: false,	
					processData: false,
					data: form
				});
				document.location.replace('/');
			});
		});
	</script>
</head>
<body>

<hr><br>

<h3>업로드</h3>
<form id="form-upload" action="upload" method="post" enctype="multipart/form-data">
	<input id="file-upload" type="file" name="file">
	<input type="submit" value="업로드">
</form>
<br><hr><br>

<h3>경로 받기</h3>
<form id="form-path" action="getpath">
	<input id="filename-path" name="name" type="text">
	<input type="submit" value="경로 받아오기">
</form>
<br><hr><br>

<h3>삭제</h3>
<form id="form-delete" action="delete">
	<input id="filename-delete" name="name" type="text">
	<input type="submit" value="삭제">
</form>
<br><hr><br>

<h3>ajax로 여러 개 올리기</h3>
<button id="btn-add">추가</button>  <button id="btn-multifile">업로드</button>
<input type="hidden" id="count" value="0">
<input type="hidden" id="url">
<div id="mydiv"><br></div>
<br><hr>

</body>
</html>
