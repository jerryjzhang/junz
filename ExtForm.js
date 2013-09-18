Ext.define("AlcazarFailure.controller.Form", {
    extend: 'Ext.app.Controller',
    views: ['Form','FailuresGrid'],
    init: function () {
        this.control({
            '#buttonGet': {
            	click: this.onClickGet
            },
            '#buttonUpdate': {
            	click: this.onClickUpdate
            },
            '#buttonDownload': {
            	click: this.onClickDownload
            },
        });
    },
    
    onClickGet: function() {
		var splashscreen = Ext.getBody().mask('Loading data', 'splashscreen');
		var url;
		url = 'alcazar/failure/get/startDate/{startDate}/endDate/{endDate}';
		url = url.replace('{startDate}', Ext.ComponentQuery.query('#startDate')[0].getRawValue());
		url = url.replace('{endDate}', Ext.ComponentQuery.query('#endDate')[0].getRawValue());
//		var store = Ext.getCmp('failuresGrid').store;
//		store.proxy.url = url;
//		store.load();
		webCall(url, Ext.ComponentQuery.query('.failuresGrid')[0].store);
    },
    
    onClickUpdate: function() {
    	var splashscreen = Ext.getBody().mask('Updating data', 'splashscreen');
	    var url = 'alcazar/failure/update';
	    webCallForPOST(url, Ext.ComponentQuery.query('.failuresGrid')[0].store);
    },
    
    onClickDownload: function() {
		var url;
		url = 'alcazar/failure/get/file/startDate/{startDate}/endDate/{endDate}';
		url = url.replace('{startDate}', Ext.ComponentQuery.query('#startDate')[0].getRawValue());
		url = url.replace('{endDate}', Ext.ComponentQuery.query('#endDate')[0].getRawValue());
    	window.open(url, 'Download File');
    },
});


function webDownload(url){
	Ext.Ajax.request({ 
		url: url,     
	    method: 'GET',  
	    //params: {param1:p1, param2:p2},      
	    success: function(responseObject){
	        Ext.getBody().unmask();
		},      
	    failure: function(responseObject){       
			alert(responseObject);
		}
	});
}

function webCall(url, store){
	Ext.Ajax.request({ 
		url: url,     
	    method: 'GET',  
	    //params: {param1:p1, param2:p2},      
	    success: function(responseObject){
			var jsonObjs = Ext.decode(responseObject.responseText);
			store.removeAll();
	        store.loadData(jsonObjs);
	        Ext.getBody().unmask();
		},      
	    failure: function(responseObject){       
			alert(responseObject);
		}
	});
}

function webCallForPOST(url, store){
	/* Update all modified records */
	var array = store.getModifiedRecords();
	var jsonData = [];
	for (var i = 0; i < array.length; i++) {
		jsonData.push(Ext.encode(array[i].data));
	}

	Ext.Ajax.request({ 
		url: url,    
		method: 'POST',  
		//params: { foo: 'bar' },
		//jsonData: {"name":"msqeops RFB 6-2-2013","reason":null,"errorMessage":null,"errorDatetime":"2013-06-02 12:45:26.717","sourceDbName":"NYP_TDMSDBB.QC_ETL","step":"RUN_MASKING_JOB","fixNumber":null},    
	    jsonData: jsonData,  
	    success: function(responseObject){ 
			Ext.getBody().unmask();
			store.commitChanges();
			alert('Update succeeded');
		},      
	    failure: function(responseObject){         
			alert('Update failed');
		}
	});
}
