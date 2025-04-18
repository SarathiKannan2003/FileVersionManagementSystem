<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.FileManipulationDao.FileVersionsDao" %>
<%@ page import="com.FileManipulationDao.UserForFileVersions" %> 
<%@ page import="com.Authentication.DaoMethods" %> 

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Uploaded Files</title>
<style type="text/css">

.open-btn {
  padding: 10px 20px;
  font-size: 18px;
  background: blue;
  color: white;
  border: none;
  cursor: pointer;
}

        .popup {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 20px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.3);
            border-radius: 8px;
            width: 300px;
            z-index: 1000;
        }

        .close-btn {
            background: red;
            color: white;
            border: none;
            padding: 5px 10px;
            cursor: pointer;
            float: right;
        }

        .popup form {
            display: flex;
            flex-direction: column;
        }

        .popup input {
            margin: 10px 0;
            padding: 8px;
            font-size: 16px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .popup button {
            padding: 10px;
            background: green;
            color: white;
            border: none;
            font-size: 16px;
            cursor: pointer;
        }
img{
width:40px;
}

.HomePage{
background-color:#fef0fe;
float:right;
width: 200px;
padding:10px;
border:2px solid blue;
}
.file{
background-color:#ffe6e6;
margin:auto;
padding:10px;
border:2px solid pink;
}
h3{
text-align:center;
color:purple;
}
.download{
width:100px;
height:30px;
background-color:green;
color:white;
font-size:15px;
}
.delete{
width:100px;
height:30px;
background-color:red;
color:white;
font-size:15px;
}
.topVersion{
width:180px;
height:30px;
background-color:#a249e6;
color:white;
font-size:15px
}
.lock{
width:160px;
height:30px;
background-color:#f54272;
color:white;
font-size:15px;
}
.overWrite{
width:220px;
height:30px;
background-color:#9e1657;
color:white;
font-size:15px;
}
.HomePage{
background-color:#fef0fe;
margin:auto;
width: 400px;
padding:10px;
border:2px solid blue;
}
p{
font-size: 13px;
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
  color: #f2f2f2;
  text-align: center;
  padding: 14px 16px;
  text-decoration: none;
  font-size: 17px;
}

.topnav-right {
  float: right;
}
</style>
</head>
<body>
<%!
String userName="";
int fileId;
String fileName="";
%>
<%
HttpSession getSession=request.getSession();
if(getSession.getAttribute("userName")==null) response.sendRedirect("index.jsp");
userName=(String) getSession.getAttribute("userName");
fileId=(int) getSession.getAttribute("fileId");
fileName=DaoMethods.getFileNameFromSql(fileId);
%>
<div class="topnav">
  <button  style="height:49px;background-color:green;color:white;font-size:17px;" onclick="history.back()">Go Back</button>
  <div class="topnav-right">
    <form action="logOut.jsp">
<button style="height:49px;background-color:green;color:white;font-size:17px;" type="submit" name="logOut">Log Out</button>
</form>
  </div>
</div><br>
<div class="HomePage" >  
  <button><img src="images/fileImage.png" alt="uploadFile" width=40px></button>
  <input type="file" id="FileInput" name="file" required multiple>
  <button type="submit" id="submit" style="height:27px;width:180px;background-color:green;color:white;" onclick="uploadFile()">Upload a NewFile</button>
</div><br><br><br>
<h1 hidden="hidden"><%=fileId %></h1>
<h2 hidden="hidden">0</h2>
<h3 style="color:brown;text-align:center;font-style:italic;font-size:bold">All The Versions of <%=fileName %></h3>   
 
 <div class="overlay" id="overlay"></div>
    <div class="popup" id="Popup" >
        <div>
            <label id ="popupHeading"></label>
            <h6 hidden="hidden"></h6>
            <h6 hidden="hidden"></h6>
            <h6 hidden="hidden"></h6>
            <label>Select File</label>
            <input type="file" id="fileInput" placeholder="file Name">
            <button type="submit" style="background-color:green;width:120px;" onclick="submitForm()">Over Write</button><br><br>
            <button type="submit" style="background-color:red;width:120px;" onclick="closePopup()">Cancel</button>
        </div>
    </div>             
<script type="text/javascript">

const fileId = document.getElementsByTagName("h1")[0].innerHTML;
window.onload=function(){
    uploadedFilesList(0);
}
let viewHeight=1000;
window.onscroll = function() {scrollFunction()};
function scrollFunction() {
  if (document.documentElement.scrollTop > viewHeight) {
	  uploadedFilesList(0);
	  viewHeight+=2000;
  }
}
let offSet=0;
let onScroll=1;
function uploadedFilesList(topVersionListing){
if(topVersionListing==0){
    var userData = {
			   "fileId": +fileId,
			   "offSet":offSet
    };
}
else{
	onScroll=1;
	var userData = {
	     "fileId": +fileId,
	     "offSet":-1
    };
}
if(onScroll==0) return;
fetch('FileVersionListingServlet', {
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
		  createDiv(fileDetails.fileId,fileDetails.fileName,fileDetails.version,fileDetails.createdTime,fileDetails.fileSize,
				  fileDetails.isLocked,i,topVersionListing);
	  }
	  if(data.files.length<10) {
		  onScroll=0;
		  return;
	  }
	  offSet+=1;
})
}
function createDiv(fileId,fileName,version,createdTime,storage,isLocked,divIndex,topVersionListing){	
    let newDiv = document.createElement("div");
    newDiv.className = "file";
    let image=document.createElement("img");
    image.src="images/file.png";
    let hTag=document.createElement("h5");
    hTag.innerHTML=fileName;
    let pTag1=document.createElement("p");
    pTag1.innerHTML="Created Time: "+createdTime;
    let pTag2=document.createElement("p");
    pTag2.innerHTML="File Size  : "+storage;

    let downloadButton = document.createElement("button");
    downloadButton.textContent = "Download✅";
    downloadButton.className = "download";
    downloadButton.onclick = function(){downloadFile(version,fileId,fileName);};    
    
    let deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete 🗑";
    deleteButton.className = "delete";
    deleteButton.onclick = function(){deleteFile(divIndex,fileId,version);};
    
    let topVersionButton = document.createElement("button");
    topVersionButton.textContent = "Set As Top Version 🔝";
    topVersionButton.className = "topVersion";
    topVersionButton.onclick = function(){setTopVersion(divIndex,fileId,version);};

    let lockButton = document.createElement("button");
    lockButton.textContent = "Lock File Version🔒";
    lockButton.className = "lock";
    lockButton.onclick = function(){lockTheVersion(divIndex,fileId,version);};
    
    let overWriteButton = document.createElement("button");
    overWriteButton.textContent = "Over Write The Version 📝";
    overWriteButton.className = "overWrite";
    overWriteButton.onclick=function(){openPopup(divIndex,fileId,version,fileName);};
    
    newDiv.appendChild(image);
    newDiv.appendChild(hTag);
    newDiv.appendChild(pTag1);
    newDiv.appendChild(pTag2);
    newDiv.appendChild(downloadButton);
    if(isLocked=="1" && divIndex!=0){
    	let label = document.createElement("label");
    	 label.textContent = " File Version Locked 🔒";
    	 label.style.backgroundColor="white";
    	 newDiv.appendChild(label);
    }
    if(divIndex==0){
    	newDiv.appendChild(overWriteButton);
    	let label = document.createElement("label");
    	 label.textContent = " Top Version 💫";
    	 label.style.backgroundColor="white";
    	 newDiv.appendChild(label);
    }
    if(divIndex!=0 && isLocked=="0"){
    newDiv.appendChild(overWriteButton);
    newDiv.appendChild(deleteButton);
    newDiv.appendChild(topVersionButton);
    newDiv.appendChild(lockButton);
    }
    let breakTag = document.createElement("br");
    if(topVersionListing==1){
    	if(divIndex==0){
    	var h2Tag=document.getElementsByTagName("h2")[0].innerHTML;
    	h2Tag=+h2Tag;
    	document.getElementsByTagName("h2")[0].innerHTML=h2Tag+1;
    	var firstDiv=document.getElementsByClassName("file")[0];
    	document.body.insertBefore(newDiv,firstDiv);
    	   var newDivButtons=newDiv.querySelectorAll("button");
    	   newDiv.after(breakTag);
    	}else{
    		var h2Tag=document.getElementsByTagName("h2")[0].innerHTML;
        	h2Tag=+h2Tag;
    		var secondDiv=document.getElementsByClassName("file")[1];
            var secondDivButtons=secondDiv.querySelectorAll("button");
            secondDivButtons[2].onclick = function(){deleteFile(1-h2Tag,fileId,version);};
            secondDivButtons[3].onclick = function(){setTopVersion(1-h2Tag,fileId,version);};
            secondDivButtons[4].onclick = function(){lockTheVersion(1-h2Tag,fileId,version);};
    	}
    }
    else{
    document.body.appendChild(newDiv);
    document.body.appendChild(breakTag);
    }
}
function openPopup(index,fileId,version,fileName) {
	var updateIndex=document.getElementsByTagName("h2")[0].innerHTML;
	updateIndex=+updateIndex;
	index=index+updateIndex;
    const HTags=document.getElementsByTagName("h6");
    HTags[0].innerHTML=fileId;
    HTags[1].innerHTML=version;
    HTags[2].innerHTML=index;
    document.getElementById("popupHeading").innerHTML=fileName+"   is being OverWrited by below choosen File";
    document.getElementById('Popup').style.display = 'block';
}

