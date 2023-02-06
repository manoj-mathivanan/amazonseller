

var data;
function myFunction(arr) {
	
    data = arr.data;
    var i;
    

    
 	var totvolumeth =  document.getElementById('totalvolumeth');
	    totvolumeth.innerText = "IN_VOL: \n" + arr.totalincomingvolume;

	var totevolumeth =  document.getElementById('totalevolumeth');
	    totevolumeth.innerText = "EX_VOL: \n" + arr.totalavailablevolume;

	var totalprofitth =  document.getElementById('totalprofitth');
	    totalprofitth.innerText = "NET PROFIT: " + arr.totalprofitPercentage + "%";
	var totalprofitthclick = document.createAttribute("onclick");
        totalprofitthclick.value = 'trigger_profit_edit(' + data[0].returns + ',' + data[0].marketing + ')';
        totalprofitth.setAttributeNode(totalprofitthclick);
   
    var tabBody=document.getElementsByTagName("tbody").item(0);
	tabBody.innerHTML = "";
    for(i = 0; i < data.length; i++) {
    	

      
      var str = data[i].lastpulled;
      var lastpulledDim = str.substring(0, str.indexOf(" mins ago"));
      var classStatus=null;
      if(lastpulledDim){
        if(lastpulledDim > 59 || lastpulledDim < 0){
          classStatus = "pending";
        }
      }else{
        classStatus = "pending";
      }

      str = data[i].fbalastpulled;
      lastpulledDim = str.substring(0, str.indexOf(" mins ago"));
      var fbaStatus=null;
      if(lastpulledDim){
        if(lastpulledDim > 59 || lastpulledDim < 0){
          fbaStatus = "pending";
        }
      }else{
        fbaStatus = "pending";
      }
      
      newreviewsval = data[i].newreviews;
      totalreviewsval = data[i].totalreviews;

      var row=document.createElement("tr");

/*      var mailscope = document.createAttribute("scope");
      var mailth = document.createElement("th");
      mailscope.value = "row";
      mailth.setAttributeNode(mailscope);
      var checkbox = document.createElement("input");
      checkbox.type = "checkbox";
      checkbox.id = data[i].asin;
      if(data[i].sendmail === "yes")
       	checkbox.checked = "checked";
      checkbox.addEventListener("click", function(){ toggleMail(this); });
      mailth.appendChild(checkbox)
      row.appendChild(mailth);*/
      
      var asintd = document.createElement("td");
      var asindiv = document.createElement("div");
      var asinclass = document.createAttribute("class");
      if(classStatus){
        asinclass.value = "tm-status-circle pending";
      }else{
        if(data[i].buybox === "tick.png")
          asinclass.value = "tm-status-circle moving";
        else if(data[i].buybox === "wrong.png")
          asinclass.value = "tm-status-circle cancelled";
      }
      asindiv.setAttributeNode(asinclass);
      asintd.appendChild(asindiv);
	  var asina = document.createElement("a");
	  var asinhref = document.createAttribute("href");
	  asinhref.value = "https://www.amazon.com/dp/" + data[i].asin;
	  asina.setAttributeNode(asinhref);	
	  var asintarget = document.createAttribute("target");
	  asintarget.value = "_blank";
	  asina.setAttributeNode(asintarget);	
	  var asincolour = document.createAttribute("style");
	  asincolour.value = "color:white;";
	  asina.setAttributeNode(asincolour);		  
	  asina.appendChild(document.createTextNode(data[i].asin));
      asintd.appendChild(asina);
      if(data[i].active === false){
    	  var asinstrike = document.createAttribute("style");
    	  asinstrike.value = "text-decoration:line-through;";
    	  asintd.setAttributeNode(asinstrike);
      }
      row.appendChild(asintd);

      var skutd = document.createElement("td");
      skutd.appendChild(document.createTextNode(data[i].title));
      if(data[i].active === false){
    	  var skustrike = document.createAttribute("style");
    	  skustrike.value = "text-decoration:line-through;";
    	  skutd.setAttributeNode(skustrike);
      }
      row.appendChild(skutd);

      var lengthtd = document.createElement("td");
      var lengthdiv = document.createElement("div");
      var lengthclass = document.createAttribute("class");
      if(classStatus){
        lengthclass.value = "tm-status-circle pending";
      }else{
        if(data[i].lengthmismatch === "tick.png")
          lengthclass.value = "tm-status-circle moving";
        else if(data[i].lengthmismatch === "wrong.png")
          lengthclass.value = "tm-status-circle cancelled";
      }
      lengthdiv.setAttributeNode(lengthclass);
      lengthtd.appendChild(lengthdiv);
      lengthtd.appendChild(document.createTextNode(data[i].obtainedlength));
      if(data[i].active === false){
    	  var lengthstrike = document.createAttribute("style");
    	  lengthstrike.value = "text-decoration:line-through;";
    	  lengthtd.setAttributeNode(lengthstrike);
      }
      row.appendChild(lengthtd);

      var widthtd = document.createElement("td");
      var widthdiv = document.createElement("div");
      var widthclass = document.createAttribute("class");
      if(classStatus){
        widthclass.value = "tm-status-circle pending";
      }else{
        if(data[i].widthmismatch === "tick.png")
          widthclass.value = "tm-status-circle moving";
        else if(data[i].widthmismatch === "wrong.png")
          widthclass.value = "tm-status-circle cancelled";
      }
      widthdiv.setAttributeNode(widthclass);
      widthtd.appendChild(widthdiv);
      widthtd.appendChild(document.createTextNode(data[i].obtainedwidth));
      if(data[i].active === false){
    	  var widthstrike = document.createAttribute("style");
    	  widthstrike.value = "text-decoration:line-through;";
    	  widthtd.setAttributeNode(widthstrike);
      }
      row.appendChild(widthtd);

      var heighttd = document.createElement("td");
      var heightdiv = document.createElement("div");
      var heightclass = document.createAttribute("class");
      if(classStatus){
        heightclass.value = "tm-status-circle pending";
      }else{
        if(data[i].heightmismatch === "tick.png")
          heightclass.value = "tm-status-circle moving";
        else if(data[i].heightmismatch === "wrong.png")
          heightclass.value = "tm-status-circle cancelled";
      }
      heightdiv.setAttributeNode(heightclass);
      heighttd.appendChild(heightdiv);
      heighttd.appendChild(document.createTextNode(data[i].obtainedheight));
      if(data[i].active === false){
    	  var heightstrike = document.createAttribute("style");
    	  heightstrike.value = "text-decoration:line-through;";
    	  heighttd.setAttributeNode(heightstrike);
      }
      row.appendChild(heighttd);

      var weighttd = document.createElement("td");
        var weightdiv = document.createElement("div");
        var weightclass = document.createAttribute("class");
        if(classStatus){
          weightclass.value = "tm-status-circle pending";
        }else{
          if(data[i].weightmismatch === "tick.png")
            weightclass.value = "tm-status-circle moving";
          else if(data[i].weightmismatch === "wrong.png")
            weightclass.value = "tm-status-circle cancelled";
        }
        weightdiv.setAttributeNode(weightclass);
        weighttd.appendChild(weightdiv);
        weighttd.appendChild(document.createTextNode(data[i].obtainedweight));
        if(data[i].active === false){
          var weightstrike = document.createAttribute("style");
          weightstrike.value = "text-decoration:line-through;";
          weighttd.setAttributeNode(weightstrike);
        }
        row.appendChild(weighttd);

      var dimtimetd = document.createElement("td");
      dimtimetd.appendChild(document.createTextNode(data[i].lastpulled.substring(0, data[i].lastpulled.indexOf(" mins ago"))+" mins"));
      if(data[i].active === false){
    	  var dimtimestrike = document.createAttribute("style");
    	  dimtimestrike.value = "text-decoration:line-through;";
    	  dimtimetd.setAttributeNode(dimtimestrike);
      }
      row.appendChild(dimtimetd);


	  var volumetd = document.createElement("td");
      volumetd.appendChild(document.createTextNode(data[i].obtainedvolume + " * "));
		var textbox = document.createElement("input");
		textbox.type = "text";
		textbox.value = data[i].incomingunits;
		textbox.id = data[i].asin;
		
		textbox.style = "width:37px";
		textbox.addEventListener("focusout", function(){ updateUnits(this); });
	  	volumetd.appendChild(textbox);
		volumetd.appendChild(document.createTextNode(" = " + data[i].incomingvolume));
      if(data[i].active === false){
    	  var volumestrike = document.createAttribute("style");
    	  volumestrike.value = "text-decoration:line-through;";
    	  volumetd.setAttributeNode(volumestrike);
      }
		volumetd.width = 140;
      row.appendChild(volumetd);

	var avoltd = document.createElement("td");
      avoltd.appendChild(document.createTextNode(data[i].availablevolume));
      if(data[i].active === false){
    	  var avolstrike = document.createAttribute("style");
    	  avolstrike.value = "text-decoration:line-through;";
    	  avoltd.setAttributeNode(avolstrike);
      }
      row.appendChild(avoltd);


      var reftd = document.createElement("td");
      reftd.appendChild(document.createTextNode(data[i].ref));
      if(data[i].active === false){
    	  var refstrike = document.createAttribute("style");
    	  refstrike.value = "text-decoration:line-through;";
    	  reftd.setAttributeNode(refstrike);
      }
      row.appendChild(reftd);

      var fbatd = document.createElement("td");
      fbatd.appendChild(document.createTextNode(data[i].fbafee));
      if(data[i].active === false){
    	  var fbastrike = document.createAttribute("style");
    	  fbastrike.value = "text-decoration:line-through;";
    	  fbatd.setAttributeNode(fbastrike);
      }
      row.appendChild(fbatd);

      var fbatimetd = document.createElement("td");
            fbatimetd.appendChild(document.createTextNode(data[i].fbalastpulled.substring(0, data[i].fbalastpulled.indexOf(" mins ago"))+" mins"));
            if(data[i].active === false){
          	  var fbatimestrike = document.createAttribute("style");
          	  fbatimestrike.value = "text-decoration:line-through;";
          	  fbatimetd.setAttributeNode(fbatimestrike);
            }
            row.appendChild(fbatimetd);

      var storagetd = document.createElement("td");
      storagetd.appendChild(document.createTextNode(data[i].storagefee));
      if(data[i].active === false){
    	  var storagestrike = document.createAttribute("style");
    	  storagestrike.value = "text-decoration:line-through;";
    	  storagetd.setAttributeNode(storagestrike);
      }
      row.appendChild(storagetd);
      
      var costtd = document.createElement("td");
      costtd.appendChild(document.createTextNode(data[i].costprice));
      if(data[i].active === false){
    	  var coststrike = document.createAttribute("style");
    	  coststrike.value = "text-decoration:line-through;";
    	  costtd.setAttributeNode(coststrike);
      }
      row.appendChild(costtd);

      var sellingtd = document.createElement("td");
      sellingtd.appendChild(document.createTextNode(data[i].sellingprice));
      if(data[i].active === false){
    	  var sellingstrike = document.createAttribute("style");
    	  sellingstrike.value = "text-decoration:line-through;";
    	  sellingtd.setAttributeNode(sellingstrike);
      }
      row.appendChild(sellingtd);
      
      var profittd = document.createElement("td");
      var profitdiv = document.createElement("div");
      var profitclass = document.createAttribute("class");
      if(fbaStatus){
        profitclass.value = "tm-status-circle pending";
      }else{
        if(data[i].profitstatus === "tick.png")
          profitclass.value = "tm-status-circle moving";
        else if(data[i].profitstatus === "wrong.png")
          profitclass.value = "tm-status-circle cancelled";
      }
      profitdiv.setAttributeNode(profitclass);
      profittd.appendChild(profitdiv);
      profittd.appendChild(document.createTextNode(data[i].profit));
      if(data[i].active === false){
    	  var profitstrike = document.createAttribute("style");
    	  profitstrike.value = "text-decoration:line-through;";
    	  profittd.setAttributeNode(profitstrike);
      }
      row.appendChild(profittd);

      var profitptd = document.createElement("td");
      profitptd.appendChild(document.createTextNode(data[i].profitpercentage+"%"));
      if(data[i].active === false){
    	  var profitpstrike = document.createAttribute("style");
    	  profitpstrike.value = "text-decoration:line-through;";
    	  profitptd.setAttributeNode(profitpstrike);
      }
      row.appendChild(profitptd);
      
      
      var newreviewstd = document.createElement("td");
      var newreviewsdiv = document.createElement("div");
      var newreviewsclass = document.createAttribute("class");
      if(classStatus){
    	  newreviewsclass.value = "tm-status-circle pending";
      }else{
        if(newreviewsval > 0)
        	newreviewsclass.value = "tm-status-circle cancelled";
        else
        	newreviewsclass.value = "tm-status-circle moving";
      }
      newreviewsdiv.setAttributeNode(newreviewsclass);
      //newreviewstd.appendChild(newreviewsdiv);
      newreviewstd.appendChild(document.createTextNode(data[i].ratings + "\/" + data[i].totalreviews+ " (" +data[i].newreviews + ")" + ""));
      if(data[i].active === false){
    	  var newreviewsstrike = document.createAttribute("style");
    	  newreviewsstrike.value = "text-decoration:line-through;";
    	  newreviewstd.setAttributeNode(newreviewsstrike);
      }
      row.appendChild(newreviewstd);

      var totalreviewstd = document.createElement("td");
      totalreviewstd.appendChild(document.createTextNode(data[i].totalreviews + " T"));
      if(data[i].active === false){
    	  var totalreviewsstrike = document.createAttribute("style");
    	  totalreviewsstrike.value = "text-decoration:line-through;";
    	  totalreviewstd.setAttributeNode(totalreviewsstrike);
      }
      //row.appendChild(totalreviewstd);
      
      var reviewstd = document.createElement("td");
      var reviewsimg = document.createElement("img");
      var reviewssrc = document.createAttribute("src");
      if(newreviewsval > 0)
    	  reviewssrc.value = "readreviews.png";
      else
    	  reviewssrc.value = "";
      reviewsimg.setAttributeNode(reviewssrc);
      var reviewsclick = document.createAttribute("onclick");
      reviewsclick.value = 'readreviews(' + i + ')';
      reviewsimg.setAttributeNode(reviewsclick);
      reviewstd.appendChild(reviewsimg);
      //row.appendChild(reviewstd);
      

      var edittd = document.createElement("td");
      var editlink = document.createElement("a");
      var editclass = document.createAttribute("class");
      editclass.value = "tm-product-delete-link";
      editlink.setAttributeNode(editclass);
      var editi = document.createElement("i");
      var editiclass = document.createAttribute("class");
      editiclass.value = "far fa-edit tm-product-delete-icon";
      editi.setAttributeNode(editiclass);
	  var editiclick = document.createAttribute("onclick");
      editiclick.value = 'trigger_popup_edit(' + i + ')';
      editi.setAttributeNode(editiclick);
      editlink.appendChild(editi);
      edittd.appendChild(editlink);
      row.appendChild(edittd);

/*      var deletetd = document.createElement("td");
      var deletelink = document.createElement("a");
      var deleteclass = document.createAttribute("class");
      deleteclass.value = "tm-product-delete-link";
      deletelink.setAttributeNode(deleteclass);
      var deletei = document.createElement("i");
      var deleteiclass = document.createAttribute("class");
      deleteiclass.value = "far fa-trash-alt tm-product-delete-icon";
      deletei.setAttributeNode(deleteiclass);
	  var deleteiclick = document.createAttribute("onclick");
      deleteiclick.value = 'deleteASIN(' + data[i].asin + ')';
      deletei.setAttributeNode(deleteiclick);
      deletelink.appendChild(deletei);
      deletetd.appendChild(deletelink);
      row.appendChild(deletetd);
*/
      
      tabBody.appendChild(row);
    }
    
}
   
