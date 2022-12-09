var data;
function myFunction(arr) {
	
    data = arr.data;
    var i;
   
    var tabBody=document.getElementsByTagName("tbody").item(0);
    for(i = 0; i < data.length; i++) {
    	
    	var att1 = document.createAttribute("rowspan");
    	att1.value = 1;
        row1=document.createElement("tr");
        cell1 = document.createElement("td");
        cell1.setAttributeNode(att1);
        var asinlink = document.createElement("a");
        asinlink.href = "https://www.amazon.com/dp/" + data[i].asin;
        asinlink.innerText = data[i].asin;
        asinlink.target="_blank"
        cell1.appendChild(asinlink);
        row1.appendChild(cell1);
        
        var att2 = document.createAttribute("rowspan");
    	att2.value = 1;
        cell2 = document.createElement("td");
        cell2.setAttributeNode(att2);
        cell2.appendChild(document.createTextNode(data[i].title));
        row1.appendChild(cell2);
        
        var att21 = document.createAttribute("rowspan");
    	att21.value = 1;
        cell2 = document.createElement("td");
        cell2.setAttributeNode(att21);
        var imgbu = document.createElement('img');
        imgbu.src = data[i].buybox;
        cell2.appendChild(imgbu);
        row1.appendChild(cell2);
        
        ohc = document.createElement("td");
        ohc.appendChild(document.createTextNode(data[i].obtainedheight));
        row1.appendChild(ohc);
        
        var vh = document.createAttribute("rowspan");
    	vh.value = 1;
        vhc = document.createElement("td");
        vhc.setAttributeNode(vh);
        var imgh = document.createElement('img');
        imgh.src = data[i].heightmismatch;
        vhc.appendChild(imgh);
        row1.appendChild(vhc);
        
        owc = document.createElement("td");
        owc.appendChild(document.createTextNode(data[i].obtainedwidth));
        row1.appendChild(owc);
        
        var vw = document.createAttribute("rowspan");
    	vw.value = 1;
        vwc = document.createElement("td");
        vwc.setAttributeNode(vw);
        var imgw = document.createElement('img');
        imgw.src = data[i].widthmismatch;
        vwc.appendChild(imgw);
        row1.appendChild(vwc);
        
        olc = document.createElement("td");
        olc.appendChild(document.createTextNode(data[i].obtainedlength));
        row1.appendChild(olc);
        
        var vl = document.createAttribute("rowspan");
    	vl.value = 1;
        vlc = document.createElement("td");
        vlc.setAttributeNode(vl);
        var imgl = document.createElement('img');
        imgl.src = data[i].lengthmismatch;
        vlc.appendChild(imgl);
        row1.appendChild(vlc);
        
        owc = document.createElement("td");
        owc.appendChild(document.createTextNode(data[i].obtainedweight));
        row1.appendChild(owc);
        
        var vwe = document.createAttribute("rowspan");
    	vwe.value = 1;
        vwec = document.createElement("td");
        vwec.setAttributeNode(vwe);
        var imgwe = document.createElement('img');
        imgwe.src = data[i].weightmismatch;
        vwec.appendChild(imgwe);
        row1.appendChild(vwec);
        
        var att3 = document.createAttribute("rowspan");
    	att3.value = 1;
        cell3 = document.createElement("td");
        cell3.setAttributeNode(att3);
        cell3.appendChild(document.createTextNode(data[i].lastpulled))
        row1.appendChild(cell3);
        tabBody.appendChild(row1);
        
        var att4 = document.createAttribute("rowspan");
    	att4.value = 1;
        cell3 = document.createElement("td");
        cell3.setAttributeNode(att4);
        var checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.id = data[i].asin;
        if(data[i].sendmail === "yes")
        	checkbox.checked = "checked";
        checkbox.addEventListener("click", function(){ toggleMail(this); });
        cell3.appendChild(checkbox)
        row1.appendChild(cell3);
        tabBody.appendChild(row1);

        
        //row2=document.createElement("tr");
        
        
        
        //tabBody.appendChild(row2);
       
    }
    
}
   

         var xmlhttp = new XMLHttpRequest();
         var url = "/getStatus";

         xmlhttp.onreadystatechange = function() {
             if (this.readyState == 4 && this.status == 200) {
                 var myArr = JSON.parse(this.responseText);
                 myFunction(myArr);
             }
         };
         xmlhttp.open("GET", url, true);
         xmlhttp.send();

  function toggleMail(asin){
	  
		
		var url = "/toggleMail/" + asin.id;
		
		var xhttp = new XMLHttpRequest();
		  xhttp.onreadystatechange = function() {
		    if (this.readyState == 4 && this.status == 200) {
		      alert("added");
		      location.reload(true);
		    }
		  };
		  xhttp.open("POST", url, true);
		  xhttp.setRequestHeader("Content-type", "application/json");
		  xhttp.send();
	  
  }
function addASIN(){

	var asin = document.getElementsByName("asin")[0].value;
	
	var url = "/addASIN/" + asin;
	
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
		  if (this.readyState == 4){
	    		if(this.status == 200) {
	    			alert("added");
	  	      location.reload(true);
	  	    }else{
	  	    	alert("add failed");
	  	    }
	  	    }
	  };
	  xhttp.open("POST", url, true);
	  xhttp.setRequestHeader("Content-type", "application/json");
	  xhttp.send();
	 
}

function deleteASIN(){
	
	var deleteasin = document.getElementsByName("deleteasin")[0].value;
	
	var url = "/deleteASIN/" + deleteasin;
	
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
		  if (this.readyState == 4){
	    		if(this.status == 200) {
	    			alert("deleted");
	  	      location.reload(true);
	  	    }else{
	  	    	alert("delete failed");
	  	    }
	  	    }
	  };
	  xhttp.open("DELETE", url, true);
	  
	  xhttp.send();
	 
}

function updateASIN(){
	
	var updateasin = document.getElementsByName("updateasin")[0].value;
	
	var jsonbody = {};

	var e = document.getElementById("inputkey");
	var str = e.options[e.selectedIndex].text;
	jsonbody.inputkey = str;
	jsonbody.inputvalue = document.getElementsByName("inputvalue")[0].value;
	jsonbody.asin = document.getElementsByName("updateasin")[0].value;

	
	var body = JSON.stringify(jsonbody);
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
		  if (this.readyState == 4){
	    		if(this.status == 200) {
	    			alert("updated");
	  	      location.reload(true);
	  	    }else{
	  	    	alert("update failed");
	  	    }
	  	    }
	  };
	  xhttp.open("PUT", "/updateASIN", true);
	  xhttp.setRequestHeader("Content-type", "application/json");
	  xhttp.send(body);
	 
}

function refresh(){
	
	var xmlhttp = new XMLHttpRequest();
    var url = "/refresh";
    
    alert("Triggered background refresh. Please wait for the page to reload automatically.");

    xmlhttp.onreadystatechange = function() {
    	if (this.readyState == 4){
    		if(this.status == 200) {
  	      location.reload(true);
  	    }else{
    	      location.reload(true);
  	    }
  	    }
    };
    xmlhttp.open("GET", url, true);
    xmlhttp.send();
	 
}
