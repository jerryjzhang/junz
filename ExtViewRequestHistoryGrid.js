Ext.define('AlcazarFailure.view.RequestHistoryGrid', {
    extend: 'Ext.grid.Panel',
    //id: "requestHistoryGrid",
    alias: 'widget.requestHistoryGrid',
    autoscroll: true,
    store: 'RequestHistory',
    rowEditor: Ext.create('Ext.grid.plugin.RowEditing', {
        clicksToEdit: 2
    }),
    initComponent: function () {
        this.columns = [{
            header: 'Request Name',
            dataIndex: 'name',
            editor: {
                xtype: 'textfield',
                allowBlank: true
            },
            flex: 1
        }, {
            header: 'Date Time',
            dataIndex: 'updateDatetime',
            flex: 1
        }, {
            header: 'Source Database',
            dataIndex: 'sourceDbName',
            flex: 1
        }, {
            header: 'Target Database',
            dataIndex: 'targetDbName',
            flex: 1
        }, {
            header: 'Status',
            dataIndex: 'status',
            flex: 1
        }, {
            header: 'Refresh Source',
            dataIndex: 'refreshSource',
            flex: 1
        }];
        this.plugins = [ this.rowEditor ];
        this.callParent(arguments);
    }
});