function closePopup() {
	document.getElementById('Popup').style.display="none";
}

function submitForm() {
const HTags= document.getElementsByTagName('h6');
const existFileId=HTags[0].innerHTML;
const existFileVersion=HTags[1].innerHTML;
const index=HTags[2].innerHTML;
const fileInput = document.getElementById('fileInput');
const file = fileInput.files[0];
if (!file) return alert("Please select a file!");
fetch('OverWriteServlet', {
    method: 'POST',
    headers: {
        'Content-Type': file.type,
        'fileName': encodeURIComponent(file.name),
        'existFileId': encodeURIComponent(existFileId) ,
        'existFileVersion': encodeURIComponent(existFileVersion) 
    },
    body:file
})
.then(response => response.json())
.then(data => {
	if(data.status=="Successfully OverWrited"){
	divTags=document.getElementsByClassName("file");
    var pTags=divTags[index].querySelectorAll("p");
    pTags[0].innerHTML="Created Time: "+data.createdTime;
    pTags[1].innerHTML="File Size  : "+data.fileSize;
	alert("OverWrited Successfully!");
	document.getElementById('Popup').style.display="none";
	return;
	}
	alert("File type (Extension) is Mismatched!");
})
.catch(error => console.error("Error:", error));
}

function uploadFile(){
	 let files = document.getElementById('FileInput').files;
     if (!files.length) {
         alert("Please select a file");
         return;
     }
     let fileName=files[0].name;
     var file=files[0];
     fetch('UploadFileVersionServlet?fileName=' + fileName, {
             method: 'POST',
             headers: { 'Content-Type': 'application/octet-stream+fileName=' }, 
             body:file
              })
     .then(response => response.json())
     .then(data => {
    	 if(data.status=="success"){
    	 var firstDiv=document.getElementsByClassName("file")[0];
         firstDiv.querySelectorAll("label")[0].hidden="hidden";
      
         let deleteButton = document.createElement("button");
         deleteButton.textContent = "Delete 🗑";
         deleteButton.className = "delete";
         
         let topVersionButton = document.createElement("button");
         topVersionButton.textContent = "Set As Top Version 🔝";
         topVersionButton.className = "topVersion";

         let lockButton = document.createElement("button");
         lockButton.textContent = "Lock File Version🔒";
         lockButton.className = "lock";
         
         firstDiv.appendChild(deleteButton);
         firstDiv.appendChild(topVersionButton);
         firstDiv.appendChild(lockButton);
     	  uploadedFilesList(1);
     	  alert("Uploaded Successfully");
     	  return;
     	  }
    	 alert("File Type or Extension is Mismatched!");
     })
     .catch(error => console.error('Upload error:', error));
}
     
