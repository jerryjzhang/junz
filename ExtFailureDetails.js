Ext.define("AlcazarFailure.controller.FailureDetails", {
    extend: 'Ext.app.Controller',
    views: ['FailureDetails','RequestHistoryGrid'],
    models: ['RequestHistory'],
    stores: ['RequestHistory'],
    init: function () {
        this.control({
            '#buttonSave': {
                click: this.onClickSave
            },
            '#buttonViewLog': {
                click: this.onClickViewLog
            },
            '#buttonViewReqHistByName': {
            	click: this.onClickViewHistByName
            },
            '#buttonViewReqHistBySrcDb': {
            	click: this.onClickViewHistBySrcDb
            }
        });
    },
    
    onClickSave: function (btn) {
    	var failureDetail =  btn.up('.failureDetails');
    	//var failureDetail = Ext.ComponentQuery.query('.failureDetails')[0];
    	var item = failureDetail.record;
    	item.set('reason', failureDetail.down('#why').getValue());
    	item.set('rootCause', failureDetail.down('#rootCause').getValue());
    	item.set('fixNumber', failureDetail.down('#fix').getValue());
    	item.set('errorMessage', failureDetail.down('#errorMessage').getValue());
    	item.set('finalSolution', failureDetail.down('#finalSolution').getValue());
    	
    	failureDetail.collapse();
    },
    
    onClickViewLog: function () {
		var url;
		url = 'http://nfsweb-na/v/campus/vi/appl/msqe/tdms-coreserver/data/cache/jpreport/'
		url = url + Ext.ComponentQuery.query('#transactionLogFile')[0].getValue();
    	window.open(url, 'Viewing Log');
    },
    
    onClickViewHistByName: function() {
		var splashscreen = Ext.getBody().mask('Loading data', 'splashscreen');
		var url;
		url = 'alcazar/refresh/get/refresh/history/request/{requestName}';
		url = url.replace('{requestName}', Ext.ComponentQuery.query('#requestName')[0].getValue());
		webCall(url, Ext.ComponentQuery.query('.requestHistoryGrid')[0].store);
		Ext.ComponentQuery.query('#refreshHistPanel')[0].expand();		
    },
    
    onClickViewHistBySrcDb: function() {
		var splashscreen = Ext.getBody().mask('Loading data', 'splashscreen');
		var url;
		url = 'alcazar/refresh/get/refresh/history/source/server/{serverName}/dbname/{dbName}';
		var dbNames = Ext.ComponentQuery.query('#srcDb')[0].getValue().split("\.");
		url = url.replace('{serverName}', dbNames[0]);
		url = url.replace('{dbName}', dbNames[1]);
		webCall(url, Ext.ComponentQuery.query('.requestHistoryGrid')[0].store);
		Ext.ComponentQuery.query('#refreshHistPanel')[0].expand();			
    },
});
