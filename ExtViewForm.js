Ext.define('AlcazarFailure.view.Form', {
    extend: 'Ext.form.Panel',
    id: "movies_form",
    alias: 'widget.moviesform',
    defaultType: 'textfield',
    items: [
        new Ext.form.DateField({
            fieldLabel: 'Start Date',
            itemId: 'startDate',
            format: 'Y-m-d'
        }),
        new Ext.form.DateField({
            fieldLabel: 'End Date',
            itemId: 'endDate',
            format: 'Y-m-d'
        }),
    ],
    buttons: [
       {
    	   text: 'Get Failures',
    	   itemId: 'buttonGet',    	
       },{
    	   text: 'Update Failures',
    	   itemId: 'buttonUpdate'
       },{
    	   text: 'Download Failures',
    	   itemId: 'buttonDownload'
       }],
    
    
    constructor: function(config) {
        Ext.applyIf(config, {
            // defaults for configs that should be passed along to the Basic form constructor go here
            trackResetOnLoad: true
        });
        this.callParent(arguments);
    }
});