function downloadFile(version,fileId,fileName) {
        fetch('DownloadFileFromListingServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
            	"fileId": +fileId,
            	"version":version
            	})
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
    function deleteFile(index,fileId,version) {
    	var updateIndex=document.getElementsByTagName("h2")[0].innerHTML;
    	updateIndex=+updateIndex;
    	index=index+updateIndex;
        const userData = {
            "fileId": +fileId,
            "version":+version
        };

        fetch('DeleteServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
        .then(function(response){
        	if(response.status==500) {
        		alert("Illegal Operation! ");
        		return;
        	}
        	divTags=document.getElementsByClassName("file");
        	divTags[index].hidden="hidden";
        	alert("Deleted Successfully");
        })
   
        .catch(error => console.error('Error:', error));
    }
  
    function setTopVersion(index,fileId,version) {
    	var updateIndex=document.getElementsByTagName("h2")[0].innerHTML;
    	updateIndex=+updateIndex;
    	index=index+updateIndex;
        const userData = {
            "fileId": +fileId,
            "version":+version
        };

        fetch('SetTopVersionServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
        
        .then(response=>response.json())
        .then(data=>{
        	var firstDiv=document.getElementsByClassName("file")[0];
            firstDiv.querySelectorAll("label")[0].hidden="hidden";
            
            let deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete 🗑";
            deleteButton.className = "delete";
            
            let topVersionButton = document.createElement("button");
            topVersionButton.textContent = "Set As Top Version 🔝";
            topVersionButton.className = "topVersion";

            let lockButton = document.createElement("button");
            lockButton.textContent = "Lock File Version🔒";
            lockButton.className = "lock";
            
            firstDiv.appendChild(deleteButton);
            firstDiv.appendChild(topVersionButton);
            firstDiv.appendChild(lockButton);
        	uploadedFilesList(1);
        	alert("Top Version Updated Successfully");
        	
        }).catch(error => console.error('Error:', error));
    }

    function lockTheVersion(index,fileId,version) {
    	var updateIndex=document.getElementsByTagName("h2")[0].innerHTML;
    	updateIndex=+updateIndex;
    	index=index+updateIndex;
        const userData = {
            "fileId": +fileId,
            "version":+version
        };

        fetch('LockTheVersionServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
        .then(function(response){
        	if(response.status==500) {
        		alert("Illegal Operation! ");
        		return;
        	}
        	divTags =document.getElementsByClassName("file");
        	div=divTags[index];
        	div.querySelectorAll("button")[1].remove();
        	div.querySelectorAll("button")[1].remove();
        	div.querySelectorAll("button")[1].remove();
        	div.querySelectorAll("button")[1].remove();
        	label=document.createElement("label");
        	label.innerText="  Version Locked🔒";
        	label.style.backgroundColor="white";
        	div.appendChild(label);
        	alert("Version Locked Successfully");
        })
        .catch(error => console.error('Error:', error));
    }
</script>
</body>
</html>