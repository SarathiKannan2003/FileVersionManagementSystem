<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.FileManipulationDao.UploadedFilesDao" %>
<%@ page import="com.FileManipulationDao.User" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>LoginHomePage</title>
<style type="text/css">
h3{
text-align:center;
color:purple;
}
.HomePage{
background-color:#fef0fe;
float:right;
width: 200px;
padding:10px;
border:2px solid blue;
}
html{
  background-color:#c7fdff;
}

.topnav {
  overflow: hidden;
  background-color: #333;
}

.topnav a {
  float: left;
  color:  white;
  background-color: #04AA6D;
  text-align: center;
  padding: 14px 16px;
  text-decoration: none;
  font-size: 17px;
}
.topnav-right {
  float: right;
}
.file{
background-color:#ffe6e6;
padding:10px;
border:2px solid pink;
}
.fileImage{
width:35px;
}
p{
font-size: 13px;
}
.open{
background-color:blue;
color:white;
width:150px;
height:30px;
}
.delete{
background-color:red;
color:white;
width:150px;
height:30px;
}
.download{
background-color:green;
color:white;
width:150px;
height:30px;
}
#uploadStatus
{
    position: absolute; 
    right: 20px;
}

</style>
</head>
<body>
<%!
String userName="";
int userId;
%>
<%
response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
HttpSession getSession=request.getSession();
if(getSession.getAttribute("userName")==null) response.sendRedirect("index.jsp");
userName=(String) getSession.getAttribute("userName");
userId=(int) getSession.getAttribute("userId");
%>
<h1 hidden="hidden"><%=userId %></h1>
<div class="topnav">
  <a  href="logOut.jsp">Home</a>
  <div class="topnav-right">
  <a  href="logOut.jsp">Log Out</a>
  </div>
</div><br>
<div style="background-color:#efdff2;">
<div class="HomePage" >  
  <button><img src="images/fileImage.png" alt="uploadFile" width=40px></button><br>
  <input type="file" id="fileInput" name="file" required multiple><br>
  <label for="FileName" style="font-size:14px;">File Name:</label>
  <input type="text" name="FileName" id="File_Name" required readonly><br><br>
  <button type="submit" id="submit" style="height:27px;width:180px;background-color:green;color:white;" onclick="uploadFile()">Upload a NewFile</button>
</div><br>
<h3 id="welcomeMessage">✽ WELCOME <%=userName%> ✽</h3>
<h4 style="color:red;text-align:center;">Total Storage Used: 0.00 B</h4>
<h4 style="color:brown;text-align:center;">Overall File Size: 0.00 B</h4>
<h4 style="color:brown;text-align:center;">   &nbsp</h4>
</div>

<h4 style="color:brown;font-style:italic;font-size:bold;">Here The List Of Already uploaded files by You -----</h4>
<span id="uploadStatus" style="background-color:white;color:brown;"></span>

<script type="text/javascript">

function getStorageDetails(){
	 const userId = document.getElementsByTagName("h1")[0].innerHTML;
	 fetch('StorageDetailsServlet', {
         method: 'POST',
         headers: {
             'Content-Type': 'application/json'
         },
         body:JSON.stringify({"userId":+userId})
     })
 .then(response => response.json())
 .then(data => {
 	document.getElementsByTagName("h4")[0].innerHTML="Total Storage Used: "+data.userStorage;
 	document.getElementsByTagName("h4")[1].innerHTML="Overall File Size: "+data.overAllFileSize;
 })
 
 .catch(error => console.error('Upload error:', error));
}

