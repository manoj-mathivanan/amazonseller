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
        imgbu.src = data[i].profitstatus;
        cell2.appendChild(imgbu);
        row1.appendChild(cell2);
        
        ohc = document.createElement("td");
        ohc.appendChild(document.createTextNode(data[i].fbafee));
        row1.appendChild(ohc);
        
        owc = document.createElement("td");
        owc.appendChild(document.createTextNode(data[i].storagefee));
        row1.appendChild(owc);
        
        olc = document.createElement("td");
        olc.appendChild(document.createTextNode(data[i].sellingprice));
        row1.appendChild(olc);
        
        owc = document.createElement("td");
        owc.appendChild(document.createTextNode(data[i].productcost));
        row1.appendChild(owc);
        
        shipping = document.createElement("td");
        shipping.appendChild(document.createTextNode(data[i].shipping));
        row1.appendChild(shipping);
        
        profit = document.createElement("td");
        profit.appendChild(document.createTextNode(data[i].profit));
        row1.appendChild(profit);
        
        profitpercentage = document.createElement("td");
        profitpercentage.appendChild(document.createTextNode(data[i].profitpercentage));
        row1.appendChild(profitpercentage);
        
        tabBody.appendChild(row1);
       
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
