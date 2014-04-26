require([
    'jquery',
    '/schulze/api/server-api.js',
    '/schulze/voters/voters.js',
    '/schulze/util/json-over-http.js'],
    function ($, createServerApi, makeVotersComponent, jsonOverHttp) {
        'use strict';
        var options, component, serverApi;
        serverApi = createServerApi(jsonOverHttp);
        options = {
            serverApi:serverApi
        };
        component = makeVotersComponent(options);
        serverApi.setListener(component);
        $('body').append(component.dom);
        component.initialize();
    });
