Ext.define("AlcazarFailure.controller.Failures", {
    extend: 'Ext.app.Controller',
    views: ['FailuresGrid', 'FailureDetails'],
    models: ['Failure'],
    stores: ['Failures'],
    init: function () {
        this.control({
            '#failuresGrid': {
                render: this.onEditorRender,
                itemclick: this.onItemClick
            }
        });
    },

    onEditorRender: function () {
        console.log("movies editor was rendered");
    },
    
    onItemClick: function () {
    	var item = arguments[1];
    	var failureDetails = Ext.ComponentQuery.query('.failureDetails')[0];
    	/*There are several ways to select ExtJs components:
    	 * 1. Ext.getCmp('<id>')
    	 * 2. Ext.ComponentQuery.query('#<itemId>/<xtype>') 
    	 * 3. component.down() / component.up() 
    	 * Ext.getCmp() is not recommended for production though */
    	failureDetails.down('#requestName').setValue(item.get('name'));    	
    	failureDetails.down('#dateTime').setValue(item.get('errorDatetime'));
    	failureDetails.down('#srcDb').setValue(item.get('sourceDbName'));
    	failureDetails.down('#tgtDb').setValue(item.get('targetDbName'));
    	failureDetails.down('#step').setValue(item.get('step'));
    	failureDetails.down('#refreshSource').setValue(item.get('refreshSource'));
    	failureDetails.down('#why').setValue(item.get('reason'));
    	failureDetails.down('#rootCause').setValue(item.get('rootCause'));
    	failureDetails.down('#fix').setValue(item.get('fixNumber'));
    	Ext.ComponentQuery.query('.failureDetails > #errorMessage')[0].setValue(item.get('errorMessage'));
    	Ext.ComponentQuery.query('.failureDetails > #finalSolution')[0].setValue(item.get('finalSolution'));
    	Ext.ComponentQuery.query('.failureDetails > #transactionLogFile')[0].setValue(item.get('transactionLogFile'));
    	
    	var failureDetail = Ext.ComponentQuery.query('.failureDetails')[0];
    	
    	failureDetail.record = item;   	
    	failureDetail.expand();
    }
});
