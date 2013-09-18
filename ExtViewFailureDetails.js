Ext.define('AlcazarFailure.view.FailureDetails', {
    extend: 'Ext.form.Panel',
    //id: "failureDetails",
    alias: 'widget.failureDetails',
    items: [
        {
        	xtype:'textfield',
        	itemId:'requestName',
        	fieldLabel: 'Request Name',
        	readOnly: true,
        	width: 400,
        },{
            xtype     : 'button',
            itemId      : 'buttonViewReqHistByName',
            text: 'View Refresh History',
            anchor    : '20%'
        },{
        	xtype:'textfield',
        	itemId:'dateTime',
        	fieldLabel: 'Date Time',
        	readOnly: true,
        	width: 400,
        },{
        	xtype:'textfield',
        	itemId:'srcDb',
        	fieldLabel: 'Source Database',
        	readOnly: true,
        	width: 400,
        }, {
            xtype     : 'button',
            itemId      : 'buttonViewReqHistBySrcDb',
            text: 'View Refresh History',
            anchor    : '20%'
        },{
            xtype     : 'textareafield',
            grow      : true,
            itemId      : 'tgtDb',
            fieldLabel: 'Target Databases',
            readOnly: true,
            anchor    : '100%'
        },{
        	xtype:'textfield',
        	itemId:'step',
        	fieldLabel: 'Failed Step',
        	readOnly: true,
        	width: 400,
        },{
        	xtype:'textfield',
        	itemId:'refreshSource',
        	fieldLabel: 'Refresh Source',
        	readOnly: true,
        	width: 400,
        },{
        	xtype:'textfield',
        	itemId:'transactionLogFile',
        	fieldLabel: 'Tranasction Logfile',
        	readOnly: true,
        	width: 500,
        },{
            xtype     : 'button',
            itemId      : 'buttonViewLog',
            text: 'View Log',
            anchor    : '20%'
        },{
            xtype     : 'combobox',
            grow      : true,
            itemId      : 'why',            
            anchor    : '100%',
            fieldLabel: 'Error Type',
            hiddenName: 'type',
            store: new Ext.data.SimpleStore({
                fields: ['type'],
                data : [['Syts Transient Issue'], ['Client DB Permission Issue'], ['Client DB Connection Issue'], ['Client DB Schema Issue'], ['DM Staging Connection Issue'], ['Other']] 
            }),
            displayField: 'type',
            typeAhead: true,
            mode: 'local',
            triggerAction: 'all',
            emptyText:'Choose Error Type',
            selectOnFocus:true,
        },{
            xtype     : 'textareafield',
            grow      : true,
            itemId      : 'rootCause',
            fieldLabel: 'Root Cause',
            anchor    : '100%'
        },{
            xtype     : 'textareafield',
            grow      : true,
            itemId      : 'finalSolution',
            fieldLabel: 'Final Solution',
            anchor    : '100%'
        },{
            xtype     : 'textareafield',
            grow      : true,
            itemId      : 'fix',
            fieldLabel: '#Bug or #Enhancement',
            anchor    : '100%'
        },{
            xtype     : 'textareafield',
            grow      : true,
            itemId      : 'errorMessage',
            fieldLabel: 'Error Message',
            anchor    : '100%'
        },{
            xtype     : 'button',
            itemId      : 'buttonSave',
            text: 'Save',
            anchor    : '20%'
        },
    ],   
    
    constructor: function(config) {
        Ext.applyIf(config, {
            // defaults for configs that should be passed along to the Basic form constructor go here
            trackResetOnLoad: true
        });
        this.callParent(arguments);
    }
});
