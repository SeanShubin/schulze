var require = {
    paths: {
        'jquery': '/lib/vendor/jquery/2.0.1/jquery',
        'backbone': '/lib/vendor/backbone/1.0.0/backbone',
        'qunit': '/lib/vendor/qunit/1.11.0/qunit',
        'underscore': '/lib/vendor/underscore/1.4.4/underscore',
        'text': '/lib/vendor/require/2.1.6/text',
        'squire': '/lib/vendor/squire/0.1.0/Squire'
    },
    shim: {
        "underscore": {
            exports: "_"
        },
        backbone: {
            deps: ['underscore', 'jquery'],
            exports: 'Backbone'
        }
    }
};
