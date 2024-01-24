<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    

 <div class="d-flex justify-content-center">
	<div class="w-50">
		<h1>글 상세</h1>
		
		<input type="text" id="subject" class="form-control" placeholder="제목을 입력하세요." value="${post.subject }">
		<textarea id="content" class="form-control" placeholder="내용을 입력하세요." rows="10">${post.content }</textarea>
		
		<%-- 이미지가 있을 경우에만 영역 노출 --%>
		<c:if test="${not empty post.imagePath }"> 
			<div class="my-3">
				<img alt="업로드 된 이미지" src="${post.imagePath }" width="300">
			</div>
		</c:if>
		
		<div class="d-flex justify-content-end my-3">
			<input type="file" id="file" accept=".jpg, .png, .gif, .jpeg">
		</div>
		
		<div class="d-flex justify-content-between">
			<button type="button" id="deleteBtn" class="btn btn-secondary" data-post-id="${post.id }">삭제</button>
			
			<div>
				<a href="/post/post-list-view" class="btn btn-dark">목록</a>
				<button type="button" id="saveBtn" class="btn btn-info" data-post-id="${post.id }" >수정</button>
			</div>
		</div>
	
	</div>
</div>

<script>

	$(document).ready(function() {
		// 글 수정 버튼
		$("#saveBtn").on('click', function() {
			let postId = $(this).data("post-id");
			let subject = $("#subject").val().trim();
			let content = $("#content").val();
			let fileName = $("#file").val(); // C:\fakepath\cat-8361048_1280.jpg
			// alert(postId);
			
			// validation check
			if (!subject) {
				alert("제목을 입력하세요.");
				return;
			}
			if (!content) {
				alert("내용을 입력하세요.");
				return;
			}
			
			// 파일이 업로드 된 경우에만 확장자 체크 (null 허용)
			if (fileName) {
				// alert("파일이 존재한다.");
				// C:\fakepath\cat-8361048_1280.jpg
				// 확장자만 뽑은 후, 소문자로 모두 변경하여 검사한다.
				let extension = fileName.split(".").pop().toLowerCase();
				// alert(extension);
				
				if ($.inArray(extension, ['jpg', 'png', 'gif', 'jpeg']) == -1) {
					alert("이미지 파일만 업로드 할 수 있습니다.");
					$("#file").val(""); // 잘못 선택된 파일을 비운다.
					return;
				}
				
			}
			
			// 이미지를 업로드 할 때는 반드시 form 태그로 구성한다.
			let formData = new FormData();
			formData.append("postId", postId);
			formData.append("subject", subject);
			formData.append("content", content);
			formData.append("file", $("#file")[0].files[0]);
			
 			$.ajax ({
				// request
				type:"PUT"
				, url:"/post/update"
				, data:formData
				, enctype:"multipart/for-data" // 파일 업로드를 위한 필수 설정 
				, processData:false // 보내는 타입이 String이 아니라 form, 파일 업로드를 위한 필수 설정
				, contentType:false // 파일 업로드를 위한 필수 설정
				
				// response
				, success:function(data) {
					if (data.code == 200) {
						alert("메모가 수정되었습니다.");
						location.reload();
					} else {
						alert(data.error_message);
					}
				}
				, error:function(request, status, error) {
					alert("글을 수정하는데 실패했습니다.");
				}
				
			}); // savetBtn ajax 
			
		}); // savetBtn
		
 		$("#deleteBtn").on('click', function() {
			// alert("삭제버튼");
			
			let postId = $(this).data("post-id");
			// alert(postId);
			
			$.ajax ({
				
				//request
				type:"DELETE"
				, url:"/post/delete"
				, data:{"postId":postId}
				// response
				, success:function(data) {
					if(data.code == 200) {
						alert("글을 삭제하였습니다.");
						location.href="/post/post-list-view";
					} else {
						alert(data.error_message);
					}
				}
				, error:function(request, status, error) {
					alert("글을 삭제하는데 실패했습니다.");
				}
				
			}); // deleteBtn ajax
			
		});  // deleteBtn 
		
	}); // ready

</script>