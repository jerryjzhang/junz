Ext.Loader.setConfig({enabled:true});

Ext.application({
    name: 'AlcazarFailure',
    appFolder: "AlcazarFailure/app",
    controllers: ['Failures', 'Form', 'FailureDetails'],
    //requires: ['AlcazarFailure.view.RequestHistoryGrid'],
    launch: function() {
	   Ext.create('ms.ext.ux.Header', {
		    application: 'Alcazar Refresh Failures Tracker',
		    renderTo: Ext.getBody(),
//		    menu: {
//		      items: { text: 'Getting Started' }
//		    },
//		    toolbar: [ {text: 'Toolbar Item', route: '/foo/bar'} ]
		  });
	   Ext.create('Ext.panel.Panel', {
		    renderTo: Ext.getBody(),
		    width: 1600,
		    height: 800,
		    layout: 'border',
		    items: [
		        {
		        	xtype: 'panel',
		        	title: 'Failure List',
		        	region: 'center',
		        	items: [
						{
							xtype: 'moviesform',
						    height: 150,
						    width: 300,
						},
						{
							xtype: 'failuresGrid',
						    height: 600,
						    width: 1500,
						},     
		        	],
		        }, {
					xtype: 'failureDetails',
		        	title: 'Failure Details',
		        	collapsible: true,
		        	collapsed: true,
		        	region: 'east',
				    width: 800,
				}, {
					itemId: 'refreshHistPanel',
					xtype: 'panel',
		        	title: 'Refresh History (Last 10 refreshes)',
		        	collapsible: true,
		        	collapsed: true,
		        	region: 'south',
				    width: 800,
				    items: [
				        {
							xtype: 'requestHistoryGrid',
						    height: 250,
						    width: 1500,
				        },
				    ],
				},
		        
		    ]
		});
    }
});