$(window).load(function () {
  $('.popupCloseButton').click(function(){
      $('.popup_edit').hide();
  });
  $('.profitCloseButton').click(function(){
        $('.profit_edit').hide();
    });

});


function loadData(){
	var xmlhttp = new XMLHttpRequest();
    var url = "/getStatus";
		 //var url = "sample.txt";

         xmlhttp.onreadystatechange = function() {
             if (this.readyState == 4 && this.status == 200) {
                 var myArr = JSON.parse(this.responseText);
                 myFunction(myArr);
             }
         };
         xmlhttp.open("GET", url, true);
         xmlhttp.send();
}
      loadData();   

  function toggleMail(asin){
	  
		
		var url = "/toggleMail/" + asin.id;
		
		var xhttp = new XMLHttpRequest();
		  xhttp.onreadystatechange = function() {
		    if (this.readyState == 4 && this.status == 200) {
		      alert("mail enabled");
		      location.reload(true);
		    }
		  };
		  xhttp.open("POST", url, true);
		  xhttp.setRequestHeader("Content-type", "application/json");
		  xhttp.send();
	  
  }

  function updateUnits(box){
	  
		
		var url = "/updateUnits/" + box.id + "/" + box.value;
		
		var xhttp = new XMLHttpRequest();
		  xhttp.onreadystatechange = function() {
		    if (this.readyState == 4 && this.status == 200) {
		      //ocation.reload(true);
				loadData(); 
		    }
		  };
		  xhttp.open("PUT", url, true);
		  xhttp.setRequestHeader("Content-type", "application/json");
		  xhttp.send();
	  
  }
  
    function trigger_popup_edit(i){
	  
      $('.popup_edit').show();
	  document.getElementById("asin").value = data[i].asin;
	  document.getElementById("title").value = data[i].title;
	  document.getElementById("expectedlength").value = data[i].expectedlength;
	  document.getElementById("expectedwidth").value = data[i].expectedwidth;
	  document.getElementById("expectedheight").value = data[i].expectedheight;
	  document.getElementById("expectedweight").value = data[i].expectedweight;
	  document.getElementById("shipping").value = data[i].shipping;
	  document.getElementById("sellingprice").value = data[i].sellingprice;
	  document.getElementById("productcost").value = data[i].productcost;
	  
	  
  }

   function trigger_profit_edit(returns,marketing){

        $('.profit_edit').show();

  	  document.getElementById("returns").value = returns;
  	  document.getElementById("marketing").value = marketing;

    }
    
    function readreviews(i){
    	
    	var jsonbody = {};

    	jsonbody.inputkey = "newreviews";
    	jsonbody.inputvalue = 0;
    	jsonbody.asin = data[i].asin;

    	
    	var body = JSON.stringify(jsonbody);
    	var xhttp = new XMLHttpRequest();
    	  xhttp.onreadystatechange = function() {
    		  if (this.readyState == 4){
    	    		if(this.status == 200) {
    	    			//alert("marked all new reviews as read");
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
  
  function update(){
	  
	  var jsonbody = {};
	  jsonbody.asin = document.getElementById("asin").value;
	  jsonbody.title = document.getElementById("title").value;
	  jsonbody.expectedlength = document.getElementById("expectedlength").value;
	  jsonbody.expectedwidth = document.getElementById("expectedwidth").value;
	  jsonbody.expectedheight = document.getElementById("expectedheight").value;
	  jsonbody.expectedweight = document.getElementById("expectedweight").value;
	  jsonbody.sellingprice = document.getElementById("sellingprice").value;
	  jsonbody.shipping = document.getElementById("shipping").value;
	  jsonbody.productcost = document.getElementById("productcost").value;
	  $('.popup_edit').hide();
	  
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
	  xhttp.open("PUT", "/updateASINDetails", true);
	  xhttp.setRequestHeader("Content-type", "application/json");
	  xhttp.send(body);
	  
	
  }

    function updateProfit(){

  	  var jsonbody = {};
  	  jsonbody.returns = document.getElementById("returns").value;
  	  jsonbody.marketing = document.getElementById("marketing").value;

  	  $('.profit_edit').hide();

  	var body = JSON.stringify(jsonbody);
  	var xhttp = new XMLHttpRequest();
  	  xhttp.onreadystatechange = function() {
  		  if (this.readyState == 4){
  	    		if(this.status == 200) {
  	    			alert("updated");
  	  	      location.reload(true);
  	  	    }else{
  	  	    	alert("update profit failed");
  	  	    }
  	  	    }
  	  };
  	  xhttp.open("PUT", "/updateProfitDetails", true);
  	  xhttp.setRequestHeader("Content-type", "application/json");
  	  xhttp.send(body);


    }



  function deleteASIN2(){
	  
	  var asin = document.getElementById("asin").value;
	  var url = "/deleteASIN/" + asin;
		
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
  
function addASIN(){

	var asin = document.getElementsByName("addasin")[0].value;
	
	var url = "/addASIN/" + asin;
	
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
		  if (this.readyState == 4){
	    		if(this.status == 200) {
	    			alert("added. Please wait till the values to be updated for the asin to be visible");
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

function deleteASIN(deleteasin){
	
	var url = "/deleteASIN/" + deleteasin.id;
	
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

function setlogin(){
	
	var username = document.getElementsByName("username")[0].value;
	var password = document.getElementsByName("password")[0].value;
	
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
		  if (this.readyState == 4){
	    		if(this.status == 200) {
	    			alert(this.responseText);
	  	    	}else{
	  	    		alert(this.responseText);
	  	    	}
	  	    }
	  };
	  xhttp.open("PUT", "/login/"+username + "/"+password, true);
	  xhttp.send();
	 
}

function setotp(){
	
	var otp = document.getElementsByName("otp")[0].value;
	
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
		  if (this.readyState == 4){
	    		if(this.status == 200) {
	    			alert(this.responseText);
	  	    	}else{
	  	    		alert(this.responseText);
	  	    	}
	  	    }
	  };
	  xhttp.open("PUT", "/setotp/"+otp , true);
	  xhttp.send();
	 
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

function setverifiedEmail(){
	
	
	
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
		  if (this.readyState == 4){
	    		alert(this.responseText);
	  	    }
	  };
	  xhttp.open("PUT", "/setverifiedEmail" , true);
	  xhttp.send();
	 
}

window.onload = function() {
	
	

}

function getPreviousDate() {
    var d = new Date(),
        month = '' + (d.getMonth() - 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) 
        month = '0' + month;
    if (day.length < 2) 
        day = '0' + day;

    return [year, month, day].join('-');
}

function getCurrentDate() {
    var d = new Date(),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) 
        month = '0' + month;
    if (day.length < 2) 
        day = '0' + day;

    return [year, month, day].join('-');
}

var sales_data = [];

  var current_sku;
  
function products_change(val) {
	document.getElementById("chartContainer").style.visibility = "hidden";
	current_sku = val;	
	//load_chart();
}

var d = new Date();
var startdate = getPreviousDate();
var enddate = getCurrentDate();

function startdate_change(val) {
	  startdate = val;
	  //load_chart();
	}

function enddate_change(val) {
	  enddate = val;
	  //load_chart();
	}

var chart;

function load_chart () {
	
	 var xmlhttp = new XMLHttpRequest();
	    var url = "/getOrders/"+current_sku+"/"+startdate+"/"+enddate;

	    xmlhttp.onreadystatechange = function() {
	        if (this.readyState == 4 && this.status == 200) {
	            var myArr = JSON.parse(this.responseText);
	            var i=0;
	            var data = [];
	            for(i = 0; i < myArr.length; i++) {
	            	data.push({
	            		x: new Date(myArr[i].date),
	            		y: myArr[i].quantity
	            			});
	            	
	            }
	            sales_data = data;
	            document.getElementById("chartContainer").style.visibility = "";
	        	
	        	chart = new CanvasJS.Chart("chartContainer",
	        		    {

	        		      title:{
	        		      text: "Sales of " + current_sku
	        		      },
	        		      axisX: {
	        		        intervalType: "month"
	        		      },
	        		      axisY:{
	        		        includeZero: false

	        		      },
	        		      data: [
	        		      {
	        		        type: "line",

	        		        dataPoints: sales_data
	        		      }
	        		      ]
	        		    });
	        		    chart.render();
	        }
	    };
	    xmlhttp.open("GET", url, true);
	    xmlhttp.send();
	    
	    
	
  }