var divIndex=0;
window.onload=function(){
	getStorageDetails();
    uploadedFilesList(0);
}
let viewHeight=1000;
let pageNo=0;
window.onscroll = function() {scrollFunction()};
function scrollFunction() {
  if (document.documentElement.scrollTop > viewHeight) {
	  uploadedFilesList(pageNo);
	  viewHeight+=1500;
  }
}
let onScroll=true;
function uploadedFilesList(pageNumber){
	if(onScroll==false) return;
	  const userId = document.getElementsByTagName("h1")[0].innerHTML;
      const userData = {
          "userId": +userId,
          "pageNo":pageNumber
      };
      fetch('FileListServlet', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json'
          },
          body: JSON.stringify(userData)
      })
      .then(response=>response.json())
      .then(data=>{
    	  for(let i=0;i<data.files.length;i++){
    		  var fileDetails=data.files[i];
    	      createDiv(fileDetails.fileId,fileDetails.fileName,fileDetails.createdTime,fileDetails.fileSize,fileDetails.fileStorage,divIndex++);

    	  }
    	  if(data.files.length<10) {
    		  onScroll=false;
    		  return;
    	  }
    	  pageNo=+data.files[data.files.length-1].fileId;  	 
      })
}
    document.getElementById('fileInput').addEventListener('change', function(event) {
    	var allFiles=event.target.files;
    	let fileName="";
    	for(let i=0;i<allFiles.length;i++){
    		 if(i==allFiles.length-1)  fileName+=allFiles[i].name;
    		 else fileName+=allFiles[i].name+",";
    	}
    	document.getElementById("File_Name").setAttribute("value",fileName);
    	});
    
 function uploadFile() {
	    let fileInput = document.getElementById('fileInput');
	    var allFiles=fileInput.files;
        if (!allFiles.length) {
            alert("Please select a file");
            return;
        }
        let j=0;
        document.getElementById("uploadStatus").innerHTML=" *"+allFiles[0].name+"* is Uploading........";
        for(let i=0;i<allFiles.length;i++){
        	let file = allFiles[i];
            fetch('UploadServlet?fileName=' + encodeURIComponent(file.name), {
                method: 'POST',
                headers: { 'Content-Type': 'application/octet-stream+fileName=' }, 
                body:file
                 })
        .then(response => response.json())
        .then(data => {
        	let fileExist=JSON.parse(data.fileExist);
        	 if(data.fileExist==true){
   			  updateDiv(data.fileId,data.fileSize,data.fileStorage);
   		     }
   		    else{
   			  createDiv(data.fileId,data.fileName,data.createdTime,data.fileSize,data.fileStorage,divIndex++);
   		    }
        	
        	if(j==allFiles.length-1)  document.getElementById("uploadStatus").innerHTML=" All Files Were Uploaded!";
        	else document.getElementById("uploadStatus").innerHTML=" *"+allFiles[j+1].name+"* is Uploading........";
        	getStorageDetails();
        	j++;
        })
        .catch(error => console.error('Upload error:', error));
        }    
    }
 
    function updateDiv(fileId,fileSize,fileStorage){
    
        let divTags=document.getElementsByClassName("file");
        let divTagsCount = divTags.length;
        for(let i=0;i<divTagsCount;i++){
        	let span=divTags[i].querySelector("span");
        	if(span.innerHTML==fileId){
        		let pTags=divTags[i].querySelectorAll("p");
        		pTags[0].innerHTML="File Size: "+fileSize;
        		pTags[2].innerHTML="Storage Used: "+fileStorage;
        		break;
        	};
        }      
}
    
    function createDiv(fileId,fileName,createdTime,fileSize,fileStorage,divIndex){	
              let newDiv = document.createElement("div");
              newDiv.className = "file";
              let image=document.createElement("img");
              image.src="images/folder.png";
              image.className = "fileImage";
              let hTag=document.createElement("h5");
              hTag.innerHTML=fileName;
              let pTag1=document.createElement("p");
              pTag1.innerHTML="File Size: "+fileSize;
              let pTag2=document.createElement("p");
              pTag2.innerHTML="Created Time: "+createdTime;
              let pTag3=document.createElement("p");
              pTag3.innerHTML="Storage Used: "+fileStorage;
              let spanTag=document.createElement("span");
              spanTag.innerHTML=fileId;
              spanTag.hidden="hidden";
         
              let openButton = document.createElement("button");
              openButton.textContent = "Open";
              openButton.className = "open";
              openButton.onclick = function(){openFile(divIndex,fileId);};

              let downloadButton = document.createElement("button");
              downloadButton.textContent = "Download";
              downloadButton.className = "download";
              downloadButton.onclick = function(){downloadFile(divIndex,fileId,fileName);};

              let deleteButton = document.createElement("button");
              deleteButton.textContent = "Delete";
              deleteButton.className = "delete";
              deleteButton.onclick = function(){deleteFile(divIndex,fileId);};
        
              newDiv.appendChild(image);
              newDiv.appendChild(hTag);
              newDiv.appendChild(pTag1);
              newDiv.appendChild(pTag2);
              newDiv.appendChild(pTag3);
              newDiv.appendChild(spanTag);
              newDiv.appendChild(openButton);
              newDiv.appendChild(downloadButton);
              newDiv.appendChild(deleteButton);
 
              let breakTag = document.createElement("br");
              document.body.appendChild(newDiv);
              document.body.appendChild(breakTag);    
    }
    
    function openFile(index,fileId) {	
        const userData = {
            "fileId": +fileId,
        };
               
        fetch('OpenFileServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
        .then(response=>response.json())
        .then(data => {
        	window.location.href="UploadedFiles.jsp";
        })
        .catch(error => console.error('Error:', error));
    }
    
    function downloadFile(index,fileId,fileName) {

        fetch('DownloadFileServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ "fileId": +fileId })
        })
        .then(response => response.blob()) 
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName; 
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        })
        .catch(error => console.error('Download error:', error));
}
    
    function deleteFile(index,fileId) {
        const userData = {
            "fileId": +fileId,
        };
        fetch('DeleteFileServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
         .then(response=>response.json())
      .then(data=>{
            getStorageDetails(); 
            let divTags=document.getElementsByClassName("file");
            if(data.status=="allFiles"){
            divTags[index].style.display="none";
            }
            let pTags=divTags[index].querySelectorAll("p");
    		pTags[0].innerHTML="File Size: "+data.fileSize;
    		pTags[2].innerHTML="Storage Used: "+data.fileStorage;
    	    alert("Deleted Successfully");
      })
        .catch(error => console.error('Error:', error));
    }
</script>
</body>
</html>