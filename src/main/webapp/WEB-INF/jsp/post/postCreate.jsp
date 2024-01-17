<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<div class="d-flex justify-content-center">
	<div class="w-50">
		<h1>글쓰기</h1>
		
		<input type="text" id="subject" class="form-control" placeholder="제목을 입력하세요.">
		<textarea id="content" class="form-control" placeholder="내용을 입력하세요." rows="10"></textarea>
		
		<div class="d-flex justify-content-end my-3">
			<input type="file" id="file" accept=".jpg, .png, .gif, .jpeg">
		</div>
		
		<div d-flex justify-content-between>
			<button type="button" id="postListBtn" class="btn btn-dark" >목록</button>
			
			<div class="d-flex">
				<button type="button" id="clearBtn" class="btn btn-secondary" >모두 지우기</button>
				<button type="button" id="savetBtn" class="btn btn-info" >저장</button>
			</div>
		</div>
		
	</div>
</div>

<script>

	$(document).ready(function() {
		// 목록 버튼 클릭 => 목록 화면 이동
		$("#postListBtn").on('click', function() {
			location.href = "/post/post-list-view";
		});
		
		// 모두 지우기 버튼 클릭
		$("#clearBtn").on('click', function() {
			// alert("모두 지우기");
			$("#subject").val("");
			$("#content").val("");
		});
		
		// 글 저장 버튼
		$("#savetBtn").on('click', function() {
			// alert("글 저장");
			let subject = $("#subject").val().trim();
			let content = $("#content").val();
			let fileName = $("#file").val(); // C:\fakepath\cat-8361048_1280.jpg
			// alert(fileName);
			
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
			
			// form 태그를 js에서 만든다. 
			// 이미지를 업로드 할 때는 반드시 form 태그가 있어야 한다.
			let formData = new FormData();
			formData.append("subject", subject); // key는 name 속성과 같다. Request Parameter
			formData.append("content", content);
			formData.append("file", $("#file")[0].files[0]); // #file중 첫번째 이미지, files[0]=한개만 선택
			
			// AJAX
			$.ajax({
				// request
				type:"POST"
				, url:"/post/create"
				, data:formData
				, enctype:"multipart/form-data" // 파일 업로드를 위한 필수 설정
				, processData:false // 파일 업로드를 위한 필수 설정
				, contentType:false // 파일 업로드를 위한 필수 설정
				
				// response
				, success:function(data) {
					if (data.code == 200) {
						alert("메모가 저장되었습니다.");
						location.href = "/post/post-list-view";
					} else {
						alert(data.error_message);
					}
				}
				, error: function(request, status, error) {
					alert("글을 저장하는데 실패했습니다.");
				}
				
			}); // ajax
			
		}); // saveBtn
		
	});

</script>