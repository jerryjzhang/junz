Ext.define('AlcazarFailure.view.FailuresGrid', {
    extend: 'Ext.grid.Panel',
    id: "failuresGrid",
    alias: 'widget.failuresGrid',
    autoscroll: true,
    store: 'Failures',
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
            dataIndex: 'errorDatetime',
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
            header: 'Step',
            dataIndex: 'step',
            flex: 1
        }, {
            header: 'Refresh Source',
            dataIndex: 'refreshSource',
            flex: 1
        },{
            header: 'Error Type',
            dataIndex: 'reason',
            flex: 1
        }, {
            header: 'Root Cause',
            dataIndex: 'rootCause',
            flex: 1
        }, {
            header: 'Final Solution',
            dataIndex: 'finalSolution',
            flex: 1
        }, {
            header: '#Bug/Enhancement',
            dataIndex: 'fixNumber',
            flex: 1
        }, {
            header: 'Error Message',
            dataIndex: 'errorMessage',
            flex: 1
        }];
        this.plugins = [ this.rowEditor ];
        this.callParent(arguments);
    }
});
