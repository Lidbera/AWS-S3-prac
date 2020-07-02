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
				size = $('#file-upload')[0].files[0].size;
				if(size > 3000000){
					alert('3MB를 초과하는 파일은 올릴 수 없습니다.');
					return false;
				}
			});
			$('#form-delete').submit(function(){
				if($('#filename-delete').val().len <= 0){
					alert('이름을 입력해주세요.')
					return false;
				}
			});
		});
	</script>
</head>
<body>
<form id="form-upload" action="upload" method="post" enctype="multipart/form-data">
	<input id="file-upload" type="file" name="file">
	<input type="submit" value="업로드">
</form>
<hr>
<form id="form-path" action="getpath">
	<input id="filename-path" name="name" type="text">
	<input type="submit" value="경로 받아오기">
</form>
<hr>
<form id="form-delete" action="delete">
	<input id="filename-delete" name="name" type="text">
	<input type="submit" value="삭제">
</form>
</body>
</html>
